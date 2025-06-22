package ru.nsu.t4werok.towerdefence.model.game.playerState;

import java.util.UUID;

/**
 * Минимальный набор данных, которые нужны любому компоненту движка,
 * чтобы «узнать» игрока и его базовые ресурсы.
 * Пока здесь нет сетевого кода и сериализации ‒
 * всё по-прежнему работает локально.
 */
public interface PlayerContext {

    /** Уникальный идентификатор (детерминируется один раз на клиенте/сервере). */
    UUID getId();

    /** Короткое отображаемое имя в UI. */
    String getName();

    /** Текущие монеты игрока. */
    int getCoins();

    /** Снятие денег (true ‒ если хватило средств). */
    boolean spendCoins(int amount);

    /** Прибавка денег. */
    void addCoins(int amount);

    /** Остаток «здоровья» игрока/базы. */
    int getHealth();

    /** Прямая запись урона (не может опустить здоровье ниже нуля). */
    void damage(int dmg);

    /** Исцеление. */
    void heal(int value);
}
