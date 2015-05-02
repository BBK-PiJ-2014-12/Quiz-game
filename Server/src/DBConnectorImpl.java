import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBConnectorImpl extends UnicastRemoteObject implements DBConnector{
	private Connection connection;
	private ResultSet result;
	
	public DBConnectorImpl() throws RemoteException {}

	
	public synchronized List<List<Object>> getData(String query) {
		List<List<Object>> data = new ArrayList<>();
		try{
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/quiz","root","");
			result = connection.createStatement().executeQuery(query);
			int columns = result.getMetaData().getColumnCount();		
			while(result.next()) {
				List<Object> row = new ArrayList();
				for(int c = 1; c <= columns; c++) {
					row.add(result.getObject(c));
				}
				data.add(row);
			}
			result.close();
		} catch (SQLException | ClassNotFoundException ex) {
			ex.printStackTrace();
		} 
		return data;
	}
	
	public synchronized int insertData(String query) {
		try{
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/quiz","root","");
			return connection.createStatement().executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
		} catch (SQLException | ClassNotFoundException ex) {
			ex.printStackTrace();
			return Integer.MIN_VALUE;
		}
	}
}
