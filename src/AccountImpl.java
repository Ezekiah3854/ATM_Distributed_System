import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;

public class AccountImpl extends UnicastRemoteObject implements Account {
    Connection conn = null;

    // Constructor: Connect to the database
    public AccountImpl() throws RemoteException {
        super();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            // Establish connection to MySQL
            conn = DriverManager.getConnection("jdbc:mysql://192.168.137.184:3306/bankDB", "root", "zack3854?");
            System.out.println("Connected to the Database!");
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Database connection failed");
        } 
    }

    @Override
    public String sendMoney(String fromAccountNumber, String toAccountNumber, double amount, String pin) throws RemoteException {
        try {
            // Authenticate the sender account
            if (!authenticate(fromAccountNumber, pin)) {
                return "Failed. Incorrect Account number or PIN.";
            }

            // Check if receiver account exists
            if (!accountExists(toAccountNumber)) {
                return "Failed. The account you want to send money to does not exist.";
            }

            // Check sender balance
            double senderBalance = getBalance(fromAccountNumber);
            if (senderBalance < amount) {
                return "Failed. Insufficient funds in your account to send Ksh" + amount + " to account " + toAccountNumber + ". Your account balance is Ksh" + senderBalance;
            }

            // Start a transaction
            conn.setAutoCommit(false);

            // Deduct amount from sender
            updateBalance(fromAccountNumber, -amount);

            // Add amount to receiver
            updateBalance(toAccountNumber, amount);

            // Record the transaction
            recordTransaction(fromAccountNumber, toAccountNumber, amount);

            // Commit the transaction
            conn.commit();
            conn.setAutoCommit(true);

            return "Confirmed. Ksh" + amount + " has been sent to account " + toAccountNumber;

        } catch (SQLException e) {
            try {
                conn.rollback(); // Roll back on failure
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return "Transaction failed: " + e.getMessage();
        }
    }

    @Override
    public String withdraw(String accountNumber, double amount, String pin) throws RemoteException {
        try {
            // Authenticate the account
            if (!authenticate(accountNumber, pin)) {
                return "Failed. Incorrect Account number or PIN.";
            }

            double currentBalance = getBalance(accountNumber);
            if (currentBalance < amount) {
                return "Failed. Insufficient funds to withdraw Ksh" + amount + ". Your account balance is Ksh" + currentBalance;
            }

            // Update balance
            updateBalance(accountNumber, -amount);

            return "Confirmed. Withdrawal of Ksh" + amount + " is successful. Dispensing cash now...";

        } catch (SQLException e) {
            e.printStackTrace();
            return "Withdrawal failed: " + e.getMessage();
        }
    }

    @Override
    public double checkBalance(String accountNumber, String pin) throws RemoteException {
        try {
            if (authenticate(accountNumber, pin)) {
                return getBalance(accountNumber);
            } else {
                throw new RemoteException("Failed. Incorrect Account number or PIN.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Balance check failed: " + e.getMessage());
        }
    }

    // Helper method to authenticate the account
    private boolean authenticate(String accountNumber, String pin) throws SQLException {
        String query = "SELECT pin FROM accounts WHERE account_number = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, accountNumber);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            String storedPin = rs.getString("pin");
            return storedPin.equals(pin);
        }
        return false;
    }

    // Helper method to get the account balance
    private double getBalance(String accountNumber) throws SQLException {
        String query = "SELECT balance FROM accounts WHERE account_number = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, accountNumber);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getDouble("balance");
        }
        throw new SQLException("Account not found");
    }

    // Helper method to update the account balance
    private void updateBalance(String accountNumber, double amountChange) throws SQLException {
        String query = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setDouble(1, amountChange);
        stmt.setString(2, accountNumber);
        stmt.executeUpdate();
    }

    //  Helper method to check if an account exists
    private boolean accountExists(String accountNumber) throws SQLException {
        String query = "SELECT account_number FROM accounts WHERE account_number = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, accountNumber);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    // Helper method to record a transaction
    private void recordTransaction(String fromAccount, String toAccount, double amount) throws SQLException {
        String query = "INSERT INTO transactions (sender_account, receiver_account, amount) VALUES (?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, fromAccount);
        stmt.setString(2, toAccount);
        stmt.setDouble(3, amount);
        stmt.executeUpdate();
    }
}
