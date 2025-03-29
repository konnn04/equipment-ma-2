package com.hatecode.equipmentma2.components;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImageUploader extends VBox {
    private ImageView imagePreview;
    private Button uploadButton;
    private File selectedFile;
    
    public ImageUploader() {
        super(10); // spacing = 10
        
        // Tạo button upload
        uploadButton = new Button("Chọn ảnh");
        uploadButton.setPrefWidth(150);
        
        // Tạo khung preview ảnh
        imagePreview = new ImageView();
        imagePreview.setFitHeight(200);
        imagePreview.setFitWidth(200);
        imagePreview.setPreserveRatio(true);
        
        // Thêm hành động cho button
        uploadButton.setOnAction(e -> openFileChooser());
        
        // Thêm các components vào VBox
        getChildren().addAll(uploadButton, imagePreview);
    }
    
    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh");
        fileChooser.getExtensionFilters().addAll(
            new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        // Hiển thị FileChooser
        selectedFile = fileChooser.showOpenDialog(getScene().getWindow());
        
        if (selectedFile != null) {
            try {
                // Hiển thị ảnh đã chọn
                Image image = new Image(new FileInputStream(selectedFile));
                imagePreview.setImage(image);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public File getSelectedFile() {
        return selectedFile;
    }

}