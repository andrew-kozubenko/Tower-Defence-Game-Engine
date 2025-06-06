module ru.nsu.t4werok.towerdefence.app {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires com.fasterxml.jackson.databind;

    exports ru.nsu.t4werok.towerdefence.managers.menu;
    exports ru.nsu.t4werok.towerdefence.config.game.entities.map;
    exports ru.nsu.t4werok.towerdefence.config.menu;
    exports ru.nsu.t4werok.towerdefence.app;
    exports ru.nsu.t4werok.towerdefence.controller;
    exports ru.nsu.t4werok.towerdefence.config.game.entities.tower;
    exports ru.nsu.t4werok.towerdefence.config.game.playerState.tech;
    exports ru.nsu.t4werok.towerdefence.config.game.entities.enemy;

    opens ru.nsu.t4werok.towerdefence.config.game.entities.enemy to com.fasterxml.jackson.databind;
    opens ru.nsu.t4werok.towerdefence.app to javafx.fxml;
    opens ru.nsu.t4werok.towerdefence.controller to javafx.fxml;
    opens ru.nsu.t4werok.towerdefence.model.game.entities.tower to com.fasterxml.jackson.databind;
    opens ru.nsu.t4werok.towerdefence.model.game.entities.map to com.fasterxml.jackson.databind;
    opens ru.nsu.t4werok.towerdefence.model.game.playerState.tech to com.fasterxml.jackson.databind;
}

