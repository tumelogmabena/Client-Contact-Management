package org.example;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.PreparedStatement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
@SpringBootApplication
public class Client_Vault {

    enum actions{
        View,
        Edit,
        Add,
        Delete
    }


    public static void main(String[] args) {
        SpringApplication.run(Client_Vault.class, args);

      Scanner scanner = new Scanner(System.in);
      boolean running = true;
      while (running) {
          System.out.println("Welcome to ClientVault, what would you like to see on our platform today. Please enter a number for an option from the menu below\n" +
                  "1.View Clients\n"+
                  "2.Edit Client Info\n" +
                  "3.Add a New Client\n" +
                  "4.Delete a Client\n" +
                  "5.Close Application");


        int useropt;
        useropt= Integer.parseInt(scanner.nextLine());

        switch (useropt) {
            case 1:
                printOutData();
                return;
            case 2:
                update();
                return;
            case 3:
                add();
                return;
            case 4:
                delete();
            case 5:
                System.exit(0);
            default:
                System.out.println("Invalid option, try again.");
                break;
        }


       }
        connect();
        createNewTable();
        add();
        update();
        column();
        showTableInfo();
        populate();

        delete();
        zipcode();
        printOutData();

    }

    private static void populate() {
        String url = "jdbc:sqlite:clients.db";
        String sql = "INSERT INTO users(name, email, dob) VALUES(?, ?, ?)";

        String[] firstNames = {"James", "John", "Robert", "Michael", "William",
                "David", "Richard", "Joseph", "Thomas", "Charles",
                "Mary", "Patricia", "Jennifer", "Linda", "Barbara",
                "Elizabeth", "Susan", "Jessica", "Sarah", "Karen"};

        String[] lastNames = {"Smith", "Johnson", "Williams", "Brown", "Jones",
                "Garcia", "Miller", "Davis", "Wilson", "Taylor",
                "Anderson", "Thomas", "Jackson", "White", "Harris",
                "Martin", "Thompson", "Young", "Allen", "King"};

        try (Connection conn = DriverManager.getConnection(url);
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < 10000; i++) {
                // generate random name
                String name = firstNames[(int)(Math.random() * firstNames.length)] + " " +
                        lastNames[(int)(Math.random() * lastNames.length)];

                // generate email from name
                String email = name.toLowerCase().replace(" ", ".") + i + "@email.com";

               // generate random date of birth between 1950 and 2005
                int year = 1950 + (int)(Math.random() * 55);
                int month = 1 + (int)(Math.random() * 12);
                int day = 1 + (int)(Math.random() * 28);
                Date dob = Date.valueOf(year + "-" +
                        String.format("%02d", month) + "-" +
                        String.format("%02d", day));

                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setDate(3, dob);
                pstmt.executeUpdate();
            }
            System.out.println("10 000 entries inserted.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void zipcode() {
        String url = "jdbc:sqlite:clients.db";
        String sql = "UPDATE users SET zipcode = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url);
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 1; i <= 10005; i++) {
                int zipcode = 1000 + (int)(Math.random() * 999); // generates between 1000-9999
                pstmt.setInt(1, zipcode);
                pstmt.setInt(2, i); // matches each existing row by id
                pstmt.executeUpdate();
            }
            System.out.println("10 005 entries inserted.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void printOutData() {
        String url = "jdbc:sqlite:clients.db";
        String sql = "SELECT id, name, email, dob, zipcode FROM users";

        try (Connection conn = DriverManager.getConnection(url);
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(rs.getInt("id") + "\t" +
                        rs.getString("name") + "\t" +
                        rs.getString("email") + "\t" +
                        rs.getDate("dob") + "\t" +
                        rs.getInt("zipcode"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("would you like to: \n" +
                "1.Edit user info \n"+
                "2. Add user \n" +
                "3. Delete a user \n" +
                "press enter to go home");
        Scanner scanner = new Scanner(System.in);
        int useropt;
        useropt= Integer.parseInt(scanner.nextLine());

        switch (useropt) {
            case 1:
               update();
                return;
            case 2:
                add();
                return;
            case 3:
                delete();
            default:
                System.out.println("\nPress Enter to return to menu..."); // add this
                new Scanner(System.in).nextLine();
                break;
        }


    }
    private static void delete() {
        String url = "jdbc:sqlite:clients.db";
        Scanner scanner = new Scanner(System.in);

        System.out.println("Which id would you like to delete?");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid id");
            return;
        }

        String sqlSelect = "SELECT id, name, email, dob FROM users WHERE id = ?";
        String sqlDelete = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement selectStmt = conn.prepareStatement(sqlSelect);
             PreparedStatement deleteStmt = conn.prepareStatement(sqlDelete)) {

            // Show the record (if it exists)
            selectStmt.setInt(1, id);
            try (java.sql.ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println(rs.getInt("id") + "\t" +
                            rs.getString("name") + "\t" +
                            rs.getString("email") + "\t" +
                            rs.getDate("dob"));
                } else {
                    System.out.println("No user found with id " + id);
                    return;
                }
            }

            // Confirm deletion
            System.out.print("Are you sure you want to delete this user? (y/N): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            if (confirm.equals("y") || confirm.equals("yes")) {
                deleteStmt.setInt(1, id);
                int affected = deleteStmt.executeUpdate();
                System.out.println("Deleted rows: " + affected);
            } else {
                System.out.println("Deletion canceled.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void createNewTable() {
        String url = "jdbc:sqlite:clients.db";
        String sql = "CREATE TABLE IF NOT EXISTS users(\n" //this will stop the code from creating multiple tables with the same name
                + "id INTEGER PRIMARY KEY, \n"
                + "name TEXT NOT NULL, \n"
                + "email TEXT NOT NULL \n"
                + ");";

        try(Connection conn = DriverManager.getConnection(url);
            java.sql.Statement stmt = conn.createStatement()){
            stmt.execute(sql);
            System.out.println(("Table has been created"));
        }
        catch (SQLException e){
            System.out.println(e.getMessage());//that get message will get an error message
        }


    }

    private static void connect() {
        String url = "jdbc:sqlite:clients.db";

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("Connected to database");

            }

        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }

    }

    private static void column() {
        String url = "jdbc:sqlite:clients.db";//this is the connection to the database
        String sql = "ALTER TABLE users ADD COLUMN zipcode DATE";

        try(Connection conn = DriverManager.getConnection(url);
            java.sql.Statement stmt = conn.createStatement()){
            stmt.execute(sql);
            System.out.println(("Column has been added"));
        }
        catch (SQLException e){
            if (e.getMessage().contains("duplicate column name")) {
                System.out.println("Column already exists, skipping.");
            } else {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void add() {
        Scanner userin = new Scanner(System.in);
        System.out.println("First Name");
        String name = userin.nextLine();
        System.out.println("email");
        String email = userin.nextLine();
        System.out.println("Enter date of birth (YYYY-MM-DD):");
        Date dob = Date.valueOf(userin.nextLine());
        String url = "jdbc:sqlite:clients.db";
        String sqlAdd = "INSERT INTO users(name, email, dob) VALUES(?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sqlAdd)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setDate(3, dob);
            pstmt.executeUpdate();
            System.out.println("Data inserted.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
    }


    private static void update() {
        String url = "jdbc:sqlite:clients.db";
        Scanner userin = new Scanner(System.in);
        String sqlUpdate = "UPDATE users SET name = ?," +
                "email = ?, " +
                "dob = ? " +
                "WHERE id = ?";
        System.out.println("which id would you like to edit");
        int id = Integer.parseInt(userin.nextLine());

        System.out.println("First Name");
        String name = userin.nextLine();
        System.out.println("email");
        String email = userin.nextLine();
        System.out.println("Enter date of birth (YYYY-MM-DD):");
        Date dob = java.sql.Date.valueOf((userin.nextLine()));


        try (Connection conn = DriverManager.getConnection(url);
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setDate(3, dob);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
            System.out.println("Data inserted.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
    }

    private static void showTableInfo() {
        String url = "jdbc:sqlite:clients.db";
        String sql = "PRAGMA table_info(users)";

        try (Connection conn = DriverManager.getConnection(url);
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(rs.getString("name") + " - " + rs.getString("type"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }



    private static void insert(String name, String email) {
        String url = "jdbc:sqlite:clients.db";
        String sqlInsert = "INSERT INTO users(name, email, dob) VALUES(?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);

            pstmt.executeUpdate();
            System.out.println("Data inserted.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
    }
}
