package com.campus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private static final String URL = "jdbc:sqlite:campus_lost_found.db";

    // Connect to SQLite
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Create the table
    public static void createTable() {

        String sql = """
                CREATE TABLE IF NOT EXISTS lost_items (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    report_type TEXT NOT NULL DEFAULT 'LOST',
                    item_name TEXT NOT NULL,
                    location TEXT NOT NULL,
                    category TEXT NOT NULL,
                    description TEXT,
                    kept_at TEXT
                )
                """;

        try (Connection connection = connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.executeUpdate();

            // Add new columns if an older database already exists
            addColumnIfMissing(connection, "report_type",
                    "TEXT DEFAULT 'LOST'");

            addColumnIfMissing(connection, "kept_at",
                    "TEXT DEFAULT ''");

            System.out.println("Database table ready.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add a column only if it doesn't already exist
    private static void addColumnIfMissing(
            Connection connection,
            String columnName,
            String columnDefinition) throws SQLException {

        String sql = "ALTER TABLE lost_items ADD COLUMN "
                + columnName + " " + columnDefinition;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.executeUpdate();

        } catch (SQLException e) {

            // Ignore error if the column already exists
            if (!e.getMessage().contains("duplicate column name")) {
                throw e;
            }
        }
    }

    // Insert a report
    public static void insertItem(
            String type,
            String name,
            String location,
            String category,
            String description,
            String keptAt) {

        String sql = """
                INSERT INTO lost_items
                (report_type, item_name, location,
                 category, description, kept_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, type);
            statement.setString(2, name);
            statement.setString(3, location);
            statement.setString(4, category);
            statement.setString(5, description);
            statement.setString(6, keptAt);

            statement.executeUpdate();

            System.out.println("Report saved to database.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get all reports
    public static List<LostItem> getAllItems() {

        List<LostItem> items = new ArrayList<>();

        String sql = """
                SELECT report_type, item_name, location,
                       category, description, kept_at
                FROM lost_items
                ORDER BY id DESC
                """;

        try (Connection connection = connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             java.sql.ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {

                String type =
                        resultSet.getString("report_type");

                String name =
                        resultSet.getString("item_name");

                String location =
                        resultSet.getString("location");

                String category =
                        resultSet.getString("category");

                String description =
                        resultSet.getString("description");

                String keptAt =
                        resultSet.getString("kept_at");

                items.add(new LostItem(
                        type,
                        name,
                        location,
                        category,
                        description,
                        keptAt
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }
}