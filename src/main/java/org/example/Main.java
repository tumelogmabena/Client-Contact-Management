package org.example;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.Date;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        connect();
        //createNewTable();
        //insert("Tumelo","tumelomabena@icloud.com");

        System.out.println("hello World");
       // add();
        update();
        //column();
        //showTableInfo();
        printOutData();

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
        String sql = "ALTER TABLE users ADD COLUMN dob DATE";

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
        String sql = "INSERT INTO users(name, email, dob) VALUES(?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
        String sql = "UPDATE users SET name = ?," +
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
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
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


    private static void printOutData() {
        String url = "jdbc:sqlite:clients.db";
        String sql = "SELECT id, name, email, dob FROM users";

        try (Connection conn = DriverManager.getConnection(url);
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(rs.getInt("id") + "\t" +
                        rs.getString("name") + "\t" +
                        rs.getString("email") + "\t" +
                        rs.getDate("dob"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void insert(String name, String email) {
        String url = "jdbc:sqlite:clients.db";
        String sql = "INSERT INTO users(name, email, dob) VALUES(?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);

            pstmt.executeUpdate();
            System.out.println("Data inserted.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
    }
}
