
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import java.rmi.Naming;

public class mainFrame extends JFrame {

    public void initialize() {
        /********** Form Panel **********/
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(128, 128, 255));

        // North Panel for Bank Name
        JLabel bankLabel = new JLabel("Welcome to DTB Bank ATM", JLabel.CENTER);
        bankLabel.setFont(new Font("Arial", Font.BOLD, 26));
        bankLabel.setForeground(Color.WHITE);
        mainPanel.add(bankLabel, BorderLayout.NORTH);
        
        // Center Panel for Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBackground(new Color(128, 128, 255));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50)); // Add margin around buttons

        JButton sendMoneyButton = new JButton("Send Money");
        JButton withdrawButton = new JButton("Withdraw Cash");
        JButton checkBalanceButton = new JButton("Check Balance");
        
        // Set preferred button size with smaller height
        Dimension buttonSize = new Dimension(150, 30);
        sendMoneyButton.setPreferredSize(buttonSize);
        withdrawButton.setPreferredSize(buttonSize);
        checkBalanceButton.setPreferredSize(buttonSize);

        sendMoneyButton.setFont(new Font("Arial", Font.PLAIN, 24));
        withdrawButton.setFont(new Font("Arial", Font.PLAIN, 24));
        checkBalanceButton.setFont(new Font("Arial", Font.PLAIN, 24));
        
        // Add button listeners to navigate to transaction frame
        sendMoneyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the current frame
                new SendMoneyFrame("Send Money");
            }
        });

        withdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the current frame
                new WithdrawFrame("Withdraw");
            }
        });

        checkBalanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the current frame
                new CheckBalanceFrame("Check Balance");
            }
        });

        // Add buttons to the panel
        buttonPanel.add(sendMoneyButton);
        buttonPanel.add(withdrawButton);
        buttonPanel.add(checkBalanceButton);

        // Add button panel to the center
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // Set up frame
        add(mainPanel);
        setTitle("ATM Client");
        setSize(500, 600);
        setMinimumSize(new Dimension(400, 500));
        setMaximumSize(new Dimension(500, 600));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            mainFrame frame = new mainFrame();
            frame.initialize();
        });
    }
}

// New frame for transactions
class CheckBalanceFrame extends JFrame {
    private Account accountService;

    public CheckBalanceFrame(String action) {
        try {
            accountService = (Account) Naming.lookup("rmi://localhost:5200/AccountService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Server connection failed: " + e.getMessage());
            e.printStackTrace();
        }

        setTitle(action);
        setSize(450, 350);
        setMinimumSize(new Dimension(450, 350));
        setMaximumSize(new Dimension(450, 350));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel transPanel = new JPanel();
        transPanel.setLayout(new GridLayout(4, 2, 10, 10));
        transPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel accountLabel = new JLabel("Account Number:");
        accountLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        JTextField accountField = new JTextField(); 

        JLabel pinLabel = new JLabel("PIN:");
        pinLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        JPasswordField pinField = new JPasswordField();

        JButton submitButton = new JButton(action);
        submitButton.setFont(new Font("Arial", Font.PLAIN, 22));

        submitButton.addActionListener(e -> {
            try {
                String accountNumber = accountField.getText();
                String pin = new String(pinField.getPassword());
                double balance = accountService.checkBalance(accountNumber, pin);
                JOptionPane.showMessageDialog(null, "Your account balance is Ksh" + balance);

                accountField.setText("");
                pinField.setText("");

                dispose(); 
                new mainFrame().initialize();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Balance check failed: " + ex.getMessage());
            }
        });

        transPanel.add(accountLabel);
        transPanel.add(accountField);
        transPanel.add(pinLabel);
        transPanel.add(pinField);
        transPanel.add(submitButton);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 22));
        backButton.addActionListener(e -> {
            dispose(); 
            new mainFrame().initialize(); 
        });
        transPanel.add(backButton);

        add(transPanel);
        setVisible(true);
    }
}


// New frame for withdraw
class WithdrawFrame extends JFrame {
    private Account accountService;

    public WithdrawFrame(String action) {
        try {
            accountService = (Account) Naming.lookup("rmi://localhost:5200:5200/AccountService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Server connection failed: " + e.getMessage());
            e.printStackTrace();
        }

        setTitle(action);
        setSize(400, 350);
        setMinimumSize(new Dimension(400, 350));
        setMaximumSize(new Dimension(400, 350));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel transPanel = new JPanel();
        transPanel.setLayout(new GridLayout(5, 2, 10, 10));
        transPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel accountLabel = new JLabel("Account Number:");
        accountLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        transPanel.add(accountLabel);
        JTextField accountField = new JTextField();
        transPanel.add(accountField);

        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        transPanel.add(amountLabel);
        JTextField amountField = new JTextField();
        transPanel.add(amountField);

        JLabel pinLabel = new JLabel("PIN:");
        pinLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        transPanel.add(pinLabel);
        JPasswordField pinField = new JPasswordField();
        transPanel.add(pinField);

        JButton submitButton = new JButton(action);
        submitButton.setFont(new Font("Arial", Font.PLAIN, 22));

        // Handle withdrawal
        submitButton.addActionListener(e -> {
            try {
                String accountNumber = accountField.getText();
                double amount = Double.parseDouble(amountField.getText());
                String pin = new String(pinField.getPassword());

                String response = accountService.withdraw(accountNumber, amount, pin);
                JOptionPane.showMessageDialog(null, response);

                accountField.setText("");
                amountField.setText("");
                pinField.setText("");

                dispose(); 
                new mainFrame().initialize();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Withdrawal failed: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        transPanel.add(submitButton);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 22));
        backButton.addActionListener(e -> {
            dispose(); 
            new mainFrame().initialize(); 
        });
        transPanel.add(backButton);

        add(transPanel);
        setVisible(true);
    }
}


// New frame for sendMoney

class SendMoneyFrame extends JFrame {
    private Account accountService; // RMI object

    public SendMoneyFrame(String action) {
        try {
            // Lookup the server object
            accountService = (Account) Naming.lookup("rmi://localhost:5200/AccountService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Server connection failed: " + e.getMessage());
            e.printStackTrace();
        }

        setTitle(action);
        setSize(500, 350);
        setMinimumSize(new Dimension(500, 350));
        setMaximumSize(new Dimension(500, 350));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel transPanel = new JPanel();
        transPanel.setLayout(new GridLayout(5, 2, 10, 10));
        transPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel accountLabel1 = new JLabel("Your account No:");
        accountLabel1.setFont(new Font("Arial", Font.PLAIN, 20));
        transPanel.add(accountLabel1);
        JTextField senderAccountField = new JTextField();
        transPanel.add(senderAccountField);

        JLabel accountLabel2 = new JLabel("Receiver's account No:");
        accountLabel2.setFont(new Font("Arial", Font.PLAIN, 20));
        transPanel.add(accountLabel2);
        JTextField receiverAccountField = new JTextField();
        transPanel.add(receiverAccountField);

        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        transPanel.add(amountLabel);
        JTextField amountField = new JTextField();
        transPanel.add(amountField);

        JLabel pinLabel = new JLabel("PIN:");
        pinLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        transPanel.add(pinLabel);
        JPasswordField pinField = new JPasswordField();
        transPanel.add(pinField);

        JButton submitButton = new JButton(action);
        submitButton.setFont(new Font("Arial", Font.PLAIN, 22));

        // 💸 Handle the send money action
        submitButton.addActionListener(e -> {
            try {
                String fromAccount = senderAccountField.getText();
                String toAccount = receiverAccountField.getText();
                double amount = Double.parseDouble(amountField.getText());
                String pin = new String(pinField.getPassword());

                // Call the remote method
                String response = accountService.sendMoney(fromAccount, toAccount, amount, pin);

                // Show the result
                JOptionPane.showMessageDialog(null, response);

                // Clear the fields
                senderAccountField.setText("");
                receiverAccountField.setText("");
                amountField.setText("");
                pinField.setText("");

                dispose(); 
                new mainFrame().initialize();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Transaction failed: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        transPanel.add(submitButton);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 22));
        backButton.addActionListener(e -> {
            dispose(); 
            new mainFrame().initialize(); 
        });
        transPanel.add(backButton);

        add(transPanel);
        setVisible(true);
    }
}

