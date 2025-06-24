/* src/main/java/ru/nsu/t4werok/towerdefence/net/NetworkSession.java */
package ru.nsu.t4werok.towerdefence.net;

/**
 * Общий контракт для сервер- и клиентской стороны сетевой сессии.
 * Поддерживает только синхронизацию постановки башен – для MVP-кооператива.
 */
public interface NetworkSession {
    /** Отправить команду «поставить башню». */
    void sendPlaceTower(String towerName, int x, int y);

    /** Подключение активно? */
    boolean isConnected();

    /** true — хост/сервер, false — клиент. */
    boolean isHost();

    /** Корректно завершить соединение. */
    void close();

//    void sendUpgradeTower(int x, int y);
    void sendSellTower(int x, int y);
}
