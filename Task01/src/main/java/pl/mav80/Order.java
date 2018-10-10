package pl.mav80;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;



public class Order {
	
	private int id;
	private String ClientId;
	private long RequestId;
	private String Name;
	private int Quantity;
	private double Price;
	
	public Order() {}

	public Order(String clientId, long requestId, String name, int quantity, double price) {
		ClientId = clientId;
		RequestId = requestId;
		Name = name;
		Quantity = quantity;
		Price = price;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getClientId() {
		return ClientId;
	}

	public void setClientId(String clientId) {
		ClientId = clientId;
	}

	public long getRequestId() {
		return RequestId;
	}

	public void setRequestId(long requestId) {
		RequestId = requestId;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public int getQuantity() {
		return Quantity;
	}

	public void setQuantity(int quantity) {
		Quantity = quantity;
	}

	public double getPrice() {
		return Price;
	}

	public void setPrice(double price) {
		Price = price;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ClientId == null) ? 0 : ClientId.hashCode());
		result = prime * result + ((Name == null) ? 0 : Name.hashCode());
		long temp;
		temp = Double.doubleToLongBits(Price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + Quantity;
		result = prime * result + (int) (RequestId ^ (RequestId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		if (ClientId == null) {
			if (other.ClientId != null)
				return false;
		} else if (!ClientId.equals(other.ClientId))
			return false;
		if (Name == null) {
			if (other.Name != null)
				return false;
		} else if (!Name.equals(other.Name))
			return false;
		if (Double.doubleToLongBits(Price) != Double.doubleToLongBits(other.Price))
			return false;
		if (Quantity != other.Quantity)
			return false;
		if (RequestId != other.RequestId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("Order [id=%s, ClientId=%s, RequestId=%s, Name=%s, Quantity=%s, Price=%s]", id, ClientId,
				RequestId, Name, Quantity, Price);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	//SELECT ClientId, COUNT(DISTINCT RequestId) as uniqueOrderCount FROM orders GROUP BY ClientId; - daje nam tabelkę z nazwą użytkownika i liczbą unikalnych orderów
	
	
	
	
	
	
	
	//add order to database
	public void addOrderToDB(Connection conn) throws SQLException {
		if (this.id == 0) { //jeśli id = 0 to znaczy że tworzymy nowe ćwiczenie
			String sql = "INSERT INTO orders(ClientId, RequestId, Name, Quantity, Price) VALUES (?, ?, ? , ?, ?)";
			String generatedColumns[] = { "ID" }; //dowiadujemy się jakie było ID ostatniego rzędu
			PreparedStatement preparedStatement;
			preparedStatement = conn.prepareStatement(sql, generatedColumns);
			preparedStatement.setString(1, this.ClientId);
			preparedStatement.setLong(2, this.RequestId);
			preparedStatement.setString(3, this.Name);
			preparedStatement.setInt(4, this.Quantity);
			preparedStatement.setDouble(5, this.Price);
			preparedStatement.executeUpdate();
			ResultSet rs = preparedStatement.getGeneratedKeys();
			if (rs.next()) {
				this.id = rs.getInt(1);
			}
		}else { //jeśli inne niż zero to uaktualniamy
			String sql = "UPDATE orders SET ClientId = ?, RequestId = ?, Name = ?, Quantity = ?, Price = ? WHERE id = ?";
			PreparedStatement preparedStatement;
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, this.ClientId);
			preparedStatement.setLong(2, this.RequestId);
			preparedStatement.setString(3, this.Name);
			preparedStatement.setInt(4, this.Quantity);
			preparedStatement.setDouble(5, this.Price);
			preparedStatement.setInt(6, this.id);
			preparedStatement.executeUpdate();
		}
	}
	
	
	//get total number of orders in  database
	public static int getNumberOfOrdersInDB(Connection conn) throws SQLException {
		
		//źle - liczy tylko linijki, ale w wielu liniach są rózne skłądniki tych samych zamówień
//			String sql = "SELECT COUNT(*) AS rowcount FROM orders";
//			PreparedStatement preparedStatement;
//			preparedStatement = conn.prepareStatement(sql);
//			ResultSet resultSet = preparedStatement.executeQuery();
//			if (resultSet.next()) {
//				int numberOfOrders =  resultSet.getInt("rowcount");
//				return numberOfOrders;
//			} else 
//			return 0;	
			
			
		 // works, but very long
		Set<String> uniqueUsers = new HashSet<>();
		int numberOfUniqueOrders = 0;
		
		String sql = "SELECT DISTINCT ClientId FROM orders;";
		PreparedStatement preparedStatement;
		preparedStatement = conn.prepareStatement(sql);
		ResultSet resultSet = preparedStatement.executeQuery();
		
		//let's count unique users
		while (resultSet.next()) {	
			uniqueUsers.add(resultSet.getString("ClientId"));
		}
		
		//System.out.println("Number of unique users: " + uniqueUsers.size());
		
		//count unique orders for every unique user
		for(String user : uniqueUsers) {
			
			sql = "SELECT COUNT(DISTINCT RequestId) AS orderCount FROM orders WHERE ClientId = ?";
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, user);
			resultSet = preparedStatement.executeQuery();
			
			if (resultSet.next()) {
				int numberOfUniqueOrdersForThisUser =  resultSet.getInt("orderCount");
				//System.out.println("Number of unique orders for user " + user + " is " + numberOfUniqueOrdersForThisUser);
				numberOfUniqueOrders = numberOfUniqueOrders + numberOfUniqueOrdersForThisUser;
			} else {
				System.out.println("Resultset is empty");
			}
		}
			
			
			return numberOfUniqueOrders;
			
			
	}
	
	
	
	//get number of orders for a single client 
	public static int getNumberOfOrdersForASingleClient(Connection conn, String userId) throws SQLException {
			String sql = "SELECT COUNT(DISTINCT RequestId) AS orderCount FROM orders WHERE ClientId = ?";
			PreparedStatement preparedStatement;
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, userId);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				int numberOfOrders =  resultSet.getInt("orderCount");
				return numberOfOrders;
			} else 
			return 0;
	}
	
	
	
	//get total value of orders in  database
	public static double getTotalValueOfOrdersInDB(Connection conn) throws SQLException {
//			String sql = "SELECT Quantity, Price FROM orders";
//			PreparedStatement preparedStatement;
//			preparedStatement = conn.prepareStatement(sql);
//			ResultSet resultSet = preparedStatement.executeQuery();
//			
//			double totalOrderValue = 0;
//			
//			while (resultSet.next()) {
//				
//				int howMany = resultSet.getInt("Quantity");
//				double howMuch = resultSet.getDouble("Price");
//				
//				totalOrderValue = totalOrderValue + (howMany * howMuch);
//			}
//			return totalOrderValue;	
		
		String sql = "SELECT SUM(Quantity * Price) as price FROM orders";
		PreparedStatement preparedStatement;
		preparedStatement = conn.prepareStatement(sql);
		ResultSet resultSet = preparedStatement.executeQuery();
		if (resultSet.next()) {
			return resultSet.getDouble("price");
		} else 
		return 0;	
	}
	
	
	
	
	//get total value of orders for a single client
	public static double getTotalValueOfOrdersForASingleClient(Connection conn, String userId) throws SQLException {
//			String sql = "SELECT Quantity, Price FROM orders  WHERE ClientId = ?";
//			PreparedStatement preparedStatement;
//			preparedStatement = conn.prepareStatement(sql);
//			preparedStatement.setString(1, userId);
//			ResultSet resultSet = preparedStatement.executeQuery();
//			
//			double totalOrderValue = 0;
//			
//			while (resultSet.next()) {
//				
//				int howMany = resultSet.getInt("Quantity");
//				double howMuch = resultSet.getDouble("Price");
//				
//				totalOrderValue = totalOrderValue + (howMany * howMuch);
//			}
			
			
		String sql = "SELECT SUM(Quantity * Price) as price FROM orders WHERE ClientId = ?"; //String sql = "SELECT FORMAT(SUM(Quantity * Price), 2) as price FROM orders WHERE ClientId = ?";
		PreparedStatement preparedStatement;
		preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setString(1, userId);
		ResultSet resultSet = preparedStatement.executeQuery();
		if (resultSet.next()) {
			return resultSet.getDouble("price");
		} else 
		return 0;	
	}
	
	
	
	
	//make a list of all orders in database
	static public ArrayList<Order> loadAllOrders(Connection conn) throws SQLException {
		ArrayList<Order> orders = new ArrayList<Order>();
		String sql = "SELECT * FROM orders ORDER BY RequestId ASC";
		PreparedStatement preparedStatement;
		preparedStatement = conn.prepareStatement(sql);
		ResultSet resultSet = preparedStatement.executeQuery();
		while (resultSet.next()) {
			Order loadedOrder = new Order();
			loadedOrder.id = resultSet.getInt("id");
			loadedOrder.ClientId = resultSet.getString("ClientId");
			loadedOrder.RequestId = resultSet.getLong("RequestId");
			loadedOrder.Name = resultSet.getString("Name");
			loadedOrder.Quantity = resultSet.getInt("Quantity");
			loadedOrder.Price = resultSet.getDouble("Price");
			orders.add(loadedOrder);
		}
		return orders;
	}
	
	
	
	
	
	//make a list of all orders for a single user
	static public ArrayList<Order> loadAllOrdersForASingleUser(Connection conn, String userId) throws SQLException {
		ArrayList<Order> orders = new ArrayList<Order>();
		String sql = "SELECT * FROM orders WHERE ClientId = ? ORDER BY RequestId ASC";
		PreparedStatement preparedStatement;
		preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setString(1, userId);
		ResultSet resultSet = preparedStatement.executeQuery();
		while (resultSet.next()) {
			Order loadedOrder = new Order();
			loadedOrder.id = resultSet.getInt("id");
			loadedOrder.ClientId = resultSet.getString("ClientId");
			loadedOrder.RequestId = resultSet.getLong("RequestId");
			loadedOrder.Name = resultSet.getString("Name");
			loadedOrder.Quantity = resultSet.getInt("Quantity");
			loadedOrder.Price = resultSet.getDouble("Price");
			orders.add(loadedOrder);
		}
		return orders;
	}
	
	
	
	
	
	
	//average order value for all orders in db
	static public double countAverageValueOfAllOrders(Connection conn) throws SQLException {
		
		Set<String> uniqueUsers = new HashSet<>();
		int numberOfUniqueOrders = 0;
		double totalOrdersValueFromAllUsers = 0;
		
		String sql = "SELECT DISTINCT ClientId FROM orders;";
		PreparedStatement preparedStatement;
		preparedStatement = conn.prepareStatement(sql);
		ResultSet resultSet = preparedStatement.executeQuery();
		
		//let's count unique users
		while (resultSet.next()) {	
			uniqueUsers.add(resultSet.getString("ClientId"));
		}
		
		//System.out.println("Number of unique users: " + uniqueUsers.size());
		
		//count unique orders for every unique user
		for(String user : uniqueUsers) {
			
			ArrayList<Integer> uniqueOrdersForThisUser = new ArrayList<>();
			double allOrdersValue = 0;
			
			sql = "SELECT DISTINCT RequestId AS uniqueOrders FROM orders WHERE ClientId = ?";
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, user);
			resultSet = preparedStatement.executeQuery();
			
			//put unique order's numbers into a table
			while(resultSet.next()) {
				uniqueOrdersForThisUser.add(resultSet.getInt("uniqueOrders"));
			}
			
			numberOfUniqueOrders = numberOfUniqueOrders + uniqueOrdersForThisUser.size();
			
			//System.out.println("Orders for user " + user + ": " + uniqueOrdersForThisUser);
			
			//compute value of every unique order
			for(int order : uniqueOrdersForThisUser) {
				allOrdersValue = allOrdersValue + getTotalValueOfOrdersForASingleOrder(conn, order, user);
				//System.out.println("Order " + order + " value: " + getTotalValueOfOrdersForASingleOrder(conn, order, user));
			}
			
			totalOrdersValueFromAllUsers = totalOrdersValueFromAllUsers + allOrdersValue;
			
		}
			return totalOrdersValueFromAllUsers / numberOfUniqueOrders;
	}
	
	
	
	
	
	
	
	//average order value per user
	static public double countAverageValueOfOrdersOfASingleUser(Connection conn, String userId) throws SQLException {
		
		ArrayList<Integer> uniqueOrdersForThisUser = new ArrayList<>();
		double allOrdersValue = 0;
		int numberOfUniqueOrders = 0;
		
		String sql = "SELECT DISTINCT RequestId AS uniqueOrders FROM orders WHERE ClientId = ?";
		PreparedStatement preparedStatement;
		preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setString(1, userId);
		ResultSet resultSet = preparedStatement.executeQuery();
		
		//put unique order's numbers into a table
		while(resultSet.next()) {
			uniqueOrdersForThisUser.add(resultSet.getInt("uniqueOrders"));
		}
		
		numberOfUniqueOrders = uniqueOrdersForThisUser.size();
		
		//System.out.println("Orders for user " + userId + ": " + uniqueOrdersForThisUser);
		
		//compute value of every unique order
		for(int order : uniqueOrdersForThisUser) {
			allOrdersValue = allOrdersValue + getTotalValueOfOrdersForASingleOrder(conn, order, userId);
			//System.out.println("Order " + order + " value: " + getTotalValueOfOrdersForASingleOrder(conn, order, userId));
		}	
	
		return allOrdersValue / numberOfUniqueOrders;
	}
	
	
	
	

	
	
	
	
	//get total value of orders for a single order (RequestId) of a single user (ClientId)
	public static double getTotalValueOfOrdersForASingleOrder(Connection conn, long RequestId, String ClientId) throws SQLException {
			
		String sql = "SELECT SUM(Quantity * Price) as price FROM orders WHERE RequestId = ? AND ClientId = ?";
		PreparedStatement preparedStatement;
		preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setLong(1, RequestId);
		preparedStatement.setString(2, ClientId);
		ResultSet resultSet = preparedStatement.executeQuery();
		if (resultSet.next()) {
			return resultSet.getDouble("price");
		} else 
		return 0;	
	}

	
	
	
	


	
}
