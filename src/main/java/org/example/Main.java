package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        connect();
        //createNewTable();
       // insert("Tumelo","tumelomabena@icloud.com");
        printOutData();

    }

    private static void printOutData() {
        String url = "jdbc:sqlite:clients.db";
        String sql = "SELECT id, name, email FROM users";

        try (Connection conn = DriverManager.getConnection(url);
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(rs.getInt("id") + "\t" +
                        rs.getString("name") + "\t" +
                        rs.getString("email"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void insert(String name, String email) {
        String url = "jdbc:sqlite:clients.db";
        String sql = "INSERT INTO users(name, email) VALUES(?, ?)";

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
}
