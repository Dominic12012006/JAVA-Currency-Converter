package CurrencyConverterP;

import java.sql.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class CurrencyConverterC {
	private static final String API_KEY = "966a32951e1b3eb718835a42";

    // Method to convert currency and save to history
	public static void convertAndSave(String fromCurrency, String toCurrency, double amount, StringBuilder result) {
        try {
            String urlString = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/" + fromCurrency;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                result.append("Error: Unable to fetch data. Response Code: " + conn.getResponseCode());
                return;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            double exchangeRate = jsonResponse.getJSONObject("conversion_rates").getDouble(toCurrency);
            double convertedAmount = amount * exchangeRate;

            result.append(String.format("%.2f %s is equal to %.2f %s", amount, fromCurrency, convertedAmount, toCurrency));
            // Save to database
            
            saveToHistory(fromCurrency, toCurrency, amount, convertedAmount);
        } catch (Exception e) {
            result.append("An error occurred: " + e.getMessage());
        }
    }
    // Method to save conversion history to the database
    private static void saveToHistory(String fromCurrency, String toCurrency, double amount, double convertedAmount) {
        String url = "jdbc:mysql://localhost:3306/CurrencyConverterDB"; // Update with your database URL
        String user = "root"; // Update with your database username
        String password = "Mitochondria@098"; // Update with your database password

        String sql = "INSERT INTO conversionhistory (from_currency, to_currency, amount, converted_amount) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fromCurrency);
            pstmt.setString(2, toCurrency);
            pstmt.setDouble(3, amount);
            pstmt.setDouble(4, convertedAmount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to retrieve conversion history from the database
    public static String getConversionHistory() {
        StringBuilder history = new StringBuilder();
        String url = "jdbc:mysql://localhost:3306/CurrencyConverterDB"; // Update with your database URL
        String user = "root"; // Update with your database username
        String password = "Mitochondria@098"; // Update with your database password

        String sql = "SELECT * FROM conversionhistory ORDER BY conversion_date DESC";
        
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                history.append(String.format("ID: %d, %s %s to %s = %.2f on %s%n", 
                    rs.getInt("id"), 
                    rs.getDouble("amount"), 
                    rs.getString("from_currency"), 
                    rs.getString("to_currency"), 
                    rs.getDouble("converted_amount"), 
                    rs.getTimestamp("conversion_date")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history.toString();
    }

    // Method to delete a specific entry from the conversion history
    public static void deleteHistoryEntry(int id) {
        String url = "jdbc:mysql://localhost:3306/CurrencyConverterDB"; // Update with your database URL
        String user = "root"; // Update with your database username
        String password = "Mitochondria@098"; // Update with your database password

        String sql = "DELETE FROM conversionhistory WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
