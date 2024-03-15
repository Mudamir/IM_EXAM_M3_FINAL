package com.example.im_exam_m3;

import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;




public class RentHistory {

    @FXML
    private Button btn_home;

    @FXML
    private Button btn_search;

    @FXML
    private TableColumn<RentHistoryData, String> clm_RoomID;

    @FXML
    private TableColumn<RentHistoryData, Integer> clm_customerID;

    @FXML
    private TableColumn<RentHistoryData, String> clm_endDate;

    @FXML
    private TableColumn<RentHistoryData, String> clm_name;

    @FXML
    private TableColumn<RentHistoryData, String> clm_startdate;

    @FXML
    private TextField tb_search;

    @FXML
    private TableView<RentHistoryData> table_rentHistory;


    @FXML
    void MovetoHomePage(ActionEvent event) {
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

        // Create a filtered list based on the data in table_rentHistory
        FilteredList<RentHistoryData> filteredList = new FilteredList<>(table_rentHistory.getItems());

        // Set the filter predicate based on the search text
        filteredList.setPredicate(rentHistory -> {
            if (searchText == null || searchText.isEmpty()) {
                return true; // Display all data if search text is empty
            }
            // Filter by name and roomID
            return rentHistory.getName().toLowerCase().contains(searchText) ||
                    rentHistory.getRoomID().toLowerCase().contains(searchText);
        });

        // Set the filtered list as the items in the TableView
        table_rentHistory.setItems(filteredList);

        // If the search text is empty, load all rent history data
        if (searchText == null || searchText.isEmpty()) {
            loadRentHistoryData();
        }
    }





    @FXML
    void initialize() {
        clm_RoomID.setCellValueFactory(new PropertyValueFactory<>("RoomID"));
        clm_customerID.setCellValueFactory(new PropertyValueFactory<>("CustomerID"));
        clm_startdate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        clm_endDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        clm_name.setCellValueFactory(new PropertyValueFactory<>("name"));


        loadRentHistoryData();
    }


    private void loadRentHistoryData() {
        ObservableList<RentHistoryData> rentHistoryList = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_im_finals", "root", "");
             PreparedStatement statement = connection.prepareStatement("SELECT c.Customer_name, c.Customer_ID, co.Room_ID, co.Date_start, co.Date_end FROM tbl_contract co " +
                  "JOIN tbl_customer c ON co.Customer_ID = c.Customer_ID");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int customerID = resultSet.getInt("Customer_ID");
                String roomID = resultSet.getString("Room_ID");
                String startDate = resultSet.getString("Date_start");
                String endDate = resultSet.getString("Date_end");
                String name = resultSet.getString("Customer_Name");

                rentHistoryList.add(new RentHistoryData(customerID, roomID, startDate, endDate, name));

            }

            // Set the items in the TableView
            table_rentHistory.setItems(rentHistoryList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
