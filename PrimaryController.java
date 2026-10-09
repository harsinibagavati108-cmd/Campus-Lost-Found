package com.campus;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

public class PrimaryController {

    // Main views
    @FXML
    private ScrollPane homeView;

    @FXML
    private ScrollPane lostView;

    @FXML
    private ScrollPane foundView;

    @FXML
    private ScrollPane reportsView;

    // Home
    @FXML
    private ListView<String> recentReportsList;

    // Lost form
    @FXML
    private TextField lostNameField;

    @FXML
    private TextField lostLocationField;

    @FXML
    private ComboBox<String> lostCategoryBox;

    @FXML
    private TextArea lostDescriptionField;

    // Found form
    @FXML
    private TextField foundNameField;

    @FXML
    private TextField foundLocationField;

    @FXML
    private ComboBox<String> foundCategoryBox;

    @FXML
    private TextArea foundDescriptionField;

    @FXML
    private TextField keptAtField;

    // Reports table
    @FXML
    private TableView<LostItem> reportsTable;

    @FXML
    private TableColumn<LostItem, String> typeColumn;

    @FXML
    private TableColumn<LostItem, String> itemColumn;

    @FXML
    private TableColumn<LostItem, String> locationColumn;

    @FXML
    private TableColumn<LostItem, String> categoryColumn;

    @FXML
    private TableColumn<LostItem, String> keptAtColumn;

    private final ObservableList<LostItem> items =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        // Create database/table
        Database.createTable();

        // Load existing records
        items.addAll(Database.getAllItems());

        // Categories
        ObservableList<String> categories =
                FXCollections.observableArrayList(
                        "Electronics",
                        "Books",
                        "ID Card",
                        "Accessories",
                        "Clothing",
                        "Stationery",
                        "Other"
                );

        lostCategoryBox.setItems(categories);

        foundCategoryBox.setItems(
                FXCollections.observableArrayList(categories)
        );

        // Configure table columns
        typeColumn.setCellValueFactory(
                data -> data.getValue().typeProperty()
        );

        itemColumn.setCellValueFactory(
                data -> data.getValue().nameProperty()
        );

        locationColumn.setCellValueFactory(
                data -> data.getValue().locationProperty()
        );

        categoryColumn.setCellValueFactory(
                data -> data.getValue().categoryProperty()
        );

        keptAtColumn.setCellValueFactory(
                data -> data.getValue().keptAtProperty()
        );

        reportsTable.setItems(items);

        // Double-click a report
        reportsTable.setOnMouseClicked(this::handleReportClick);

        refreshRecentReports();
        setupKeyboardEvents();
        showHome();
    }
    // ================= KEYBOARD EVENTS =================

private void setupKeyboardEvents() {

    // Press ENTER in the lost-item name field
    lostNameField.setOnKeyPressed(event -> {

        if (event.getCode() == KeyCode.ENTER) {
            reportLostItem();
        }
    });

    // Press ENTER in the found-item name field
    foundNameField.setOnKeyPressed(event -> {

        if (event.getCode() == KeyCode.ENTER) {
            reportFoundItem();
        }
    });
}

    // ================= HOME =================

    @FXML
    private void showHome() {

        homeView.setVisible(true);
        homeView.setManaged(true);

        lostView.setVisible(false);
        lostView.setManaged(false);

        foundView.setVisible(false);
        foundView.setManaged(false);

        reportsView.setVisible(false);
        reportsView.setManaged(false);

        refreshRecentReports();
    }

    // ================= LOST FORM =================

    @FXML
    private void showLostForm() {

        homeView.setVisible(false);
        homeView.setManaged(false);

        lostView.setVisible(true);
        lostView.setManaged(true);

        foundView.setVisible(false);
        foundView.setManaged(false);

        reportsView.setVisible(false);
        reportsView.setManaged(false);

        lostNameField.requestFocus();
    }

    // ================= FOUND FORM =================

    @FXML
    private void showFoundForm() {

        homeView.setVisible(false);
        homeView.setManaged(false);

        lostView.setVisible(false);
        lostView.setManaged(false);

        foundView.setVisible(true);
        foundView.setManaged(true);

        reportsView.setVisible(false);
        reportsView.setManaged(false);

        foundNameField.requestFocus();
    }

    // ================= REPORTS =================

    @FXML
    private void showReports() {

        homeView.setVisible(false);
        homeView.setManaged(false);

        lostView.setVisible(false);
        lostView.setManaged(false);

        foundView.setVisible(false);
        foundView.setManaged(false);

        reportsView.setVisible(true);
        reportsView.setManaged(true);

        // Refresh table in case a new report was added
        reportsTable.setItems(items);
        reportsTable.refresh();
    }

    // ================= REPORT LOST ITEM =================

    @FXML
    private void reportLostItem() {

        String name = lostNameField.getText().trim();
        String location = lostLocationField.getText().trim();
        String category = lostCategoryBox.getValue();
        String description = lostDescriptionField.getText().trim();

        if (name.isEmpty()
                || location.isEmpty()
                || category == null) {

            showMessage(
                    "Please fill in Item Name, Location and Category.",
                    Alert.AlertType.WARNING
            );

            return;
        }

        // Check for an existing FOUND report
        LostItem match = findMatch(
                "LOST",
                name,
                location,
                category
        );

        // Save to database
        Database.insertItem(
                "LOST",
                name,
                location,
                category,
                description,
                ""
        );

        // Add to current application list
        LostItem newItem = new LostItem(
                "LOST",
                name,
                location,
                category,
                description,
                ""
        );

        items.add(newItem);

        refreshRecentReports();
        clearLostForm();

        if (match != null) {

            showMatchMessage(
                    "A possible match was found!\n\n"
                    + "Item: " + match.getName() + "\n"
                    + "Found at: " + match.getLocation() + "\n"
                    + "Currently kept at: "
                    + match.getKeptAt()
            );

        } else {

            showMessage(
                    "Your lost item has been reported successfully.",
                    Alert.AlertType.INFORMATION
            );
        }

        showHome();
    }

    // ================= REPORT FOUND ITEM =================

    @FXML
    private void reportFoundItem() {

        String name = foundNameField.getText().trim();
        String location = foundLocationField.getText().trim();
        String category = foundCategoryBox.getValue();
        String description = foundDescriptionField.getText().trim();
        String keptAt = keptAtField.getText().trim();

        if (name.isEmpty()
                || location.isEmpty()
                || category == null
                || keptAt.isEmpty()) {

            showMessage(
                    "Please fill in Item Name, Found Location, "
                    + "Category and Kept At.",
                    Alert.AlertType.WARNING
            );

            return;
        }

        // Check for an existing LOST report
        LostItem match = findMatch(
                "FOUND",
                name,
                location,
                category
        );

        // Save to database
        Database.insertItem(
                "FOUND",
                name,
                location,
                category,
                description,
                keptAt
        );

        // Add to current application list
        LostItem newItem = new LostItem(
                "FOUND",
                name,
                location,
                category,
                description,
                keptAt
        );

        items.add(newItem);

        refreshRecentReports();
        clearFoundForm();

        if (match != null) {

            showMatchMessage(
                    "Possible match found!\n\n"
                    + "Someone reported losing: "
                    + match.getName() + "\n\n"
                    + "Found at: " + location + "\n"
                    + "Currently kept at: " + keptAt
            );

        } else {

            showMessage(
                    "Found item has been reported successfully.",
                    Alert.AlertType.INFORMATION
            );
        }

        showHome();
    }

    // ================= MATCHING =================

    private LostItem findMatch(
            String currentType,
            String name,
            String location,
            String category) {

        for (LostItem item : items) {

            // LOST report searches for FOUND report
            if (currentType.equals("LOST")
                    && item.getType().equals("FOUND")) {

                if (isSameReport(
                        item,
                        name,
                        location,
                        category)) {

                    return item;
                }
            }

            // FOUND report searches for LOST report
            if (currentType.equals("FOUND")
                    && item.getType().equals("LOST")) {

                if (isSameReport(
                        item,
                        name,
                        location,
                        category)) {

                    return item;
                }
            }
        }

        return null;
    }

    private boolean isSameReport(
            LostItem item,
            String name,
            String location,
            String category) {

        return item.getName().trim().equalsIgnoreCase(name.trim())
                && item.getLocation().trim()
                        .equalsIgnoreCase(location.trim())
                && item.getCategory().trim()
                        .equalsIgnoreCase(category.trim());
    }

    // ================= RECENT REPORTS =================

    private void refreshRecentReports() {

        recentReportsList.getItems().clear();

        if (items.isEmpty()) {

            recentReportsList.getItems().add(
                    "No reports yet. Be the first to report an item!"
            );

            return;
        }

        for (LostItem item : items) {

            String icon;

            if (item.getType().equals("LOST")) {
                icon = "🔴";
            } else {
                icon = "🟢";
            }

            String report = icon
                    + "  "
                    + item.getType()
                    + "  •  "
                    + item.getName()
                    + "  •  "
                    + item.getLocation();

            recentReportsList.getItems().add(report);
        }
    }

    // ================= TABLE MOUSE EVENT =================

    private void handleReportClick(MouseEvent event) {

        if (event.getClickCount() == 2) {

            LostItem selected =
                    reportsTable.getSelectionModel()
                            .getSelectedItem();

            if (selected != null) {

                String message =
                        "Type: " + selected.getType()
                        + "\nItem: " + selected.getName()
                        + "\nLocation: "
                        + selected.getLocation()
                        + "\nCategory: "
                        + selected.getCategory();

                if (selected.getType().equals("FOUND")) {

                    message += "\nKept at: "
                            + selected.getKeptAt();
                }

                showMessage(
                        message,
                        Alert.AlertType.INFORMATION
                );
            }
        }
    }

    // ================= CLEAR FORMS =================

    @FXML
    private void clearLostForm() {

        lostNameField.clear();
        lostLocationField.clear();
        lostCategoryBox.setValue(null);
        lostDescriptionField.clear();
    }

    @FXML
    private void clearFoundForm() {

        foundNameField.clear();
        foundLocationField.clear();
        foundCategoryBox.setValue(null);
        foundDescriptionField.clear();
        keptAtField.clear();
    }

    // ================= ALERTS =================

    private void showMessage(
            String message,
            Alert.AlertType type) {

        Alert alert = new Alert(type);

        alert.setTitle("Campus Lost & Found");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private void showMatchMessage(String message) {

        Alert alert = new Alert(
                Alert.AlertType.INFORMATION
        );

        alert.setTitle("Possible Match Found!");
        alert.setHeaderText("🔍 Possible Match Found!");
        alert.setContentText(message);

        alert.showAndWait();
    }
}