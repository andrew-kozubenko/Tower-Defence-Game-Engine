package ru.nsu.t4werok.towerdefence.net;

import ru.nsu.t4werok.towerdefence.controller.SceneController;
import ru.nsu.t4werok.towerdefence.controller.game.GameController;
import ru.nsu.t4werok.towerdefence.controller.menu.LobbyController;
import ru.nsu.t4werok.towerdefence.net.protocol.NetMessage;
import ru.nsu.t4werok.towerdefence.net.protocol.NetMessageType;

import java.io.*;
import java.net.Socket;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Сетевой клиент кооператива (TCP).
 * Поддерживает лобби и синхронизацию действий в игре.
 */
public class MultiplayerClient extends Thread implements NetworkSession {

    /* ---------------- config ---------------- */
    private static final Path SD_DIR = Paths.get(System.getProperty("user.home"),
            "Documents", "Games", "TowerDefenceSD");

    private final String hostIp;
    private final int    port;
    private final String nickname;

    /* ---------------- net ---------------- */
    private Socket        socket;
    private BufferedReader in;
    private PrintWriter    out;

    /* ---------------- refs ---------------- */
    private volatile LobbyController lobby;
    private volatile GameController  game;
    private volatile SceneController scene;

    /* ---------------- state ---------------- */
    private final List<String> players = new CopyOnWriteArrayList<>();
    private volatile boolean   running = true;

    public MultiplayerClient(String ip, int port, String nick) {
        this.hostIp = ip; this.port = port; this.nickname = nick;
        setDaemon(true);
        setName("TD-Client");
    }

    /* ================= life-cycle ================= */

    public void connect() throws IOException {
        socket = new Socket(hostIp, port);
        in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter   (new OutputStreamWriter(socket.getOutputStream()), true);

        /* зарегистрируемся в «глобальном» контексте */
        LocalMultiplayerContext.get().registerSession(this);

        /* HELLO */
        send(new NetMessage(NetMessageType.HELLO, Map.of("name", nickname)));

        start();
    }
    public void disconnect(){ close(); }

    /* ========= DI ========= */
    public void setLobbyController(LobbyController l){ this.lobby = l; }
    public void injectSceneController(SceneController sc){ this.scene = sc; }
    public void attachGameController(GameController g){
        this.game = g;
        g.setNetworkSession(this);
    }
    public List<String> getConnectedPlayers(){ return new ArrayList<>(players); }

    /* ================= main RX loop ================= */
    @Override public void run() {
        try {
            String line;
            while (running && (line = in.readLine()) != null) {
                NetMessage msg = NetMessage.fromJson(line);
                switch (msg.getType()) {
                    /* ---------- lobby ---------- */
                    case PLAYERS -> {
                        players.clear();
                        ((List<?>) msg.get("players")).forEach(p -> players.add(String.valueOf(p)));
                    }
                    case START -> handleStart(msg);

                    /* ---------- game ----------- */
                    case PLACE_TOWER -> {
                        if (game == null) break;
                        game.placeTowerRemote(msg.get("tower"),
                                (Integer) msg.get("x"), (Integer) msg.get("y"));
                    }

                    /* ---------- waves ---------- */
                    case WAVE_START, ENEMY_SPAWN, BASE_HP -> {
                        /* сразу отдаём движку */
                        LocalMultiplayerContext.get().dispatch(msg);
                    }

                    default -> {}
                }
            }
        } catch (IOException ignored) { }
        finally { close(); }
    }

    /* ================= START ================= */
    private void handleStart(NetMessage msg){
        String map  = msg.get("map");
        String data = msg.get("data");
        try {
            unpackToSd(Base64.getDecoder().decode(data));
            if (lobby != null) lobby.onGameStartSignal(map);
            else if (scene != null)
                scene.startMultiplayerGame(Paths.get(map), /*isHost=*/false);
        } catch (IOException e){ e.printStackTrace(); }
    }
    private void unpackToSd(byte[] zip) throws IOException{
        if(!Files.exists(SD_DIR)) Files.createDirectories(SD_DIR);
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zip))){
            ZipEntry e; while((e=zis.getNextEntry())!=null){
                Path dst = SD_DIR.resolve(e.getName());
                Files.createDirectories(dst.getParent());
                try(OutputStream o = Files.newOutputStream(dst,
                        StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING)){
                    zis.transferTo(o);
                }
            }
        }
    }

    /* ================= NetworkSession ================= */
    @Override public void sendPlaceTower(String tower,int x,int y){
        send(new NetMessage(NetMessageType.PLACE_TOWER,
                Map.of("tower",tower,"x",x,"y",y)));
    }
    private void send(NetMessage m){ if(out!=null) out.println(m.toJson()); }

    @Override public boolean isConnected(){ return socket!=null && socket.isConnected();}
    @Override public boolean isHost(){ return false; }

    /* ================= cleanup ================= */
    @Override public void close(){
        running=false;
        try{if(socket!=null)socket.close();}catch(IOException ignored){}
    }
}
