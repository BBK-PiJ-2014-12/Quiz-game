import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
	
	/**
	 * Registers DBConnector on the server. 
	 * @param args
	 */
	public static void main(String[] args) {
		System.setProperty("java.security.policy", "security.policy");
		System.setSecurityManager(new RMISecurityManager());
		
		try{
			Registry regOnServer = LocateRegistry.createRegistry(1099);
			DBConnectorImpl dbc = new DBConnectorImpl();
			regOnServer.rebind("dbc", dbc);

			System.out.println("Server is ready!");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}