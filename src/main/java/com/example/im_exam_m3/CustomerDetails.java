package com.example.im_exam_m3;

import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.Alert.AlertType;
public class CustomerDetails implements Initializable {

    @FXML
    private Button btn_add;

    @FXML
    private Button btn_home;

    @FXML
    private Button btn_search;

    @FXML
    private TableColumn<CustomerDetails_Data, String> clm_contact;

    @FXML
    private TableColumn<CustomerDetails_Data, String> clm_employment;

    @FXML
    private TableColumn<CustomerDetails_Data, String> clm_name;

    @FXML
    private TableColumn<CustomerDetails_Data, String> clm_city;

    @FXML
    private TableColumn<CustomerDetails_Data, String> clm_address;

    @FXML
    private TableColumn<CustomerDetails_Data, String> clm_customerID;

    @FXML
    private ComboBox<String> cmb_city;

    @FXML
    private ComboBox<String> cmb_employment;

    @FXML
    private ComboBox<String> cmb_source;

    @FXML
    private Button btn_payment;

    @FXML
    private TableView<CustomerDetails_Data> table_tenant;

    @FXML
    private TextField tb_address;

    @FXML
    private TextField tb_contact;

    @FXML
    private TextField tb_name;

    @FXML
    private Button btn_contract;

    @FXML
    private Button btn_update;

    @FXML
    private TextField tb_search;

    private List<String> allCities = new ArrayList<>();

    private ObservableList<CustomerDetails_Data> data;

    private FilteredList<CustomerDetails_Data> filteredData;


    @FXML
    void MovetoPaymentPage(ActionEvent event) {
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
    void Addcustomer(ActionEvent event) {
        // Retrieve the selected items from ComboBoxes
        String cityName = cmb_city.getValue();
        String status = cmb_employment.getValue();
        String source = cmb_source.getValue();
        String name = tb_name.getText();
        String contact = tb_contact.getText();
        String address = tb_address.getText();

        // Perform validation checks
        if (cityName == null || status == null || source == null || name.isEmpty() || contact.isEmpty() || address.isEmpty()) {
            // Display an error message indicating that all fields are required
            showAlert(AlertType.ERROR, "Error", "All fields are required.");
            return; // Exit the method if any field is empty
        }

        // Validate the name field to ensure it contains at least 2 capital letters
        int capitalCount = 0;
        for (char c : name.toCharArray()) {
            if (Character.isUpperCase(c)) {
                capitalCount++;
                if (capitalCount >= 2) {
                    break; // Break the loop if at least 2 capital letters are found
                }
            }
        }
        if (capitalCount < 2) {
            // Display an error message indicating that the name must contain at least 2 capital letters
            showAlert(AlertType.ERROR, "Error", "Name must contain at least 2 capital letters.");
            return; // Exit the method if name field is not valid
        }

        // Validate the contact field to ensure it contains between 11 and 13 digits
        if (contact.length() != 11 || !contact.matches("\\d+")) {
            // Display an error message indicating that the contact field must contain between 11 and 13 digits
            showAlert(AlertType.ERROR, "Error", "Contact must contain between 11 and 13 digits.");
            return; // Exit the method if contact field is not valid
        }

        if (isNameExists(name)) {
            showAlert(AlertType.ERROR, "Error", "Name already exists in the database.");
            return;
        }

        if (isContactExists(contact)) {
            showAlert(AlertType.ERROR, "Error", "Contact already exists in the database.");
            return;
        }

        // Retrieve corresponding primary key values from related tables
        int cityId = getPrimaryKey("tbl_city", "City_ID", "City", cityName);
        int statusId = getPrimaryKey("tbl_status", "Status_ID", "Status", status);
        int sourceId = getPrimaryKey("tbl_source", "Source_ID", "Source", source);

        // Now you have the primary key values, you can insert a new record into tbl_customer
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_im_finals", "root", "");
             PreparedStatement statement = connection.prepareStatement("INSERT INTO tbl_customer (City_ID, Status_ID, Source_ID, Customer_Name, Customer_Contact, Address) VALUES (?, ?, ?, ?, ?, ?)")) {
            statement.setInt(1, cityId);
            statement.setInt(2, statusId);
            statement.setInt(3, sourceId);
            statement.setString(4, name);
            statement.setString(5, contact);
            statement.setString(6, address);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any SQL exception here
        }
        showAlert(AlertType.INFORMATION, "Success", "Successfully added");

        populateTableView();

        clearTextFields();
    }

    private void clearTextFields() {
        // Clear the text of each text field
        tb_contact.clear();
        tb_name.clear();
        tb_address.clear();  // Example text field, add others as needed
        // Add more text fields here if needed
    }

    private boolean isContactExists(String contact) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_im_finals", "root", "");
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM tbl_customer WHERE Customer_Contact = ?")) {
            statement.setString(1, contact);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next(); // Contact exists if resultSet has any rows
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "An error occurred while checking contact existence.");
            return true; // Assuming we handle database error as if contact exists to prevent unintended actions
        }
    }

    private boolean isNameExists(String name) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_im_finals", "root", "");
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM tbl_customer WHERE Customer_Name = ?")) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "An error occurred while checking name existence.");
            return true; // Assuming we handle database error as if name exists to prevent unintended actions
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
    void searchDetails(ActionEvent event) {
        String searchText = tb_search.getText().toLowerCase(); // Get the search text

        // Set the filter predicate based on the search text
        filteredData.setPredicate(detail -> {
            if (searchText == null || searchText.isEmpty()) {
                return true; // Display all data if search text is empty
            }
            // Filter by Name, Contact, and City
            return detail.getName().toLowerCase().contains(searchText) ||
                    detail.getContact().toLowerCase().contains(searchText) ||
                    detail.getCity().toLowerCase().contains(searchText);
        });

        // Update the TableView to reflect the filtered data
        table_tenant.setItems(filteredData);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        populateComboBox(cmb_city, "SELECT City FROM tbl_city");
        populateComboBox(cmb_employment, "SELECT Status FROM tbl_status");
        populateComboBox(cmb_source, "SELECT Source FROM tbl_source");

        clm_customerID.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        clm_contact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        clm_employment.setCellValueFactory(new PropertyValueFactory<>("employment"));
        clm_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        clm_city.setCellValueFactory(new PropertyValueFactory<>("city"));
        clm_address.setCellValueFactory(new PropertyValueFactory<>("address"));

        populateTableView();

    }

    private void populateTableView() {
        // Clear existing data in the TableView
        table_tenant.getItems().clear();

        // Retrieve data from the database and populate the table view
        ObservableList<CustomerDetails_Data> data = FXCollections.observableArrayList();
        // Fetch data from the database and add it to 'data' list
        data.addAll(fetchDataFromDatabase());

        // Set the data to the table view

        filteredData = new FilteredList<>(data);

        table_tenant.setItems(data);
    }

    private List<CustomerDetails_Data> fetchDataFromDatabase() {
        List<CustomerDetails_Data> dataList = new ArrayList<>();
        String url = "jdbc:mysql://localhost:3306/db_im_finals";
        String username = "root";
        String password = "";

        // Modify your SQL query to include an ORDER BY clause to sort by Customer_ID
        String sql = "SELECT c.Customer_ID, c.Customer_Name, c.Customer_Contact, ci.City, s.Status, c.Address " +
                "FROM tbl_customer c " +
                "JOIN tbl_status s ON c.Status_ID = s.Status_ID " +
                "JOIN tbl_city ci ON c.City_ID = ci.City_ID " +
                "ORDER BY c.Customer_ID DESC"; // ASC for ascending order, DESC for descending

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int customerId = rs.getInt("Customer_ID");
                String name = rs.getString("Customer_Name");
                String contact = rs.getString("Customer_Contact");
                String city = rs.getString("City");
                String employment = rs.getString("Status");
                String address = rs.getString("Address");

                CustomerDetails_Data customerData = new CustomerDetails_Data(customerId, name, contact, city, employment, address);
                dataList.add(customerData);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching customer data: " + e.getMessage());
        }

        return dataList;
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

    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void searchCity(KeyEvent event) {

    }

    @FXML
    void MovetoContractPage(ActionEvent event) {
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
    void updateCustomerDetails(ActionEvent event) {
        try {
            // Retrieve data from UI components
            CustomerDetails_Data selectedCustomer = table_tenant.getSelectionModel().getSelectedItem();
            int customerId = selectedCustomer.getCustomerId();
            String newName = tb_name.getText();
            String newContact = tb_contact.getText();
            String newAddress = tb_address.getText();

            // Validate input data
            int capitalCount = 0;
            for (char c : newName.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    capitalCount++;
                    if (capitalCount >= 2) {
                        break; // Break the loop if at least 2 capital letters are found
                    }
                }
            }
            if (capitalCount < 2) {
                showAlert(AlertType.ERROR, "Error", "Name must contain at least 2 capital letters.");
                return;
            }

            // Validate the contact field to ensure it contains between 11 and 13 digits
            if (newContact.length() != 11 || !newContact.matches("\\d+")) {
                showAlert(AlertType.ERROR, "Error", "Contact must contain 11 digits.");
                return;
            }

            // Update database
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_im_finals", "root", "");
                 PreparedStatement statement = connection.prepareStatement("UPDATE tbl_customer SET Customer_Name = ?, Customer_Contact = ?, Address = ? WHERE Customer_ID = ?")) {

                statement.setString(1, newName);
                statement.setString(2, newContact);
                statement.setString(3, newAddress);
                statement.setInt(4, customerId);

                int rowsUpdated = statement.executeUpdate();

                if (rowsUpdated > 0) {
                    // Show success message
                    showAlert(AlertType.INFORMATION, "Success", "Customer details have been successfully updated.");

                    // Refresh table view with updated data
                    populateTableView();
                } else {
                    showAlert(AlertType.ERROR, "Error", "Failed to update customer details.");
                }
            } catch (SQLException e) {
                System.err.println("Error updating customer details: " + e.getMessage());
            }
        } catch (NullPointerException e) {
            showAlert(AlertType.ERROR, "Error", "Please select a customer to update.");
        }
    }



}

