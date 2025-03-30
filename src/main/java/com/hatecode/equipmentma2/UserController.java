package com.hatecode.equipmentma2;

import com.hatecode.pojo.User;
import com.hatecode.services.impl.UserServiceImpl;
import com.hatecode.services.interfaces.UserService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class UserController implements Initializable {
    @FXML
    private TableView<User> users;


    public void loadUsers(){
        UserServiceImpl services = new UserServiceImpl();
        try {
            List<User> res = services.getUsers();
            res.forEach(u -> System.out.println(u.toString()));
            this.users.setItems(FXCollections.observableList(res));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadColumn();
        loadUsers();
    }

    public void loadColumn(){
        TableColumn colId = users.getColumns().get(0);
        colId.setCellValueFactory(new PropertyValueFactory("id"));

        TableColumn colName = users.getColumns().get(1);
        colName.setCellValueFactory(new PropertyValueFactory("username"));

        TableColumn colRole = users.getColumns().get(2);
        colRole.setCellValueFactory(new PropertyValueFactory("roleName"));

    }
}
