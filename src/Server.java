

import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Server {
    public static void main(String[] args) {
        try {
            // Create and export the remote object
            Account accountService = new AccountImpl();

            // get server ip address
            String serveIp = InetAddress.getLocalHost().getHostAddress();
            
            // Start the RMI registry on port 1099 (default RMI port)
            LocateRegistry.createRegistry(5200);
            
            // Bind the remote object to the registry with a name
            Naming.rebind("rmi://"+serveIp+":5200/AccountService", accountService);
            
            System.out.println("RMI Server is running... AccountService is bound to the registry.");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
