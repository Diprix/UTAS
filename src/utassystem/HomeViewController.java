package utassystem;

import javafx.css.PseudoClass;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.Pane;

import com.github.fxrouter.FXRouter;

public class HomeViewController {
	public String test;
	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	// search by itinerary panel
	private Pane itineraryPane;

	@FXML
	// search by line panel
	private Pane linePane;

	@FXML
	// select box for itinerary's departure
	private ComboBox<String> departure;

	@FXML
	// select box for itinerary's destination
	private ComboBox<String> destination;

	@FXML
	// select box for line
	private ComboBox<String> line;

	@FXML
	private Button searchByItinerary;

	@FXML
	private Button searchByLine;

	@FXML
	// Itinerary filter label
	private Hyperlink itineraryFilter;

	@FXML
	// Line filter label
	private Hyperlink lineFilter;
	private DbConnection dc;
	private ArrayList<String> linee = new ArrayList<String>();
	private ArrayList<String> partenze = new ArrayList<String>();
	private ArrayList<String> arrivi = new ArrayList<String>();
	private String nTappa;
	private String lineaPart, lineaArr, lineaCalc;
	private String tappaInizio, tappaFine, linea;

	@FXML
	void onSearchByItinerary(ActionEvent event) throws IOException {

		String []v = {linea, tappaInizio, tappaFine};
		FXRouter.goTo("map", v);

	}

	@FXML
	void onSearchByLine(ActionEvent event) throws IOException {
		
		System.out.println("ricerca per linea: " + line.getValue());
		
		String []v = {line.getValue(), "0", "0"};
		FXRouter.goTo("map", v);
	}

	// get 'switched' pseudo-class to check filters switching
	private PseudoClass filterSwitch = PseudoClass.getPseudoClass("switched");

	@FXML
	void goToLogin(ActionEvent event) throws Exception {
		FXRouter.goTo("login");
	}

	@FXML
	/* Fired every time user clicks on "Percorso" filter */
	void onItineraryFilter(ActionEvent event) {
		// if filter isn't already selected
		if (!itineraryFilter.getPseudoClassStates().contains(filterSwitch)) {
			// switch filters class
			lineFilter.pseudoClassStateChanged(filterSwitch, false);
			itineraryFilter.pseudoClassStateChanged(filterSwitch, true);

			// switch Pane
			itineraryPane.setVisible(true);
			linePane.setVisible(false);
		}
	}

	@FXML
	/* Fired every time user clicks on "Linea" filter */
	void onLineFilter(ActionEvent event) {
		// if filter isn't already selected
		if (!lineFilter.getPseudoClassStates().contains(filterSwitch)) {
			// switch filters class
			itineraryFilter.pseudoClassStateChanged(filterSwitch, false);
			lineFilter.pseudoClassStateChanged(filterSwitch, true);

			// switch Pane
			linePane.setVisible(true);
			itineraryPane.setVisible(false);
		}
	}

	@FXML
	void initialize() {
		dc = new DbConnection();
		assert departure != null : "fx:id=\"departure\" was not injected: check your FXML file 'entryView.fxml'.";

		// pre-select "Percorso" filter
		itineraryFilter.pseudoClassStateChanged(filterSwitch, true);
		// show "Linea" Pane initially
		linePane.setVisible(false);

		fillComboBoxLinea();
		fillComboBoxPartenza();

	}
	
	//QUESTO METODO RIEMPIE LA COMBOBOX LINEA CON TUTTE LE LINEE PRESENTI SUL DATABASE

	public void fillComboBoxLinea() {
		line.getItems().clear();		//PULISCE LA COMBOBOX PER EVITARE DUPLICATI
		try {
			
			// QUERY PER OTTENERE TUTTE LE LINEE IN ORDINE CRESCENTE (100, 101, ... )
			String query = "SELECT codLine FROM linea ORDER BY codLine ASC";

			Connection conn = dc.Connect();

			ResultSet rs = conn.createStatement().executeQuery(query);

			while (rs.next()) {

				//VISUALIZZA I DATI DELLA TUPLA OTTENUTI ALL'INTERNO DI UN ARRAYLIST
				linee.add(rs.getString("codLine")); 

			}

		} catch (SQLException e) {
			
			System.err.println("Errore " + e);

		}
		line.getItems().addAll(linee); //VISUALIZZA L'ARRAYLIST DENTRO LA COMBOBOX
	}
	

	public void fillComboBoxPartenza() {

		//String query = "SELECT DISTINCT nomeTappa FROM tappa WHERE nTappa = 1";
		//String query = "SELECT DISTINCT nomeTappa FROM tappa t1 WHERE t1.nTappa <> (SELECT max(nTappa) FROM tappa t2 WHERE t2.nLine = t1.nLine)" ;
		String query = "SELECT DISTINCT nomeTappa FROM tappa t1" ;
		
		try {
			Connection conn = dc.Connect();
			ResultSet rs = conn.createStatement().executeQuery(query);

			while (rs.next()) {

				partenze.add(rs.getString("nomeTappa"));

			}
			
			departure.getItems().addAll(partenze);
			destination.getItems().clear();

		} catch (SQLException e) {
			System.err.println("Errore " + e);

		}

	}

	public void calculateLinea() {
		String query = "SELECT * FROM tappa WHERE nomeTappa = '" + departure.getValue() + "';";

		try {
			Connection conn = dc.Connect();
			ResultSet rs = conn.createStatement().executeQuery(query);

			while (rs.next()) {
				
				/*
				try {
					String query2 = "select l.nomeTappa  " 
							+ "from tappa l " 
							+ "where l.nLine = '"+ rs.getString("nLine") + "' " 
							+ "group by l.nTappa "
							+ "having l.nTappa >= ALL (select l2.nTappa " 
													+ "from tappa l2 " 
													+ "where l2.nLine = '"+ rs.getString("nLine") + "' "
													+ "group by nTappa)";
					System.out.println("QUI: " + query2);
					*/
				try {
					String q = "select nomeTappa from tappa where nLine = '" + rs.getString("nLine") + "' "
						+ "and nTappa <> '" + rs.getInt("nTappa") + "'";
					ResultSet rs2 = conn.createStatement().executeQuery(q);
					while (rs2.next()) {

						arrivi.add(rs2.getString("nomeTappa"));
						arrivi.remove(departure.getValue());
						
					}
					destination.getItems().clear();
					destination.getItems().addAll(arrivi);
				} catch (SQLException e) {
					System.err.println("Errore " + e);

				}

			}
		} catch (SQLException e) {
			System.err.println("Errore " + e);

		}

	}

	public void unLockComboBoxDeparture(ActionEvent event) {

		if (departure.getValue() != "" || departure.getValue() != null) {
			arrivi.clear();
			destination.setDisable(false);
			calculateLinea();
			
			
		}

	}
	
	public void IA(ActionEvent event){
		
		//calculateLineFromSelectedPoind();
		calcolaLinea();
		searchByItinerary.setDisable(false);
		
	}
	
	public void calculateNTappa() {
		String query = "SELECT nTappa FROM tappa WHERE nomeTappa = '" + destination.getValue() + "'";
		
		
		try {
			Connection conn = dc.Connect();
			ResultSet rs = conn.createStatement().executeQuery(query);

			while (rs.next()) {

				nTappa = rs.getString("nTappa");
			}

		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.println("errore nTappa");
		}
		
	}
	
	public void calcolaLinea() {
		String query = "Select nLine from tappa where nomeTappa = '" + destination.getValue() + "'";
		try {
			Connection conn = dc.Connect();
			ResultSet rs = conn.createStatement().executeQuery(query);
			while(rs.next()) {
				this.linea = rs.getString("nLine");
				
				query = "Select nTappa from tappa where nLine = '" + linea + "' and nomeTappa = '" + departure.getValue() + "'";
				ResultSet rs1 = conn.createStatement().executeQuery(query);
				while(rs1.next()) {
					this.tappaInizio = rs1.getString("nTappa");
					query = "Select nTappa from tappa where nLine = '" + linea + "' and nomeTappa = '" + destination.getValue() + "'";
					ResultSet rs2 = conn.createStatement().executeQuery(query);
					while(rs2.next()) {
						this.tappaFine = rs2.getString("nTappa");
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("Linea: " + linea);
		System.out.println("Tappa inizio: " + this.tappaInizio);
		System.out.println("Tappa fine: " + this.tappaFine);
		
	}

	public void calculateLineFromSelectedPoind() {
		
		calculateNTappa();
		
		String query = "SELECT l.nLine AS partenza, l2.nLine AS arrivo " + "FROM tappa l, tappa l2 "
				+ "WHERE l.nTappa = 1 " + "AND l.nomeTappa = '" + departure.getValue() + "' " + "AND l2.nTappa = '"
				+ nTappa + "' " + "AND l2.nomeTappa = '" + destination.getValue() + "' ";
		
		
				try {
					Connection conn = dc.Connect();
					ResultSet rs = conn.createStatement().executeQuery(query);
					while (rs.next()) {

						lineaPart = rs.getString("partenza");
						lineaArr = rs.getString("arrivo");
						System.out.println(lineaPart);
						System.out.println(lineaArr);
						
						if (lineaPart.equals(lineaArr)) {
							lineaCalc = lineaPart;
							
						}
						System.out.println("Linea Calcolata: " + lineaCalc);
						
					}

					
				} catch (SQLException e) {
					System.err.println("Errore " + e);
					System.out.println("errore IA");
				}
			
	}
	
	public void sbloccaTastoCerca(ActionEvent event) {
		
		if(line.getValue() == null) {
			searchByLine.setDisable(true);
		}else {
			searchByLine.setDisable(false);
		}
			
		
	}

}
