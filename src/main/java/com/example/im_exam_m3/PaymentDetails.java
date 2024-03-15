package com.example.im_exam_m3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class PaymentDetails {

    @FXML
    private Button btn_Update;

    @FXML
    private Button btn_home;

    @FXML
    private Button btn_save;

    @FXML
    private TableColumn<Payment, String> clm_Amount;

    @FXML
    private TableColumn<Payment, String> clm_ContractID;

    @FXML
    private TableColumn<Payment, String> clm_Date;

    @FXML
    private TableColumn<Payment, String> clm_ID;

    @FXML
    private TableColumn<Payment, String> clm_Mode;

    @FXML
    private TableColumn<Payment, String> clm_Type;


    @FXML
    private ComboBox<String> cmb_Mode;

    @FXML
    private ComboBox<String> cmb_Type;

    @FXML
    private DatePicker dp_Date;

    @FXML
    private TextField tb_Amount;

    @FXML
    private TextField tb_ContractID;

    @FXML
    private Button btn_tenant;

    @FXML
    private Button btn_contract;

    @FXML
    private TableView<Payment> tbl_payments;

    @FXML
    void MovetoHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Home.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current window
            Stage currentStage = (Stage) btn_home.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle any errors loading the home page scene
        }
    }

    @FXML
    void MovetoContract(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Contract.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current window
            Stage currentStage = (Stage) btn_home.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle any errors loading the home page scene
        }
    }


    @FXML
    void MovetoTenant(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Customer-Details.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current window
            Stage currentStage = (Stage) btn_home.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle any errors loading the home page scene
        }
    }


    @FXML
    void SavetoDatabase(ActionEvent event) {
        try {
            // Retrieve data from UI components
            int contractID = Integer.parseInt(tb_ContractID.getText());
            String type = cmb_Type.getValue();
            String mode = cmb_Mode.getValue();
            String date = dp_Date.getValue().toString();
            int amount = Integer.parseInt(tb_Amount.getText());

            int Type_ID = getPrimaryKey("tbl_payment_type", "PaymentType_ID", "Payment_type", type);
            int Mode_ID = getPrimaryKey("tbl_payment_mode", "PaymentMode_ID", "Payment_mode", mode);

            if (!isContractExists(contractID)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Contract Doesn't exist");
                return;
            }
            // Check if there's already a record with the same Contract ID, type, and date
            if (isDuplicateRecord(contractID, type, date)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Duplicate Payment Unavailable");
                return;
            }

            // Insert data into the database
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_im_finals", "root", "");
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO tbl_payment (Contract_ID, PaymentType_ID, PaymentMode_ID, Payment_date, Payment_amount) VALUES (?, ?, ?, ?, ?)")) {
                statement.setInt(1, contractID);
                statement.setInt(2, Type_ID);
                statement.setInt(3, Mode_ID);
                statement.setString(4, date);
                statement.setInt(5, amount);

                int rowsInserted = statement.executeUpdate();

                if (rowsInserted > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Successfully added");
                    loadPaymentData();
                } else {
                    System.out.println("Failed to save payment to the database.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle any SQL exception here
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter numeric values for Contract ID and Amount.");
        }
    }



    @FXML
    void UpdateTable(ActionEvent event) {
        try {
            // Retrieve data from UI components
            int paymentID = tbl_payments.getSelectionModel().getSelectedItem().getID();
            int contractID = Integer.parseInt(tb_ContractID.getText());
            String type = cmb_Type.getValue();
            String mode = cmb_Mode.getValue();
            String date = dp_Date.getValue().toString();
            int amount = Integer.parseInt(tb_Amount.getText());

            int Type_ID = getPrimaryKey("tbl_payment_type", "PaymentType_ID", "Payment_type", type);
            int Mode_ID = getPrimaryKey("tbl_payment_mode", "PaymentMode_ID", "Payment_mode", mode);




            // Update data in the database
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_im_finals", "root", "");
                 PreparedStatement statement = connection.prepareStatement("UPDATE tbl_payment SET Contract_ID = ?, PaymentType_ID = ?, PaymentMode_ID = ?, Payment_date = ?, Payment_amount = ? WHERE Payment_ID = ?")) {
                statement.setInt(1, contractID);
                statement.setInt(2, Type_ID);
                statement.setInt(3, Mode_ID);
                statement.setString(4, date);
                statement.setInt(5, amount);
                statement.setInt(6, paymentID);

                int rowsUpdated = statement.executeUpdate();

                if (rowsUpdated > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Successfully added");
                    loadPaymentData();
                } else {
                    System.out.println("Failed to update payment details in the database.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle any SQL exception here
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Contract Id Doesn't Exist");
        } catch (NullPointerException e) {
            showAlert(Alert.AlertType.INFORMATION, "Succes", "Please Select a row to Update");
        }
    }


    private boolean isDuplicateRecord(int contractID, String type, String date) {
        String query = "SELECT COUNT(*) FROM tbl_payment WHERE Contract_ID = ? AND PaymentType_ID = (SELECT PaymentType_ID FROM tbl_payment_type WHERE Payment_type = ?) AND Payment_date = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_im_finals", "root", "");
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, contractID);
            statement.setString(2, type);
            statement.setString(3, date);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isContractExists(int contractID) {
        String query = "SELECT COUNT(*) FROM tbl_contract WHERE Contract_ID = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_im_finals", "root", "");
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, contractID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    @FXML
    public void initialize() {
        populateComboBox(cmb_Mode, "SELECT Payment_Mode FROM tbl_payment_mode");
        populateComboBox(cmb_Type, "SELECT Payment_type FROM tbl_payment_type");

        clm_ID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        clm_ContractID.setCellValueFactory(new PropertyValueFactory<>("contractID"));
        clm_Type.setCellValueFactory(new PropertyValueFactory<>("type"));
        clm_Mode.setCellValueFactory(new PropertyValueFactory<>("mode"));
        clm_Date.setCellValueFactory(new PropertyValueFactory<>("date"));
        clm_Amount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        loadPaymentData();
    }
    private void loadPaymentData() {
        ObservableList<Payment> paymentList = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_im_finals", "root", "");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT p.Payment_ID, p.Contract_ID, pm.Payment_mode, pt.Payment_type, p.Payment_amount, p.payment_date " +
                     "FROM tbl_payment p " +
                     "JOIN tbl_payment_mode pm ON p.PaymentMode_ID = pm.PaymentMode_ID " +
                     "JOIN tbl_payment_type pt ON p.PaymentType_ID = pt.PaymentType_ID " +
                     "ORDER BY p.Payment_ID DESC")) { // Ordering by Payment_ID in descending order

            while (resultSet.next()) {
                int paymentID = resultSet.getInt("Payment_ID");
                int contractID = resultSet.getInt("Contract_ID");
                String paymentMode = resultSet.getString("Payment_mode");
                String paymentType = resultSet.getString("Payment_type");
                int paymentAmount = resultSet.getInt("Payment_amount");
                String paymentDate = resultSet.getString("Payment_date");

                // Create a new Payment object and add it to the list
                paymentList.add(new Payment(paymentID, contractID, paymentType, paymentMode , paymentDate, paymentAmount));
            }

            // Set the items in the TableView
            tbl_payments.setItems(paymentList);
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any SQL exception here
        }
    }


    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void populateComboBox(ComboBox<String> comboBox, String query) {
        ObservableList<String> itemList = FXCollections.observableArrayList();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_im_finals", "root", "");
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                itemList.add(resultSet.getString(1));
            }
            comboBox.setItems(itemList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getPrimaryKey(String tableName, String primaryKeyColumn, String searchColumn, String searchValue) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_im_finals", "root", "");
             PreparedStatement statement = connection.prepareStatement("SELECT " + primaryKeyColumn + " FROM " + tableName + " WHERE " + searchColumn + " = ?")) {
            statement.setString(1, searchValue);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any SQL exception here
        }
        return -1; // Return -1 if no record found
    }
}
