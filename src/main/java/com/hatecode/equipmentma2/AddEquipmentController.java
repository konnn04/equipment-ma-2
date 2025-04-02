package com.hatecode.equipmentma2;

import com.hatecode.models.Equipment;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class AddEquipmentController {

    @FXML
    private ListView<Equipment> availableEquipmentListView;

    @FXML
    private ListView<Equipment> addEquipmentListView;

    @FXML
    private ObservableList<Equipment> result;

    private Stage popupStage;

    public void setStage(Stage stage) {
        this.popupStage = stage;
    }

    public ObservableList<Equipment> getResult() {
        return result;
    }

    @FXML
    private void handleSave() {
        if (addEquipmentListView != null)
            result = addEquipmentListView.getItems();
        popupStage.close();
    }

    @FXML
    private void handleCancel() {
        result = null;
        popupStage.close();
    }

}
