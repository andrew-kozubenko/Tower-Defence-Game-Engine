module ru.nsu.t4werok.towerdefence {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;

    opens ru.nsu.t4werok.towerdefence to javafx.fxml;
    exports ru.nsu.t4werok.towerdefence;
}