package com.example.im_exam_m3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class Contract {

    @FXML
    private Button btn_add;

    @FXML
    private Button btn_home;

    @FXML
    private Button btn_payment;

    @FXML
    private Button btn_search;

    @FXML
    private TableColumn<ContractDetails, Integer> clm_ID;

    @FXML
    private TableColumn<ContractDetails, String> clm_end;

    @FXML
    private TableColumn<ContractDetails, String> clm_room;

    @FXML
    private TableColumn<ContractDetails, String> clm_start;

    @FXML
    private TableColumn<ContractDetails, String> clm_tenant;

    @FXML
    private TableColumn<ContractDetails, String> clm_type;

    @FXML
    private ComboBox<String> cmb_room;

    @FXML
    private ComboBox<String> cmb_tenant;

    @FXML
    private ComboBox<String> cmb_type;

    @FXML
    private DatePicker dp_end;

    @FXML
    private DatePicker dp_start;

    @FXML
    private TableView<ContractDetails> table_contract;

    @FXML
    private Button btn_tenant;
    @FXML
    private TextField tb_search;

    @FXML
    void MoveToTenant(ActionEvent event) {
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
    void MoveToPayment(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Payment-Details.fxml"));
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
    void addContract(ActionEvent event) {
        String roomID = cmb_room.getValue();
        String tenantName = cmb_tenant.getValue();
        String contractType = cmb_type.getValue();
        String startDate = dp_start.getValue().toString();
        String endDate = dp_end.getValue().toString();

        // Validate input
        if (roomID == null || tenantName == null || contractType == null || startDate.isEmpty() || endDate.isEmpty()) {
            // Handle validation failure
            System.out.println("Please fill in all fields.");
            return;
        }

        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Validate start date
        if (!isAfter(LocalDate.parse(startDate), currentDate)) {
            System.out.println("Start date must be today or in the future.");
            return;
        }

        // Validate end date
        LocalDate oneMonthLater = plusMonths(LocalDate.parse(startDate), 1);
        if (!isAfter(LocalDate.parse(endDate), oneMonthLater)) {
            System.out.println("End date must be at least 1 month after the start date.");
            return;
        }

        int customerID = getPrimaryKey("tbl_customer", "Customer_ID", "Customer_Name", tenantName);
        int contractTypeID = getPrimaryKey("tbl_contract_type", "ContractType_ID", "Contract_type", contractType);

        // Check if contract already exists
        if (isContractExists(customerID, roomID, startDate, endDate)) {
            System.out.println("Contract already exists for the selected tenant and room within the specified dates.");
            return;
        }


        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_im_finals", "root", "");
             PreparedStatement statement = connection.prepareStatement("INSERT INTO tbl_contract (Room_ID, Customer_ID, ContractType_ID, Date_start, Date_end) VALUES (?, ?, ?, ?, ?)")) {
            statement.setString(1, roomID);
            statement.setInt(2, customerID);
            statement.setInt(3, contractTypeID);
            statement.setString(4, startDate);
            statement.setString(5, endDate);

            int rowsInserted = statement.executeUpdate();
            updateRoomStatus(roomID, "3");

            if (rowsInserted > 0) {
                System.out.println("Contract added successfully.");

                // Clear ComboBoxes and DatePicker
                populateComboBox(cmb_room, "SELECT Room_ID FROM tbl_rooms WHERE RoomStatus_ID = '2'");
                cmb_tenant.setValue(null);
                cmb_type.setValue(null);
                dp_start.setValue(null);
                dp_end.setValue(null);

                // Refresh TableView
                loadContractData();
            } else {
                System.out.println("Failed to add contract.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private boolean isBefore(LocalDate date1, LocalDate date2) {
        return date1.compareTo(date2) < 0;
    }

    private boolean isAfter(LocalDate date1, LocalDate date2) {
        return date1.compareTo(date2) > 0;
    }

    private LocalDate plusMonths(LocalDate date, int months) {
        return date.plusMonths(months);
    }



    @FXML
    public void initialize() {
        populateComboBox(cmb_room, "SELECT Room_ID FROM tbl_rooms WHERE RoomStatus_ID = '2'");
        populateComboBox(cmb_tenant, "SELECT Customer_Name FROM tbl_customer");
        populateComboBox(cmb_type, "SELECT Contract_type FROM tbl_contract_type");

        clm_ID.setCellValueFactory(new PropertyValueFactory<>("Contract_ID"));
        clm_room.setCellValueFactory(new PropertyValueFactory<>("Room_ID"));
        clm_tenant.setCellValueFactory(new PropertyValueFactory<>("Customer_name"));
        clm_type.setCellValueFactory(new PropertyValueFactory<>("Contract_type"));
        clm_start.setCellValueFactory(new PropertyValueFactory<>("Date_start"));
        clm_end.setCellValueFactory(new PropertyValueFactory<>("Date_end"));


        loadContractData();
    }

    private void loadContractData() {
        ObservableList<ContractDetails> contractList = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_im_finals", "root", "");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT c.Contract_ID, cu.Customer_Name, ct.Contract_type, c.Room_ID, c.Date_start, c.Date_end " +
                     "FROM tbl_contract c " +
                     "JOIN tbl_customer cu ON c.Customer_ID = cu.Customer_ID " +
                     "JOIN tbl_contract_type ct ON c.ContractType_ID = ct.ContractType_ID")) {

            while (resultSet.next()) {
                int id = resultSet.getInt("Contract_ID");
                String tenant = resultSet.getString("Customer_Name");
                String type = resultSet.getString("Contract_type");
                String roomId = resultSet.getString("Room_ID");
                String start = resultSet.getString("Date_start");
                String end = resultSet.getString("Date_end");

                contractList.add(new ContractDetails(id, tenant, roomId, type, start, end));
            }

            // Set the items in the TableView
            table_contract.setItems(contractList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    private void updateRoomStatus(String roomId, String status) {
        String updateQuery = "UPDATE tbl_rooms SET RoomStatus_ID = ? WHERE Room_ID = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_im_finals", "root", "");
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setString(1, status);
            statement.setString(2, roomId);
            statement.executeUpdate();
            System.out.println("Room status updated to " + status + " for Room ID: " + roomId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isContractExists(int tenant, String room, String startDate, String endDate) {
        String query = "SELECT COUNT(*) FROM tbl_contract WHERE Customer_ID = ? AND Room_ID = ? AND Date_start = ? AND Date_end = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_im_finals", "root", "");
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, tenant);
            statement.setString(2, room);
            statement.setString(3, startDate);
            statement.setString(4, endDate);

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
    void searchDetails(ActionEvent event) {
        String searchTerm = tb_search.getText();
        ObservableList<ContractDetails> filteredList = FXCollections.observableArrayList();

        if (searchTerm.isEmpty()) {
            // If search term is empty, reload all contract data
            loadContractData();
            return;
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_im_finals", "root", "");
             PreparedStatement statement = connection.prepareStatement("SELECT c.Contract_ID, cu.Customer_Name, ct.Contract_type, c.Room_ID, c.Date_start, c.Date_end " +
                     "FROM tbl_contract c " +
                     "JOIN tbl_customer cu ON c.Customer_ID = cu.Customer_ID " +
                     "JOIN tbl_contract_type ct ON c.ContractType_ID = ct.ContractType_ID " +
                     "WHERE c.Room_ID LIKE ? OR cu.Customer_Name LIKE ? OR ct.Contract_type LIKE ?")) {
            statement.setString(1, "%" + searchTerm + "%");
            statement.setString(2, "%" + searchTerm + "%");
            statement.setString(3, "%" + searchTerm + "%");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("Contract_ID");
                    String tenant = resultSet.getString("Customer_Name");
                    String type = resultSet.getString("Contract_type");
                    String roomId = resultSet.getString("Room_ID");
                    String start = resultSet.getString("Date_start");
                    String end = resultSet.getString("Date_end");

                    filteredList.add(new ContractDetails(id, tenant, roomId, type, start, end));
                }
            }

            // Set the items in the TableView
            table_contract.setItems(filteredList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
