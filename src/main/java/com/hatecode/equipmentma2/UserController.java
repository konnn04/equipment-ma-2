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
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class UserController implements Initializable {
    @FXML
    private TableView<User> users;

    @FXML
    private ComboBox<Role> roles;

    @FXML
    private TextField txtSearchUser;

    // Các control cho phần chi tiết
    @FXML
    private TextField userIdField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private ComboBox<Role> roleComboBox;
    @FXML
    private CheckBox isActiveCheckBox;
    @FXML
    private ImageView avatarImageView;
    @FXML
    private Button changeAvatarButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button newUserButton;
    @FXML
    private Button deleteUserButton;

    private File selectedAvatarFile;
    private User currentUser;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadColumn();
            loadUsers(null, 0);
            loadRole();
            setupHandler();
            setupDetailForm();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Load danh sách các cột chỉ định
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
        ObservableList<Role> roleObList = FXCollections.observableList(data);
        roles.setItems(roleObList);
        roleComboBox.setItems(roleObList);
    }

    private void setupHandler() {
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
//        System.out.println(user.getAvatar().getId());

        // Hiển thị thông tin user lên form
        userIdField.setText(String.valueOf(user.getId()));
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        usernameField.setText(user.getUsername());
        passwordField.setText("");
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhone());
        isActiveCheckBox.setSelected(user.isActive());
        roles.getSelectionModel().select(user.getRole());


        // Hiển thị avatar
        ImageService imageService = new ImageServiceImpl();
        com.hatecode.pojo.Image avatar = imageService.getImageById(user.getAvatarId());
        if (avatar != null) {
            try {
                UserService userService = new UserServiceImpl();
                String imageUrl = userService.getUserImage(user);

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
//                        new Date(),
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
//                    avatar.getId()
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
//        try {
//            UserService userService = new UserServiceImpl();
//
//            // Cập nhật thông tin từ form vào đối tượng user
//            currentUser.setFirstName(firstNameField.getText());
//            currentUser.setLastName(lastNameField.getText());
//            if (passwordField.getText().isEmpty() || passwordField.getText() == null) { // Không thay đổi mật khẩu
//                currentUser.setPassword(currentUser.getPassword());
//            } else { // Thay đổi mật khẩu
//                String salt = PasswordUtils.generateSalt();
//                String hashedPassword = PasswordUtils.hashPassword(passwordField.getText(), salt);
//                currentUser.setPassword(hashedPassword);
//            }
//            currentUser.setUsername(usernameField.getText());
//            currentUser.setEmail(emailField.getText());
//            currentUser.setPhone(phoneField.getText());
//            currentUser.setRole(roleComboBox.getSelectionModel().getSelectedItem());
//            currentUser.setActive(isActiveCheckBox.isSelected());
//
//
//            if (currentUser.getId() == 0) {
//                // Thêm mới
//                userService.addUser(currentUser);
//                showInfoAlert("Thành công", "Thêm người dùng thành công");
//            } else {
//                com.hatecode.pojo.Image currUserImg = null;
//                if (selectedAvatarFile != null) {
//                    UserService services = new UserServiceImpl();
//                    String imgUrl = services.uploadUserImage(selectedAvatarFile);
//                    ImageService imgServices = new ImageServiceImpl();
//                    String fileName = ExtractImageIdUtils.extractPublicIdFromUrl(imgUrl);
//                    // Nếu là hình mặc định thì tạo mới
//                    if (currentUser.getAvatarId() == 1) {
//                        currUserImg = new com.hatecode.pojo.Image();
//                        currUserImg.setId(0);
//                    } else {
//                        currUserImg = imgServices.getImageById(currentUser.getAvatarId());
//                    }
//                    currUserImg.setFilename(fileName);
//                    currUserImg.setPath(imgUrl);
//                    currUserImg.setCreateDate(new Date());
//                }
//                if (userService.updateUser(currentUser, currUserImg)) {
//                    showInfoAlert("Thành công", "Cập nhật người dùng thành công");
//                    loadUsers(null, 0);
//                }
//            }
//
//            // Làm mới danh sách
//            loadUsers(txtSearchUser.getText(),
//                    roles.getSelectionModel().getSelectedItem() != null ?
//                            roles.getSelectionModel().getSelectedItem().getId() : 0);
//
//        } catch (SQLException e) {
//            showErrorAlert("Lỗi", "Không thể lưu người dùng", e.getMessage());
//            throw e;
//        } catch (Exception e) {
//            showErrorAlert("Lỗi", "Dữ liệu không hợp lệ", e.getMessage());
//            throw e;
//        }
    }

    private void setupDetailForm() {
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
