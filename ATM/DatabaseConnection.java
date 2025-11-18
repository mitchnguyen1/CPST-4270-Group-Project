import java.io.IOException;
import java.sql.*;
import java.io.FileInputStream;
import java.util.Properties;

public class DatabaseConnection {
    //Connection object to make connect to the database for queries
    private static Connection getConnection() throws SQLException {
        //Get database information
        Properties db = new Properties();
        try (FileInputStream fis = new FileInputStream("resources/db.properties")) {
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

    //function to create an account object from database
    public static Account loadAccount(int accNumber) {

        String sql =
                "SELECT \"customerNumber\", \"pinNumber\", \"checkingBalance\", \"savingBalance\", \"created_at\" " +
                        "FROM account " +
                        "WHERE \"customerNumber\" = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, accNumber);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    return null; // no account found
                }

                // Build Account object from DB values
                Account acc = new Account(
                        rs.getInt("customerNumber"),
                        rs.getInt("pinNumber"),
                        rs.getDouble("checkingBalance"),
                        rs.getDouble("savingBalance")
                );

                return acc;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
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
    //update checking balance
    public static void updateCheckingBalance(int customerNumber, double newCheckingBalance) {
        String sql =
                "UPDATE account " +
                        "SET \"checkingBalance\" = ? " +
                        "WHERE \"customerNumber\" = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, newCheckingBalance);
            ps.setInt(2, customerNumber);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateSavingBalance(int customerNumber, double newSavingBalance) {
        String sql =
                "UPDATE account " +
                        "SET \"savingBalance\" = ? " +
                        "WHERE \"customerNumber\" = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, newSavingBalance);
            ps.setInt(2, customerNumber);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    // Get Checking Balance from the database
    public static double checkingBalance(Account account) {
        String sql = "SELECT \"checkingBalance\" FROM account WHERE \"customerNumber\" = ?;";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, account.getCustomerNumber());

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    //return as double
                    return rs.getDouble("checkingBalance");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Account not found or error
        return 0;
    }

    // Get Savings Balance from the database
    public static double savingBalance(Account account) {
        String sql = "SELECT \"savingBalance\" FROM account WHERE \"customerNumber\" = ?;";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, account.getCustomerNumber());

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    //return as double
                    return rs.getDouble("savingBalance");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Account not found or error
        return 0;
    }

    // validate pin
    public static boolean validatePin(int accountNumber) {
        String sql = "SELECT \"pinNumber\" FROM account WHERE \"customerNumber\" = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // set parameter with account number
            ps.setInt(1, accountNumber);

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
