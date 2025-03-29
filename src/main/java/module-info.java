module com.hatecode.equipmentma2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    
    
    requires junit;
    requires cloudinary.core;


    opens com.hatecode.equipmentma2 to javafx.fxml;
    exports com.hatecode.equipmentma2;    


    opens com.hatecode.equipmentma2.Controllers to javafx.fxml;
    opens com.hatecode.equipmentma2.components to javafx.fxml;
    exports com.hatecode.equipmentma2.Controllers;

    exports com.hatecode.pojo;
    exports com.hatecode.services;
    exports com.hatecode.services.impl;
    
}
