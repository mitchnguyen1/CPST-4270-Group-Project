import java.io.InputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DatabaseConnection {

    // Load DB properties from classpath
    private static Connection getConnection() throws SQLException {

        String url = Config.get("db.url");
        String user = Config.get("db.user");
        String password = Config.get("db.password");

        return DriverManager.getConnection(url, user, password);
    }

    // Save an account to the database
    public static void saveAccount(Account account) {
        String sql = "INSERT INTO account (\"customerNumber\", \"pinNumber\", \"checkingBalance\", \"savingBalance\", \"created_at\") "
                + "VALUES (?, ?, ?, ?, NOW())";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, account.getCustomerNumber());
            ps.setInt(2, account.getPinNumber());
            ps.setDouble(3, account.getCheckingBalance());
            ps.setDouble(4, account.getSavingBalance());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Load account from database
    public static Account loadAccount(int accNumber) {
        String sql = "SELECT \"customerNumber\", \"pinNumber\", \"checkingBalance\", \"savingBalance\", \"created_at\" "
                + "FROM account WHERE \"customerNumber\" = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, accNumber);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                return new Account(
                        rs.getInt("customerNumber"),
                        rs.getInt("pinNumber"),
                        rs.getDouble("checkingBalance"),
                        rs.getDouble("savingBalance")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Validate account exists
    public static boolean validateDBAccount(Integer accNumber) {
        String sql = "SELECT \"customerNumber\" FROM account WHERE \"customerNumber\" = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, accNumber);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update checking balance (Method Overload for Account object)
    public static void updateCheckingBalance(Account account) {
        updateCheckingBalance(account.getCustomerNumber(), account.getCheckingBalance());
    }

    // Update checking balance (Primitive values)
    public static void updateCheckingBalance(int customerNumber, double newCheckingBalance) {
        String sql = "UPDATE account SET \"checkingBalance\" = ? WHERE \"customerNumber\" = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, newCheckingBalance);
            ps.setInt(2, customerNumber);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update savings balance (Method Overload for Account object)
    public static void updateSavingBalance(Account account) {
        updateSavingBalance(account.getCustomerNumber(), account.getSavingBalance());
    }

    // Update savings balance (Primitive values)
    public static void updateSavingBalance(int customerNumber, double newSavingBalance) {
        String sql = "UPDATE account SET \"savingBalance\" = ? WHERE \"customerNumber\" = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, newSavingBalance);
            ps.setInt(2, customerNumber);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get checking balance
    public static double checkingBalance(Account account) {
        String sql = "SELECT \"checkingBalance\" FROM account WHERE \"customerNumber\" = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, account.getCustomerNumber());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("checkingBalance");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // Get savings balance
    public static double savingBalance(Account account) {
        String sql = "SELECT \"savingBalance\" FROM account WHERE \"customerNumber\" = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, account.getCustomerNumber());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("savingBalance");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // Validate PIN exists
    public static boolean validatePin(int accountNumber) {
        String sql = "SELECT \"pinNumber\" FROM account WHERE \"customerNumber\" = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, accountNumber);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



}