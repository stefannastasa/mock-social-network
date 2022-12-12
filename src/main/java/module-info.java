module com.example.mocksocialnetwork {
    requires javafx.controls;
    requires javafx.fxml;


    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.mocksocialnetwork to javafx.fxml;
    exports com.mocksocialnetwork;
}