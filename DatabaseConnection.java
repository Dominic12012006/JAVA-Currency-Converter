package CurrencyConverterP;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/CurrencyConverterDB";
    private static final String USER = "root"; // Replace with your MySQL username
    private static final String PASSWORD = "Mitochondria@098"; // Replace with your MySQL password

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void saveConversion(String fromCurrency, String toCurrency, double amount, double convertedAmount) {
        String query = "INSERT INTO ConversionHistory (from_currency, to_currency, amount, converted_amount) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, fromCurrency);
            pstmt.setString(2, toCurrency);
            pstmt.setDouble(3, amount);
            pstmt.setDouble(4, convertedAmount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
