package com.hatecode.equipmentma2.Controllers;

import com.hatecode.equipmentma2.pojo.Category;
import com.hatecode.equipmentma2.services.CategoryServices;
import java.sql.SQLException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class Controller {

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() throws SQLException {
        CategoryServices c = new CategoryServices();
        List<Category> category = c.getCategory();
        category.forEach(ct -> System.out.println(ct.toString()));
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
