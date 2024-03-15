package com.example.im_exam_m3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.transformation.FilteredList;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class TransactionHistory {

    @FXML
    private Button btn_home;

    @FXML
    private Button btn_search;

    @FXML
    private TableView<TransactionData> table_History;

    @FXML
    private TableColumn<TransactionData, Integer> clm_ContractID;

    @FXML
    private TableColumn<TransactionData, Integer> clm_ID;

    @FXML
    private TableColumn<TransactionData, Integer> clm_amount;

    @FXML
    private TableColumn<TransactionData, String> clm_date;

    @FXML
    private TableColumn<TransactionData, String> clm_mode;

    @FXML
    private TableColumn<TransactionData, String> clm_tenant;

    @FXML
    private TableColumn<TransactionData, String> clm_type;

    @FXML
    private TextField tb_search;

    @FXML
    void initialize() {
        // Initialize table columns
        clm_ContractID.setCellValueFactory(new PropertyValueFactory<>("contractID"));
        clm_ID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        clm_amount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        clm_date.setCellValueFactory(new PropertyValueFactory<>("date"));
        clm_mode.setCellValueFactory(new PropertyValueFactory<>("mode"));
        clm_tenant.setCellValueFactory(new PropertyValueFactory<>("name"));
        clm_type.setCellValueFactory(new PropertyValueFactory<>("type"));

        loadTransactionData();
    }


    private void loadTransactionData() {
        ObservableList<TransactionData> transactionList = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_im_finals", "root", "");
             PreparedStatement statement = connection.prepareStatement("SELECT p.Contract_ID, p.Payment_ID, p.Payment_amount, p.Payment_date, pm.Payment_Mode, pt.Payment_type, c.Customer_Name " +
                     "FROM tbl_payment p " +
                     "JOIN tbl_payment_mode pm ON p.PaymentMode_ID = pm.PaymentMode_ID " +
                     "JOIN tbl_payment_type pt ON p.PaymentType_ID = pt.PaymentType_ID " +
                     "JOIN tbl_contract ct ON p.Contract_ID = ct.Contract_ID " +
                     "JOIN tbl_customer c ON ct.Customer_ID = c.Customer_ID");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int contractID = resultSet.getInt("Contract_ID");
                int ID = resultSet.getInt("Payment_ID");
                int amount = resultSet.getInt("Payment_amount");
                String date = resultSet.getString("Payment_date");
                String mode = resultSet.getString("Payment_Mode");
                String type = resultSet.getString("Payment_type");
                String name = resultSet.getString("Customer_Name");

                transactionList.add(new TransactionData(ID, contractID, name, type, mode, date, amount));
            }

            table_History.setItems(transactionList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    @FXML
    void MoveToHomePage(ActionEvent event) {
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
    void search_table(ActionEvent event) {
        String searchText = tb_search.getText().toLowerCase(); // Get the search text

        // Create a filtered list based on the data in table_History
        FilteredList<TransactionData> filteredList = new FilteredList<>(table_History.getItems());

        // Set the filter predicate based on the search text
        filteredList.setPredicate(transaction -> {
            if (searchText == null || searchText.isEmpty()) {
                return true; // Display all data if search text is empty
            }
            // Filter by name, contractID, and ID
            return transaction.getName().toLowerCase().contains(searchText) ||
                    String.valueOf(transaction.getContractID()).contains(searchText) ||
                    String.valueOf(transaction.getID()).contains(searchText);
        });

        // Set the filtered list as the items in the TableView
        table_History.setItems(filteredList);

        // If the search text is empty, load all transaction data
        if (searchText == null || searchText.isEmpty()) {
            loadTransactionData();
        }
    }


}
