package utassystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class DbConnection {
/**
 * @author Oscar
 * @return
 * @throws SQLException
 * @throws ClassNotFoundException 
 */
	
	public Connection Connect () throws SQLException {
		try {
		//Dati per accedere al DBMS
		String url = "jdbc:mysql://localhost:3306/utas?useSSL=false";
		String user = "root"; // il tuo username, solitamente root.
		String password = "password"; // la tua password, se non ne hai una lascia "".
		
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(url, user, password);
		return conn;
		
		
		} catch (ClassNotFoundException e){
			Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null ,e);

			ErrorMessage("Non sono riuscito a collegarmi al database.");
		}
		
		return null;
		
	}	
	
	public void ErrorMessage(String txt) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("ERRORE accesso al database");
		alert.setHeaderText(null);
		alert.setContentText(txt);
		alert.showAndWait();
	}
	
	
	
	
	
}
