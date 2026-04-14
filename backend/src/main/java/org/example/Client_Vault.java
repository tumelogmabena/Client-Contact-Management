package org.example;
import org.example.server.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.IOException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.Scanner;

@SpringBootApplication
public class Client_Vault {

    enum Actions {
        View, Edit, Add, Delete
    }

    public static void main(String[] args) {
        SpringApplication.run(Client_Vault.class, args);

        try {
            Server.start();
        } catch (IOException e) {
            System.out.println("Failed to start server: " + e.getMessage());
        }

        // etc
        connect();
        createNewTable();
        column();
        //populate();

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\nWelcome to ClientVault, what would you like to see on our platform today.");
            System.out.println("Please enter a number for an option from the menu below");
            System.out.println("1. View Clients");
            System.out.println("2. Edit Client Info");
            System.out.println("3. Add a New Client");
            System.out.println("4. Delete a Client");
            System.out.println("5. Close Application");

            try {
                int useropt = Integer.parseInt(scanner.nextLine());

                switch (useropt) {
                    case 1:
                        printOutData();
                        break;
                    case 2:
                        update();
                        break;
                    case 3:
                        add();
                        break;
                    case 4:
                        delete();
                        break;
                    case 5:
                        System.out.println("Goodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option, try again.");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static void connect() {
        String url = "jdbc:sqlite:Client-Contact-Management-System/backend/database/clients.db";
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("Connected to database");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void createNewTable() {
        String url = "jdbc:sqlite:Client-Contact-Management-System/backend/database/clients.db";
        String sql = "CREATE TABLE IF NOT EXISTS users("
                + "id INTEGER PRIMARY KEY, "
                + "name TEXT NOT NULL, "
                + "lastname TEXT NOT NULL, "
                + "email TEXT NOT NULL, "
                + "dob DATE, "
                + "zipcode INTEGER"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table is ready.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void column() {
        String url = "jdbc:sqlite:Client-Contact-Management-System/backend/database/clients.db";
        String sql = "ALTER TABLE users ADD COLUMN zipcode INTEGER";

        try (Connection conn = DriverManager.getConnection(url);
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Zipcode column added.");
        } catch (SQLException e) {
            if (e.getMessage().contains("duplicate column name")) {
                System.out.println("Zipcode column already exists, skipping.");
            } else {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void populate() {
        String url = "jdbc:sqlite:Client-Contact-Management-System/backend/database/clients.db";
        String sql = "INSERT INTO users(name, lastname, email, dob, zipcode) VALUES(?, ?, ?, ?, ?)";
        String[] firstNames = {"James", "John", "Robert", "Michael", "William",
                "David", "Richard", "Joseph", "Thomas", "Charles",
                "Mary", "Patricia", "Jennifer", "Linda", "Barbara",
                "Elizabeth", "Susan", "Jessica", "Sarah", "Karen"};
        String[] lastNames = {"Smith", "Johnson", "Williams", "Brown", "Jones",
                "Garcia", "Miller", "Davis", "Wilson", "Taylor",
                "Anderson", "Thomas", "Jackson", "White", "Harris",
                "Martin", "Thompson", "Young", "Allen", "King"};

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < 10000; i++) {
                String firstName = firstNames[(int)(Math.random() * firstNames.length)];
                String lastName  = lastNames[(int)(Math.random() * lastNames.length)];
                String email     = firstName.toLowerCase() + "." + lastName.toLowerCase() + i + "@email.com";
                int year  = 1950 + (int)(Math.random() * 55);
                int month = 1    + (int)(Math.random() * 12);
                int day   = 1    + (int)(Math.random() * 28);
                Date dob  = Date.valueOf(year + "-" +
                        String.format("%02d", month) + "-" +
                        String.format("%02d", day));
                int zipcode = 1000 + (int)(Math.random() * 999); // 1000–9999

                pstmt.setString(1, firstName);
                pstmt.setString(2, lastName);
                pstmt.setString(3, email);
                pstmt.setDate(4, dob);
                pstmt.setInt(5, zipcode);
                pstmt.executeUpdate();
            }
            System.out.println("10 000 entries inserted.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void printOutData() {
        String url = "jdbc:sqlite:Client-Contact-Management-System/backend/database/clients.db";
        String sql = "SELECT id, name, lastname, email, dob, zipcode FROM users";

        try (Connection conn = DriverManager.getConnection(url);
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\nID\tName\t\tLastName\t\tEmail\t\t\tDOB\t\tZipcode");
            System.out.println("----------------------------------------------------------------------");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + "\t" +
                        rs.getString("name") + "\t" +
                        rs.getString("lastname") + "\t" +
                        rs.getString("email") + "\t" +
                        rs.getDate("dob") + "\t" +
                        rs.getInt("zipcode"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void add() {
        Scanner userin = new Scanner(System.in);
        System.out.println("First Name:");
        String name = userin.nextLine();
        System.out.println("Last Name:");
        String lname = userin.nextLine();
        System.out.println("Email:");
        String email = userin.nextLine();
        System.out.println("Date of birth (YYYY-MM-DD):");
        Date dob = Date.valueOf(userin.nextLine());

        String url = "jdbc:sqlite:Client-Contact-Management-System/backend/database/clients.db";
        String sqlAdd = "INSERT INTO users(name,lastname, email, dob) VALUES(?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sqlAdd)) {
            pstmt.setString(1, name);
            pstmt.setString(2, lname);
            pstmt.setString(3, email);
            pstmt.setDate(4, dob);
            pstmt.executeUpdate();
            System.out.println("Client added successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void update() {
        String url = "jdbc:sqlite:Client-Contact-Management-System/backend/database/clients.db";
        Scanner userin = new Scanner(System.in);

        System.out.println("Which ID would you like to edit?");
        int id = Integer.parseInt(userin.nextLine());

        System.out.println("New First Name:");
        String name = userin.nextLine();
        System.out.println("New Last Name:");
        String lname = userin.nextLine();
        System.out.println("New Email:");
        String email = userin.nextLine();
        System.out.println("New Date of birth (YYYY-MM-DD):");
        Date dob = Date.valueOf(userin.nextLine());

        String sqlUpdate = "UPDATE users SET name = ?, lastname = ?, email = ?, dob = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {
            pstmt.setString(1, name);
            pstmt.setString(2, lname);
            pstmt.setString(3, email);
            pstmt.setDate(4, dob);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
            System.out.println("Client updated successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void delete() {
        String url = "jdbc:sqlite:Client-Contact-Management-System/backend/database/clients.db";
        Scanner scanner = new Scanner(System.in);

        System.out.println("Which ID would you like to delete?");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            return;
        }

        String sqlSelect = "SELECT id, name, lastname, email, dob FROM users WHERE id = ?";
        String sqlDelete = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement selectStmt = conn.prepareStatement(sqlSelect);
             PreparedStatement deleteStmt = conn.prepareStatement(sqlDelete)) {

            selectStmt.setInt(1, id);
            try (java.sql.ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println(rs.getInt("id") + "\t" +
                            rs.getString("name") + "\t" +
                            rs.getString("lastname") + "\t" +
                            rs.getString("email") + "\t" +
                            rs.getDate("dob"));
                } else {
                    System.out.println("No user found with ID " + id);
                    return;
                }
            }

            System.out.print("Are you sure you want to delete this client? (y/N): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            if (confirm.equals("y") || confirm.equals("yes")) {
                deleteStmt.setInt(1, id);
                int affected = deleteStmt.executeUpdate();
                System.out.println("Deleted " + affected + " client(s).");
            } else {
                System.out.println("Deletion cancelled.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}