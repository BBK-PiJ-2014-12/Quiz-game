import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;


public interface DBConnector extends Remote{
	
	/**
	 * Sends the query to the database to obtain information and
	 * returns the result in the form of a list of list of objects. 
	 * 
	 * @param query
	 * @return List<List<Object>>
	 * @throws RemoteException
	 */
	List<List<Object>> getData(String query) throws RemoteException;	
	
	/**
	 * Sends insert statement to the database and return the id of the new row. 
	 * 
	 * @param query
	 * @return the index on the inserted record
	 * @throws RemoteException
	 */
	int insertData(String query) throws RemoteException;
}