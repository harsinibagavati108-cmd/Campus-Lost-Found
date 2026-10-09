package com.campus;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LostItem {

    private final StringProperty type;
    private final StringProperty name;
    private final StringProperty location;
    private final StringProperty category;
    private final StringProperty description;
    private final StringProperty keptAt;

    // Main constructor
    public LostItem(String type,
                    String name,
                    String location,
                    String category,
                    String description,
                    String keptAt) {

        this.type = new SimpleStringProperty(type);
        this.name = new SimpleStringProperty(name);
        this.location = new SimpleStringProperty(location);
        this.category = new SimpleStringProperty(category);
        this.description = new SimpleStringProperty(description);
        this.keptAt = new SimpleStringProperty(keptAt);
    }

    // Old constructor - keeps our existing code working for now
    public LostItem(String name,
                    String location,
                    String category,
                    String description) {

        this("LOST", name, location, category, description, "");
    }

    public StringProperty typeProperty() {
        return type;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty locationProperty() {
        return location;
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty keptAtProperty() {
        return keptAt;
    }

    public String getType() {
        return type.get();
    }

    public String getName() {
        return name.get();
    }

    public String getLocation() {
        return location.get();
    }

    public String getCategory() {
        return category.get();
    }

    public String getDescription() {
        return description.get();
    }

    public String getKeptAt() {
        return keptAt.get();
    }
}