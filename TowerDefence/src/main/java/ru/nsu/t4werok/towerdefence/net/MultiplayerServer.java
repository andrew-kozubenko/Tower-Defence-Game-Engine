package ru.nsu.t4werok.towerdefence.net;

import ru.nsu.t4werok.towerdefence.controller.SceneController;
import ru.nsu.t4werok.towerdefence.controller.game.GameController;
import ru.nsu.t4werok.towerdefence.model.game.entities.enemy.Enemy;
import ru.nsu.t4werok.towerdefence.model.game.entities.tower.Tower;
import ru.nsu.t4werok.towerdefence.net.protocol.NetMessage;
import ru.nsu.t4werok.towerdefence.net.protocol.NetMessageType;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * TCP-хост: лобби + ретрансляция событий игры.
 */
public class MultiplayerServer extends Thread implements NetworkSession {

    /* ---------------- config ---------------- */
    private static final Path SD_DIR = Paths.get(System.getProperty("user.home"),
            "Documents", "Games", "TowerDefenceSD");

    private final int    port;
    private final String mapPath;           // выбранная хостом карта
    private final GameController controller;// null до старта
    private SceneController scene;          // set by LobbyController

    /* ---------------- net ---------------- */
    private final ServerSocket serverSocket;
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private final List<String>        names   = new CopyOnWriteArrayList<>();
    private final AtomicLong             towerIds = new AtomicLong(1);   // уникальные id башен
    private volatile boolean running = true;

    /* ---------------- ctor ---------------- */
    public MultiplayerServer(int port,String map) throws IOException{
        this(port,null,map);
    }
    public MultiplayerServer(int port,GameController c,String map) throws IOException{
        this.port=port; this.controller=c; this.mapPath=map;
        serverSocket=new ServerSocket(port);
        setDaemon(true); setName("TD-Server");

        /* регистрируемся в контексте */
        LocalMultiplayerContext.get().registerSession(this);
    }
    public void injectSceneController(SceneController sc){ this.scene = sc; }

    /* ---------------- run ---------------- */
    @Override public void run(){
        try{
            while(running){
                Socket s = serverSocket.accept();
                clients.add(new ClientHandler(s));
            }
        }catch(IOException ignored){} finally{ close(); }
    }

    /* ==================== START ==================== */
    public void startGame(){
        try{
            byte[] zip = buildAssetsZip();
            String blob= Base64.getEncoder().encodeToString(zip);

            NetMessage start = new NetMessage(NetMessageType.START,
                    Map.of("map",mapPath,"data",blob));
            broadcast(start);

            unpackToSd(zip);
            if(scene!=null)
                scene.startMultiplayerGame(Paths.get(mapPath), true);
        }catch(IOException e){ e.printStackTrace(); }
    }

    /* ================= NetworkSession ================= */
    @Override public void sendPlaceTower(String tower,int x,int y){
        long id = towerIds.getAndIncrement();
        /* локально для хоста */
        LocalMultiplayerContext.get().dispatch(
                new NetMessage(NetMessageType.PLACE_TOWER,
                        Map.of("tower",tower,"x",x,"y",y)));

        /* клиентам */
        broadcast(new NetMessage(NetMessageType.PLACE_TOWER,
                Map.of("tower",tower,"x",x,"y",y)));
    }

    @Override public void sendUpgradeTower(String name, Integer damage, Double fireRate, Double attackRadius,
                                           List<String> upgrades, int x, int y){
        long id = towerIds.getAndIncrement();
        /* локально для хоста */
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", name);
        payload.put("damage", damage);
        payload.put("fireRate", fireRate);
        payload.put("attackRadius", attackRadius);
        payload.put("upgrades", upgrades);
        payload.put("x", x);
        payload.put("y", y);

        LocalMultiplayerContext.get().dispatch(
                new NetMessage(NetMessageType.UPGRADE_TOWER, payload));

        /* клиентам */
        broadcast(new NetMessage(NetMessageType.UPGRADE_TOWER, payload));
    }

    @Override
    public void sendSellTower(int x, int y) {
        // Локальная обработка (для хоста)
        LocalMultiplayerContext.get().dispatch(
                new NetMessage(NetMessageType.SELL_TOWER,
                        Map.of("x", x, "y", y)));

        // Отправка всем клиентам
        broadcast(new NetMessage(NetMessageType.SELL_TOWER,
                Map.of("x", x, "y", y)));
    }

    /* --- новые вспомогательные методы для волн --- */

    public void sendWaveSync(int idx,long seed){
        broadcast(new NetMessage(NetMessageType.WAVE_SYNC, Map.of("idx",idx,"seed",seed)));
    }
    public void sendStateSync(int hp, List<Enemy> enemies, int waveIdx){
        var arr = enemies.stream().map(e ->
                Map.of("x",e.getX(),"y",e.getY(),
                        "path",e.getCurrentPathIndex(),
                        "hp",e.getLifePoints())
        ).toList();
        broadcast(new NetMessage(NetMessageType.STATE_SYNC,
                Map.of("hp",hp,"wave",waveIdx,"data",arr)));
    }

    @Override public boolean isConnected(){ return !serverSocket.isClosed(); }
    @Override public boolean isHost(){ return true; }

    /* ================= house-keeping ================= */
    @Override public void close(){
        running=false;
        try{serverSocket.close();}catch(IOException ignored){}
        clients.forEach(ClientHandler::close);
        clients.clear(); names.clear();
    }

    /* ================= utils ================= */
    private void broadcast(NetMessage m){ clients.forEach(c->c.send(m)); }
    private void sendPlayers(){ broadcast(new NetMessage(NetMessageType.PLAYERS,
            Map.of("players",names))); }

    /* ---------------- zip helpers ---------------- */
    private byte[] buildAssetsZip() throws IOException{
        Path root=Paths.get("").toAbsolutePath();
        List<Path> dirs=List.of("maps","towers","waves","enemy","settings","techTree","image")
                .stream().map(root::resolve).toList();

        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        try(ZipOutputStream zos=new ZipOutputStream(baos)){
            for(Path d:dirs) if(Files.exists(d))
                Files.walk(d).filter(Files::isRegularFile).forEach(p->{
                    try(InputStream in=Files.newInputStream(p)){
                        zos.putNextEntry(new ZipEntry(root.relativize(p).toString().replace("\\","/")));
                        in.transferTo(zos); zos.closeEntry();
                    }catch(IOException e){ throw new UncheckedIOException(e);}
                });
            /* карта отдельно, если вне /maps */
            Path map=Paths.get(mapPath);
            if(!map.startsWith(root.resolve("maps")) && Files.exists(map)){
                try(InputStream in=Files.newInputStream(map)){
                    zos.putNextEntry(new ZipEntry("maps/"+map.getFileName()));
                    in.transferTo(zos); zos.closeEntry();
                }
            }
        }
        return baos.toByteArray();
    }
    private void unpackToSd(byte[] zip) throws IOException{
        if(!Files.exists(SD_DIR)) Files.createDirectories(SD_DIR);
        try(java.util.zip.ZipInputStream zis =
                    new java.util.zip.ZipInputStream(new ByteArrayInputStream(zip))){
            ZipEntry e; while((e=zis.getNextEntry())!=null){
                Path dst=SD_DIR.resolve(e.getName());
                Files.createDirectories(dst.getParent());
                try(OutputStream o=Files.newOutputStream(dst,StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING)){
                    zis.transferTo(o);
                }
            }
        }
    }

    /* =======================================================
                          ClientHandler
       ======================================================= */
    private class ClientHandler extends Thread{
        private final Socket sock;
        private final BufferedReader in;
        private final PrintWriter  out;
        private String nick="Unknown";

        ClientHandler(Socket s) throws IOException{
            this.sock=s;
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out= new PrintWriter  (new OutputStreamWriter(s.getOutputStream()),true);
            setDaemon(true); start();
        }
        void send(NetMessage m){ out.println(m.toJson()); }

        @Override public void run(){
            try{
                String line;
                while((line=in.readLine())!=null){
                    NetMessage msg=NetMessage.fromJson(line);
                    switch(msg.getType()){
                        case HELLO -> { nick=msg.get("name"); names.add(nick); sendPlayers(); }
                        case PLACE_TOWER, SELL_TOWER, UPGRADE_TOWER -> {
                            broadcast(msg);                  // всем остальным
                            LocalMultiplayerContext.get().dispatch(msg); // локально хосту
                        }
                        case WAVE_REQ -> LocalMultiplayerContext.get().dispatch(msg);
                        default -> {}
                    }
                }
            }catch(IOException ignored){}
            finally{
                names.remove(nick); sendPlayers(); close(); clients.remove(this);
            }
        }
        void close(){ try{sock.close();}catch(IOException ignored){} }
    }
}
