package com.hatecode.equipmentma2;

import com.hatecode.pojo.Role;
import com.hatecode.pojo.User;
import com.hatecode.services.impl.ImageServiceImpl;
import com.hatecode.services.impl.UserServiceImpl;
import com.hatecode.services.ImageService;
import com.hatecode.services.UserService;
import com.hatecode.utils.ExtractImageIdUtils;
import com.hatecode.utils.PasswordUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class UserManagerController {
    @FXML
    TableView<User> users;
    @FXML
    ComboBox<Role> roles;
    @FXML
    TextField txtSearchUser;
    // Các control cho phần chi tiết
    @FXML
    TextField userIdField;
    @FXML
    TextField firstNameField;
    @FXML
    TextField lastNameField;
    @FXML
    TextField usernameField;
    @FXML
    TextField passwordField;
    @FXML
    TextField emailField;
    @FXML
    TextField phoneField;
    @FXML
    ComboBox<Role> roleComboBox;
    @FXML
    CheckBox isActiveCheckBox;
    @FXML
    ImageView avatarImageView;
    @FXML
    Button changeAvatarButton;
    @FXML
    Button saveButton;
    @FXML
    Button newUserButton;
    @FXML
    Button deleteUserButton;

    private File selectedAvatarFile;
    private User currentUser;

    public void loadColumn() {
        TableColumn colId = users.getColumns().get(0);
        colId.setCellValueFactory(new PropertyValueFactory("id"));

        TableColumn colName = users.getColumns().get(1);
        colName.setCellValueFactory(new PropertyValueFactory("username"));

        TableColumn colRole = users.getColumns().get(2);
        colRole.setCellValueFactory(new PropertyValueFactory("role"));
    }

    public void loadUsers(String kw, int roleId) {
        UserService services = new UserServiceImpl();
        try {
            List<User> res = services.getUsers(kw, roleId);
            this.users.setItems(FXCollections.observableList(res));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Load dữ liệu Role
    public void loadRole() throws SQLException {
        List<Role> data = Role.getAllRoles();
        ObservableList<Role> roleList = FXCollections.observableArrayList(data);
        roles.setItems(roleList);
        roleComboBox.setItems(roleList);
    }

    public void setupHandler() {
        // Tìm kiếm
        this.txtSearchUser.textProperty().addListener(e -> {
            if (roles.getSelectionModel().getSelectedItem() == null) {
                loadUsers(this.txtSearchUser.getText(), 0);
            } else {
                loadUsers(this.txtSearchUser.getText(), roles.getSelectionModel().getSelectedItem().getId());
            }
        });

        // Lọc theo role
        roles.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadUsers(this.txtSearchUser.getText(), newVal.getId());
            }
        });

        // Chọn user từ bảng
        users.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    showUserDetails(newVal);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Nút thêm mới
        newUserButton.setOnAction(e -> {
            try {
                createNewUser();
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);
            } catch (InvalidKeySpecException ex) {
                throw new RuntimeException(ex);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Nút lưu
        saveButton.setOnAction(e -> {
            try {
                saveUser();
            } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Nút đổi avatar
        changeAvatarButton.setOnAction(e -> changeAvatar());

        // Nút xóa
        deleteUserButton.setOnAction(e -> {
            try {
                deleteUser();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void showUserDetails(User user) throws SQLException {
        this.currentUser = user;
        System.out.println(user.getAvatarId());

        // Hiển thị thông tin user lên form
        userIdField.setText(String.valueOf(user.getId()));
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        usernameField.setText(user.getUsername());
        passwordField.setText("");
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhone());
        isActiveCheckBox.setSelected(user.isActive());

        // Thiết lập role
        for (Role role : roleComboBox.getItems()) {
            if (role.getId() == user.getRole().getId()) {
                roleComboBox.getSelectionModel().select(role);
                break;
            }
        }

        // Hiển thị avatar từ Cloudinary
        ImageService  imageService = new ImageServiceImpl();
        if (imageService.getImageById(user.getAvatarId()) != null) {
            try {
                UserService userService = new UserServiceImpl();
                String imageUrl = userService.getUserImage(user);
                System.out.println(imageUrl);
                if (imageUrl != null) {
                    // Tải ảnh từ URL và hiển thị
                    Image image = new Image(imageUrl, true); // true để tải ảnh nền
                    avatarImageView.setImage(image);
                } else {
                    // Hiển thị ảnh mặc định nếu không có avatar
                    avatarImageView.setImage(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                avatarImageView.setImage(null);
            }
        } else {
            System.out.println("No avatar found");
            avatarImageView.setImage(null);
        }

        selectedAvatarFile = null;
    }

    private void createNewUser() throws NoSuchAlgorithmException, InvalidKeySpecException, SQLException {
//        try {
//            String salt = PasswordUtils.generateSalt();
//            String hashedPassword = PasswordUtils.hashPassword(passwordField.getText(), salt);
//            Role selectedRole = roleComboBox.getSelectionModel().getSelectedItem();
//            UserService services = new UserServiceImpl();
//            com.hatecode.pojo.Image avatar = null;
//            if (selectedAvatarFile != null) {
//                String imgUrl = services.uploadUserImage(selectedAvatarFile);
//                String fileName = ExtractImageIdUtils.extractPublicIdFromUrl(imgUrl);
//                avatar = new com.hatecode.pojo.Image(
//                        fileName,
//                        LocalDateTime.now(),
//                        imgUrl
//                );
//            }
//
//            this.currentUser = new User(
//                    firstNameField.getText(),
//                    lastNameField.getText(),
//                    usernameField.getText(),
//                    hashedPassword,
//                    emailField.getText(),
//                    phoneField.getText(),
//                    selectedRole,
//                    isActiveCheckBox.isSelected(),
//                    avatar
//            );
//            if (services.addUser(currentUser)) {
//                showInfoAlert("Succesfully", "Add new user Successfully");
//                loadUsers(null, 0);
//            } else {
//                showErrorAlert("Failed", "Error", "Failed to add new user");
//            }
//            clearForm();
//        } catch (Exception e) {
//            showErrorAlert("Error", "Error", "Failed when add user");
//            throw e;
//        }

        clearForm();
        this.currentUser = new User(); // Tạo user mới với giá trị mặc định
        userIdField.setText("0"); // Đặt ID tạm thời là 0
        isActiveCheckBox.setSelected(true); // Mặc định active khi tạo mới
    }

    private void clearForm() {
        userIdField.clear();
        firstNameField.clear();
        lastNameField.clear();
        usernameField.clear();
        passwordField.clear();
        emailField.clear();
        phoneField.clear();
        roleComboBox.getSelectionModel().clearSelection();
        isActiveCheckBox.setSelected(false);
        avatarImageView.setImage(null);
        selectedAvatarFile = null;
    }

    private void changeAvatar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh đại diện");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(avatarImageView.getScene().getWindow());
        if (file != null) {
            selectedAvatarFile = file;
            Image image = new Image(file.toURI().toString());
            avatarImageView.setImage(image);
        }
    }

    private void saveUser() throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {

        try {
            UserService userService = new UserServiceImpl();

            // Lấy thông tin từ form
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();
            Role selectedRole = roleComboBox.getSelectionModel().getSelectedItem();
            boolean isActive = isActiveCheckBox.isSelected();

            // Validate dữ liệu
            if ((currentUser.getId() == 0 && passwordField.getText().isEmpty()) || firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() ||
                    email.isEmpty() || selectedRole == null || phoneField.getText().isEmpty()) {
                showErrorAlert("Error", "Missing information", "Please fill in all required fields");
                return;
            }

            // Xử lý avatar
            com.hatecode.pojo.Image avatar = null;
            if (selectedAvatarFile != null) {
                String imgUrl = userService.uploadUserImage(selectedAvatarFile);
                String fileName = ExtractImageIdUtils.extractPublicIdFromUrl(imgUrl);
                avatar = new com.hatecode.pojo.Image(
                        0,
                        fileName,
                        LocalDateTime.now(),
                        imgUrl
                );
            }

//            // Xử lý mật khẩu
//            String password = currentUser.getPassword(); // Giữ nguyên nếu không thay đổi
//            if (!passwordField.getText().isEmpty()) {
//                password = PasswordUtils.hashPassword(passwordField.getText());
//            }

            // Nếu là user mới (ID = 0)x
            if (currentUser.getId() == 0) {
                currentUser = new User(
                        firstName,
                        lastName,
                        username,
                        password,
                        email,
                        phone,
                        selectedRole,
                        isActive,
                        avatar != null ? 0 : 1
                );

                if (userService.addUser(currentUser,avatar)) {
                    showInfoAlert("Thành công", "Thêm người dùng mới thành công");
                    loadUsers(txtSearchUser.getText(),
                            roles.getSelectionModel().getSelectedItem() != null ?
                                    roles.getSelectionModel().getSelectedItem().getId() : 0);
                    clearForm();
                }
            }
            // Nếu là cập nhật user hiện có
            else {
                currentUser.setFirstName(firstName);
                currentUser.setLastName(lastName);
                currentUser.setUsername(username);
                currentUser.setEmail(email);
                currentUser.setPhone(phone);
                currentUser.setRole(selectedRole);
                currentUser.setActive(isActive);

                String new_password = (passwordField.getText() != null && !passwordField.getText().isEmpty())
                        ? passwordField.getText() : null;


                if (userService.updateUser(currentUser, new_password, avatar)) {
                    showInfoAlert("Thành công", "Cập nhật người dùng thành công");
                    loadUsers(txtSearchUser.getText(),
                            roles.getSelectionModel().getSelectedItem() != null ?
                                    roles.getSelectionModel().getSelectedItem().getId() : 0);
                }
                else{
                    showErrorAlert("Lỗi", "Lỗi cơ sở dữ liệu", "BUGS");
                }
            }
        } catch (SQLException e) {
            showErrorAlert("Lỗi", "Lỗi cơ sở dữ liệu", e.getMessage());
            throw e;
        } catch (Exception e) {
            showErrorAlert("Lỗi", "Lỗi hệ thống", e.getMessage());
            throw e;
        }
    }

    public void setupDetailForm() {
        // Vô hiệu hóa trường ID
        userIdField.setDisable(true);

        // Thiết lập ComboBox role
        roleComboBox.setCellFactory(param -> new ListCell<Role>() {
            @Override
            protected void updateItem(Role item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        roleComboBox.setConverter(new StringConverter<Role>() {
            @Override
            public String toString(Role role) {
                return role == null ? null : role.getName();
            }

            @Override
            public Role fromString(String string) {
                return null;
            }
        });
    }

    private void deleteUser() throws SQLException {
        if (currentUser == null || currentUser.getId() == 0) {
            showErrorAlert("Error", "No User Selected", "Please select a user to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete User");
        confirmation.setContentText("Are you sure you want to delete user: " + currentUser.getUsername() + "?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    UserService userService = new UserServiceImpl();
                    if (userService.deleteUser(currentUser.getId())) {
                        showInfoAlert("Success", "User deleted successfully");
                        loadUsers(txtSearchUser.getText(),
                                roles.getSelectionModel().getSelectedItem() != null ?
                                        roles.getSelectionModel().getSelectedItem().getId() : 0);
                        clearForm();
                    } else {
                        showErrorAlert("Error", "Deletion Failed", "Failed to delete user");
                    }
                } catch (SQLException e) {
                    showErrorAlert("Error", "Database Error", e.getMessage());
                }
            }
        });
    }

    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfoAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}