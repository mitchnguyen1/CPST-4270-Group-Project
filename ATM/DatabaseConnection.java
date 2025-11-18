import java.io.IOException;
import java.sql.*;
import java.io.FileInputStream;
import java.util.Properties;

public class DatabaseConnection {
    //Connection object to make connect to the database for queries
    private static Connection getConnection() throws SQLException {
        //Get database information
        Properties db = new Properties();
        try (FileInputStream fis = new FileInputStream("db.properties")) {
            db.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load db.properties", e);
        }

        String url = db.getProperty("db.url");
        String user = db.getProperty("db.user");
        String password = db.getProperty("db.password");

        //establish connection
        return DriverManager.getConnection(url, user, password);
    }
    // Function to save an account to the database
    public static void saveAccount(Account account) {
        //Query for inserting into account table for a new customer
        String sql =
                "INSERT INTO account (\"customerNumber\", \"pinNumber\", \"checkingBalance\", \"savingBalance\", \"created_at\") " +
                        "VALUES (?, ?, ?, ?, NOW())";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, account.getCustomerNumber());
            ps.setInt(2, account.getPinNumber());
            ps.setDouble(3, account.getCheckingBalance());
            ps.setDouble(4, account.getSavingBalance());

            ps.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }
    // This function validates if there is an existing user account in the database
    public static boolean validateDBAccount(Integer accNumber) {
        String sql =
                "SELECT \"customerNumber\" " +
                        "FROM account " +
                        "WHERE \"customerNumber\" = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // set parameter with account number
            ps.setInt(1, accNumber);

            // execute the query
            try (ResultSet rs = ps.executeQuery()) {

                //if a row exists, return true
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // account not found, return false
        }
    }

}
