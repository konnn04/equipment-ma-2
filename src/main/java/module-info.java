module com.hatecode.equipmentma2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.hatecode.equipmentma2 to javafx.fxml;
    exports com.hatecode.equipmentma2;
    exports com.hatecode.equipmentma2.pojo;
    exports com.hatecode.equipmentma2.services;

    opens com.hatecode.equipmentma2.Controllers to javafx.fxml;
}