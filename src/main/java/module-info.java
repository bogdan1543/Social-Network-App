module org.example {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens org.example to javafx.fxml;
    exports org.example;
    exports org.example.controller;
    exports org.example.domain;
    opens org.example.controller to javafx.fxml;
}