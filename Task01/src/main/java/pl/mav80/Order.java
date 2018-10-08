package pl.mav80;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
	
	
	

	
	
	
	

}
