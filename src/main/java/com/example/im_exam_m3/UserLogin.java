package com.example.im_exam_m3;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.Parent;
import javafx.stage.Stage;
import java.io.IOException;


import java.io.IOException;


public class UserLogin {

    @FXML
    private Button btn_exit;

    @FXML
    private Button btn_login;

    @FXML
    private PasswordField tb_password;

    @FXML
    private TextField tb_username;

    @FXML
    void ExitSystem(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void MoveToHomePage(ActionEvent event) throws IOException {
        String username = tb_username.getText();
        String password = tb_password.getText();

        // Check if both username and password are "admin"
        if (username.equals("admin") && password.equals("admin")) {
            try {
                // Load the FXML file for Scene 3
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Home.fxml"));
                Parent homeParent = loader.load();

                // Get the current stage
                Stage stage = (Stage) tb_username.getScene().getWindow();

                // Create a new scene with Scene 3 content
                Scene homeScene = new Scene(homeParent);

                // Set the new scene on the stage
                stage.setScene(homeScene);
            } catch (IOException e) {
                System.err.println("Error loading Home.fxml: " + e.getMessage());
            }
        } else {
            // Handle invalid login
            System.err.println("Username or Password incorrect. Please input properly.");

            // Clear the text fields
            tb_password.clear();
            tb_username.clear();

            // Show an error message box
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText("The username or password is incorrect. Please try again.");
            alert.showAndWait();
        }

    }

}