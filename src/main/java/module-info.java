module com.hatecode.equipmentma2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    requires junit;
    requires cloudinary.core;
    requires jakarta.mail;

    requires java.desktop;
    requires java.persistence;


    opens com.hatecode.equipmentma2 to javafx.fxml;
    exports com.hatecode.equipmentma2;

    exports com.hatecode.pojo;
    exports com.hatecode.services.interfaces;
    exports com.hatecode.services.impl;

    exports com.hatecode.utils;
}