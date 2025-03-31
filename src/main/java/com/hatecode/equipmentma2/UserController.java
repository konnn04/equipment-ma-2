package com.hatecode.equipmentma2;

import com.hatecode.pojo.Role;
import com.hatecode.pojo.User;
import com.hatecode.services.impl.RoleServiceImpl;
import com.hatecode.services.impl.UserServiceImpl;
import com.hatecode.services.interfaces.RoleService;
import com.hatecode.services.interfaces.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class UserController implements Initializable {
    @FXML
    private TableView<User> users;

    @FXML
    private ComboBox<Role> roles;

    @FXML
    private TextField txtSearchUser;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadColumn();
            loadUsers(null,0);
            loadRole();

            // Tìm kiếm dựa theo tên
            this.txtSearchUser.textProperty().addListener(e -> {
                if(roles.getSelectionModel().getSelectedItem() == null) {
                    loadUsers(this.txtSearchUser.getText(), 0);
                }
                else{
                    loadUsers(this.txtSearchUser.getText(), roles.getSelectionModel().getSelectedItem().getId());
                }
            });

            roles.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    loadUsers(this.txtSearchUser.getText(),newVal.getId());
                }
            });

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Load danh sách các cột chỉ định
    public void loadColumn(){
        TableColumn colId = users.getColumns().get(0);
        colId.setCellValueFactory(new PropertyValueFactory("id"));

        TableColumn colName = users.getColumns().get(1);
        colName.setCellValueFactory(new PropertyValueFactory("username"));

        TableColumn colRole = users.getColumns().get(2);
        colRole.setCellValueFactory(new PropertyValueFactory("roleName"));
    }

    public void loadUsers(String kw,int roleId){
        UserService services = new UserServiceImpl();
        try {
            List<User> res = services.getUsers(kw,roleId);
            this.users.setItems(FXCollections.observableList(res));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Load dữ liệu Role
    public void loadRole() throws SQLException {
        RoleService services = new RoleServiceImpl();
        List<Role> data = services.getRoles();
        ObservableList<Role> roleObList = FXCollections.observableList(data);
        roles.setItems(roleObList);
    }

}
