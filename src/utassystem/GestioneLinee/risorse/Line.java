package utassystem.GestioneLinee.risorse;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import utassystem.DbConnection;

public class Line {

	private final DoubleProperty lat;
	private final DoubleProperty lng;
	private final StringProperty name;
	private final IntegerProperty nLinea;
	private final IntegerProperty nTappa;
	private final DoubleProperty kmLinea;

	public Line(double lat, double lng, String name, int nLinea, int nTappa, double kmLinea) {
		super();
		this.lat = new SimpleDoubleProperty(lat);
		this.lng = new SimpleDoubleProperty(lng);
		this.name = new SimpleStringProperty(name);
		this.nLinea = new SimpleIntegerProperty(nLinea);
		this.nTappa = new SimpleIntegerProperty(nTappa);
		this.kmLinea = new SimpleDoubleProperty(kmLinea);
	}

	public final DoubleProperty latProperty() {
		return this.lat;
	}

	public final double getLat() {
		return this.latProperty().get();
	}

	public final void setLat(final double lat) {
		this.latProperty().set(lat);
	}

	public final DoubleProperty lngProperty() {
		return this.lng;
	}

	public final double getLng() {
		return this.lngProperty().get();
	}

	public final void setLng(final double lng) {
		this.lngProperty().set(lng);
	}

	public final StringProperty nameProperty() {
		return this.name;
	}

	public final String getName() {
		return this.nameProperty().get();
	}

	public final void setName(final String name) {
		this.nameProperty().set(name);
	}

	public final IntegerProperty nLineaProperty() {
		return this.nLinea;
	}

	public final int getNLinea() {
		return this.nLineaProperty().get();
	}

	public final void setNLinea(final int nLinea) {
		this.nLineaProperty().set(nLinea);
	}

	public final IntegerProperty nTappaProperty() {
		return this.nTappa;
	}

	public final int getNTappa() {
		return this.nTappaProperty().get();
	}

	public final void setNTappa(final int nTappa) {
		this.nTappaProperty().set(nTappa);
	}

	public final DoubleProperty kmLineaProperty() {
		return this.kmLinea;
	}

	public final double getKmLinea() {
		return this.kmLineaProperty().get();
	}

	public final void setKmLinea(final double kmLinea) {
		this.kmLineaProperty().set(kmLinea);
	}

	// METODI INTERAZIONE DATABASE

	private DbConnection dc;
	boolean result;

	// METODO PER INSERIRE UNA NUOVA TAPPA

	public void aggiungi() {
		dc = new DbConnection();
		
		
		try {

			Connection conn = dc.Connect();
			String query = "SELECT * FROM tappa WHERE nLine = " + this.getNLinea() + " AND nTappa = "
					+ this.getNTappa();
			
			System.out.println("CONTROLLO SU TAPPA "+query);
			
			ResultSet rs = conn.createStatement().executeQuery(query);
			
			if (rs.next()) {

				ErrorMessage("ERRORE, la TAPPA che vuoi inserire gia esiste per questa linea!");

			} else {

				String query2 = "INSERT INTO tappa (nLine, nTappa, latitudine, longitudine, nomeTappa, KmLine) values ("
						+ this.getNLinea() + "," + this.getNTappa() + "," + this.getLat() + "," + this.getLng() + ",'"
						+ this.getName() + "', "+ this.getKmLinea() +");";

				System.out.println("INSERT "+query2);
				
				conn.createStatement().executeUpdate(query2);
				
				String query3 = "SELECT * FROM linea WHERE codLine = " + this.getNLinea();
				System.out.println("CONTROLLO SU LINEA "+query3);
				
				ResultSet rs3 = conn.createStatement().executeQuery(query3);
				
				if (rs3.next()) {
					
					aggiornaLineaDB(this.getNLinea());

				} else {
					String query5 = "INSERT INTO linea (codLine, nTappe, KmLineTot) values ("
							+ this.getNLinea() + "," + this.getNTappa() + "," + this.getKmLinea() + ");";

					System.out.println("INSERIMENTO IN LINEA "+query5);
					
					conn.createStatement().executeUpdate(query5);
				}

			}

		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.println("ERRORE inserimento dati");

			ErrorMessage("Non sono riuscito a collegarmi al database.");
		}

	}

	// METODO PER AGGIORNARE UNA TAPPA

	public void aggiorna() {
		dc = new DbConnection();
		
		try {
			Connection conn = dc.Connect(); 
			
			String query2 = "SELECT * FROM linea WHERE codLine = " + this.getNLinea();
			ResultSet rs2 = conn.createStatement().executeQuery(query2);
			
			if(rs2.next()) {
				
				Statement st = conn.createStatement();
				String query = "UPDATE tappa set nomeTappa= '" + this.getName() + "', latitudine = " + this.getLat() + ", "
						+ "longitudine = " + this.getLng() + ", kmLine = " + this.getKmLinea() + " WHERE nLine = " + this.getNLinea()
					+ " AND nTappa = " + this.getNTappa() ;
				System.out.println(query);
				st.executeUpdate(query); 
				
				aggiornaLineaDB(this.getNLinea());

			} else {
				ErrorMessage("La tappa selezionata della linea "+ this.getNLinea() +" non esiste");
			}
			
			

		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.println("ERRORE aggiornamento dati");

			ErrorMessage("Non sono riuscito a collegarmi al database.");
		}

	}

	// METODO PER ELIMINARE UNA TAPPA

	public void elimina() {
		dc = new DbConnection();

		try {

			Connection conn = dc.Connect();

			Statement st = conn.createStatement();

			String sql = "DELETE FROM tappa WHERE nLine = "
					+ this.getNLinea() + " AND nTappa = "
					+ this.getNTappa() + ";";

			boolean result = confirmBox("Conferma", "Sei sicuro di voler eliminare la tappa: "
					+ this.getNTappa() + " della linea: "+this.getNLinea()+" dal Database?");
			
			if (result == true) {

				st.executeUpdate(sql);
				
				System.out.println("Tappa " + this.getNTappa()
						+ " della linea "+this.getNLinea()+" eliminata correttamente");

				aggiornaLineaDB(this.getNLinea());
				
			} else {

				InformationMessage("Operazione annullata");

			}

		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.println("ERRORE eliminazione dati");

			ErrorMessage("Non sono riuscito a collegarmi al database.");
		}

	}

	// ALERT VARI

	public void ErrorMessage(String txt) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("ERRORE insermento dati");
		alert.setHeaderText(null);
		alert.setContentText(txt);
		alert.showAndWait();
	}
	
	public void InformationMessage(String txt) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Dialog");
		alert.setHeaderText(null);
		alert.setContentText(txt);
		alert.showAndWait();
	}

	public void warningMessage(String txt) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Information Dialog");
		alert.setHeaderText(null);
		alert.setContentText(txt);
		alert.showAndWait();
	}

	public boolean confirmBox(String title, String message) {

		Alert alert = new Alert(AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.CANCEL);
		alert.setTitle(title);

		alert.showAndWait();

		if (alert.getResult() == ButtonType.YES) {
			result = true;
		} else
			result = false;

		return result;

	}
	
	private void aggiornaLineaDB(int linea) throws SQLException {
		
		dc = new DbConnection();
		Connection conn = dc.Connect(); 
		
		int numeroTappe = 0;
		int totaleKilometri = 0;
		
		String query = "select count(nTappa), SUM(KmLine)  " 
				+ "from tappa  " 
				+ "where nLine = "+ linea;
				
		
		System.out.println("QUI 1: " + query);
		ResultSet rs = conn.createStatement().executeQuery(query);
		
		if(rs.next()) {
			numeroTappe = rs.getInt("count(nTappa)");
			System.out.println(numeroTappe);
			
			totaleKilometri = rs.getInt("SUM(KmLine)");
			System.out.println(totaleKilometri);
		}
		
		
		String UPDATE = "UPDATE linea set nTappe = " + numeroTappe + ", KmLineTot = " + totaleKilometri + " WHERE codLine = " + linea;
		
		Statement st = conn.createStatement();
		st.executeUpdate(UPDATE); 
		
		if(numeroTappe == 0) {
			
			String ELIMINA = "DELETE FROM linea WHERE codLine = "+ this.getNLinea();
			st.executeUpdate(ELIMINA); 
			
		}
	}

}
