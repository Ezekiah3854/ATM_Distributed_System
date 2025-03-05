

import java.rmi.Remote;
import java.rmi.RemoteException;

// Defining the remote interface
public interface Account extends Remote{
    // Method to send money to another account
    String sendMoney(String fromAccountNumber, String toAccountNumber, double amount, String pin) throws RemoteException;
    
    // Method to withdraw money
    String withdraw(String accountNumber, double amount, String pin) throws RemoteException;
    
    // Method to check account balance
    double checkBalance(String accountNumber, String pin) throws RemoteException;
    
} 
