package com.example.im_exam_m3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.util.converter.IntegerStringConverter;


public class Rooms {

    @FXML
    private Button btn_home;

    @FXML
    private Button btn_search;

    @FXML
    private Button btn_update;

    @FXML
    private TableColumn<RoomsData, Integer> clm_bldg;

    @FXML
    private TableColumn<RoomsData, Integer> clm_occupants;

    @FXML
    private TableColumn<RoomsData, Integer> clm_price;

    @FXML
    private TableColumn<RoomsData, String> clm_room;

    @FXML
    private TableColumn<RoomsData, Integer> clm_status;

    @FXML
    private TableColumn<RoomsData, Integer> clm_type;

    @FXML
    private TableView<RoomsData> table_Rooms;

    @FXML
    private ComboBox<String> cmb_occupants;

    @FXML
    private ComboBox<String> cmb_status;

    @FXML
    private TextField tb_price;

    @FXML
    private TextField tb_search;

    Map<String, Integer> statusMap;

    private ObservableList<RoomsData> data = FXCollections.observableArrayList();

    private FilteredList<RoomsData> filteredData = new FilteredList<>(data);

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
    void SearchRoom(ActionEvent event) {
        String searchText = tb_search.getText().toLowerCase(); // Get the search text

        // Set the filter predicate based on the search text
        filteredData.setPredicate(room -> {
            if (searchText == null || searchText.isEmpty()) {
                return true; // Display all data if search text is empty
            }
            // Filter by Room_ID and Room_Status
            return room.getRoom_ID().toLowerCase().contains(searchText) ||
                    room.getRoom_Status().toLowerCase().contains(searchText);
        });

        // Update the TableView to reflect the filtered data
        table_Rooms.setItems(filteredData);
    }

    @FXML
    void UpdateTable(ActionEvent event) {
        // Get the selected item from the table
        RoomsData selectedRoom = table_Rooms.getSelectionModel().getSelectedItem();
        if (selectedRoom != null) {
            // Get the new status from the combo box
            String newStatus = cmb_status.getValue();

            // Get the new occupants from the combo box
            String newOccupants = cmb_occupants.getValue();

            // Get the new price from the text field
            Integer newPrice = null;
            try {
                newPrice = Integer.parseInt(tb_price.getText());
            } catch (NumberFormatException e) {
                // Handle invalid input for price (e.g., non-numeric input)
                System.err.println("Invalid input for price: " + e.getMessage());
            }

            // Update the database with the new status, occupants, and price of the selected room
            updateDatabase(selectedRoom.getRoom_ID(), newStatus, newOccupants, newPrice);

            // Reload the table with the updated data
            loadDataFromDatabase();
        } else {
            // Show an error message if no room is selected
            System.out.println("Please select a room to update.");
        }
    }


    private void updateDatabase(String roomID, String newStatus, String newOccupants, Integer newPrice) {
        String url = "jdbc:mysql://localhost:3306/db_im_finals";
        String username = "root";
        String password = "";

        StringBuilder sqlBuilder = new StringBuilder("UPDATE tbl_rooms SET ");
        List<Object> params = new ArrayList<>();

        // Check if new status is provided
        if (newStatus != null) {
            sqlBuilder.append("RoomStatus_ID = ?, ");
            params.add(convertStatusToID(newStatus));
        }

        // Check if new occupants is provided
        if (newOccupants != null) {
            sqlBuilder.append("Room_occupants = ?, ");
            params.add(newOccupants);
        }

        // Check if new price is provided
        if (newPrice != null) {
            sqlBuilder.append("Room_price = ?, ");
            params.add(newPrice);
        }

        // Remove the trailing comma and space
        sqlBuilder.delete(sqlBuilder.length() - 2, sqlBuilder.length());

        // Add WHERE clause
        sqlBuilder.append(" WHERE Room_ID = ?");
        params.add(roomID);

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sqlBuilder.toString())) {

            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof String) {
                    stmt.setString(i + 1, (String) param);
                }
            }

            // Execute the update query
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(AlertType.INFORMATION, "Success", "Successfully added");
            } else {
                System.out.println("Failed to update room details.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating room details: " + e.getMessage());
        }

    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void initialize() {

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) { // Only allow digits
                return change;
            }
            return null; // Reject the change if it contains non-numeric characters
        };

        // Create a TextFormatter with the filter
        TextFormatter<Integer> textFormatter = new TextFormatter<>(new IntegerStringConverter(), 0, filter);

        // Apply the TextFormatter to the TextField
        tb_price.setTextFormatter(textFormatter);

        ObservableList<String> occupantOptions = FXCollections.observableArrayList();
        for (int i = 1; i <= 10; i++) {
            occupantOptions.add(String.valueOf(i));
        }
        // Add "Commercial" option
        occupantOptions.add("Commercial");
        occupantOptions.add("Under Maintenance");
        occupantOptions.add("Vacant");
        cmb_occupants.setItems(occupantOptions);

        ObservableList<String> statusOptions = FXCollections.observableArrayList("Under Maintenance", "Vacant", "Occupied", "Not Available","Maintenance Required");
        cmb_status.setItems(statusOptions);

        // Initialize columns
        clm_room.setCellValueFactory(new PropertyValueFactory<>("Room_ID"));
        clm_bldg.setCellValueFactory(new PropertyValueFactory<>("Building_ID"));
        clm_type.setCellValueFactory(new PropertyValueFactory<>("Room_Type"));
        clm_status.setCellValueFactory(new PropertyValueFactory<>("Room_Status"));
        clm_occupants.setCellValueFactory(new PropertyValueFactory<>("Room_occupants"));
        clm_price.setCellValueFactory(new PropertyValueFactory<>("Room_price"));

        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        // Clear existing data in the TableView
        table_Rooms.getItems().clear();

        // Retrieve data from the database and populate the table view
        ObservableList<RoomsData> data = FXCollections.observableArrayList();
        // Fetch data from the database and add it to 'data' list
        data.addAll(fetchDataFromDatabase());

        // Set the data to the table view
        table_Rooms.setItems(data);

        filteredData = new FilteredList<>(data);

    }

    private List<RoomsData> fetchDataFromDatabase() {
        List<RoomsData> dataList = new ArrayList<>();
        String url = "jdbc:mysql://localhost:3306/db_im_finals";
        String username = "root";
        String password = "";

        // Modify your SQL query to include an ORDER BY clause to sort by Room_ID
        String sql = "SELECT r.Room_ID, r.Building_ID, r.Room_occupants, r.Room_price, t.Room_Type, s.Room_Status " +
                "FROM tbl_rooms r " +
                "JOIN tbl_room_type t ON r.RoomType_ID = t.RoomType_ID " +
                "JOIN tbl_room_status s ON r.RoomStatus_ID = s.RoomStatus_ID " +
                "ORDER BY r.Room_ID ASC"; // ASC for ascending order, DESC for descending

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String Room_ID = rs.getString("Room_ID");
                Integer Building_ID = rs.getInt("Building_ID");
                String Room_Type = rs.getString("Room_Type");
                String Room_Status = rs.getString("Room_status");
                String Room_occupants = rs.getString("Room_occupants");
                Integer Room_price = rs.getInt("Room_price");

                RoomsData Rooms = new RoomsData(Room_ID, Building_ID, Room_Type, Room_Status, Room_occupants, Room_price);

                dataList.add(Rooms);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching participant data: " + e.getMessage());
        }

        return dataList;
    }

    private Integer convertStatusToID(String status) {
        Map<String, Integer> statusMap = new HashMap<>();
        statusMap.put("Under Maintenance", 1);
        statusMap.put("Vacant", 2);
        statusMap.put("Occupied", 3);
        statusMap.put("Not Available", 4);
        statusMap.put("Maintenance Required", 5);
        return statusMap.get(status);
    }
}
