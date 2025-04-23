module com.hatecode.equipmentma2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    requires cloudinary.core;
    requires jakarta.mail;

    requires jbcrypt;

    requires java.desktop;
    requires mysql.connector.j;
    opens com.hatecode.config;
//    requires dotenv.java;
    requires io.github.cdimascio.dotenv.java;
    opens com.hatecode.equipmentma2 to javafx.fxml;
    exports com.hatecode.equipmentma2;
    exports com.hatecode.pojo;
    exports com.hatecode.services.impl;
    exports com.hatecode.utils;
}
