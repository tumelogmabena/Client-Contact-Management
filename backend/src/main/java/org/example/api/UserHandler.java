package org.example.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URI;
import java.sql.*;
import java.util.stream.Collectors;

public class UserHandler implements HttpHandler {

    private static final String DB_URL = "jdbc:sqlite:Client-Contact-Management-System/backend/database/clients.db";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // CORS headers
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        // handle preflight
        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String method = exchange.getRequestMethod();
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();

        // check if id is in the URL e.g. /api/users/1
        String[] parts = path.split("/");
        boolean hasId = parts.length > 3;
        int id = hasId ? Integer.parseInt(parts[3]) : -1;

        switch (method.toUpperCase()) {
            case "GET"    -> handleGet(exchange, hasId, id);
            case "POST"   -> handlePost(exchange);
            case "PUT"    -> handlePut(exchange, id);
            case "DELETE" -> handleDelete(exchange, id);
            default       -> sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
        }
    }

    // GET all users or GET by id
    private void handleGet(HttpExchange exchange, boolean hasId, int id) throws IOException {
        StringBuilder json = new StringBuilder();
        String sql = hasId
                ? "SELECT * FROM users WHERE id = " + id
                : "SELECT * FROM users";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (hasId) {
                if (rs.next()) {
                    json.append(rowToJson(rs));
                } else {
                    sendResponse(exchange, 404, "{\"error\":\"User not found\"}");
                    return;
                }
            } else {
                json.append("[");
                boolean first = true;
                while (rs.next()) {
                    if (!first) json.append(",");
                    json.append(rowToJson(rs));
                    first = false;
                }
                json.append("]");
            }

            sendResponse(exchange, 200, json.toString());

        } catch (SQLException e) {
            sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // POST - add new user
    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "INSERT INTO users(name, lastname, email, dob, zipcode) VALUES(?,?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            fillStatement(pstmt, body);
            pstmt.executeUpdate();
            sendResponse(exchange, 201, "{\"message\":\"User created\"}");
        } catch (SQLException e) {
            sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // PUT - update user
    private void handlePut(HttpExchange exchange, int id) throws IOException {
        String body = readBody(exchange);
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "UPDATE users SET name=?, lastname=?, email=?, dob=?, zipcode=? WHERE id=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            fillStatement(pstmt, body);
            pstmt.setInt(6, id);
            pstmt.executeUpdate();
            sendResponse(exchange, 200, "{\"message\":\"User updated\"}");
        } catch (SQLException e) {
            sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // DELETE - delete user
    private void handleDelete(HttpExchange exchange, int id) throws IOException {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "DELETE FROM users WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            sendResponse(exchange, 200, "{\"message\":\"User deleted\"}");
        } catch (SQLException e) {
            sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // helper - convert row to JSON string
    private String rowToJson(ResultSet rs) throws SQLException {
        return "{" +
                "\"id\":"         + rs.getInt("id")           + "," +
                "\"name\":\""     + rs.getString("name")      + "\"," +
                "\"lastname\":\"" + rs.getString("lastname")  + "\"," +
                "\"email\":\""    + rs.getString("email")     + "\"," +
                "\"dob\":\""      + rs.getString("dob")       + "\"," +
                "\"zipcode\":"   + rs.getInt("zipcode")     +
                "}";
    }

    // helper - parse JSON body and fill prepared statement
    private void fillStatement(PreparedStatement pstmt, String body) throws SQLException {
        String name     = extractJson(body, "name");
        String lastname = extractJson(body, "lastname");
        String email    = extractJson(body, "email");
        String dob      = extractJson(body, "dob");
        int zipcode    = Integer.parseInt(extractJson(body, "zipcode"));
        pstmt.setString(1, name);
        pstmt.setString(2, lastname);
        pstmt.setString(3, email);
        pstmt.setString(4, dob);
        pstmt.setInt(5, zipcode);
    }

    // helper - extract value from JSON string
    private String extractJson(String json, String key) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx == -1) return "";
        int colon = json.indexOf(":", idx) + 1;
        String rest = json.substring(colon).trim();
        if (rest.startsWith("\"")) {
            int end = rest.indexOf("\"", 1);
            return rest.substring(1, end);
        } else {
            int end = rest.indexOf(",");
            if (end == -1) end = rest.indexOf("}");
            return rest.substring(0, end).trim();
        }
    }

    // helper - read request body
    private String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new BufferedReader(new InputStreamReader(is))
                    .lines().collect(Collectors.joining("\n"));
        }
    }

    // helper - send response
    private void sendResponse(HttpExchange exchange, int code, String body) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] bytes = body.getBytes();
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}