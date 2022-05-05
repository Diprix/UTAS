package utassystem.GestioneDipendenti.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.ResourceBundle;

import com.github.fxrouter.FXRouter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import utassystem.DbConnection;
import utassystem.GestioneDipendenti.risorse.Employee;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

/**
 * Classe per gestire gli impiegati. Questa classe permette l'inerimento, la
 * modifica e l'eliminazione dei dipendenti dal DBMS. Per funzionare bisogna
 * essere connessi al database. Controlla la classe Database.DbConnection e
 * verifica che i dati del database siano corretti.
 * 
 * @author Oscar
 *
 */

public class EmployeeManagementController implements Initializable {

	@FXML
	private TableView<Employee> table;
	@FXML
	private TableColumn<Employee, String> columnFirstName;
	@FXML
	private TableColumn<Employee, String> columnSurname;
	@FXML
	private TableColumn<Employee, String> columnPassword;
	@FXML
	private TableColumn<Employee, String> columnSesso;
	@FXML
	private TableColumn<Employee, String> columnRole;
	@FXML
	private TableColumn<Employee, String> columnMatriculation;
	@FXML
	private TableColumn<Employee, String> columnDateOfBirth;
	@FXML
	private Button btnBack;
	@FXML
	private TextField txtFirstName;
	@FXML
	private TextField txtSurname;
	@FXML
	private TextField txtPassword;
	@FXML
	private TextField txtMatriculation;
	@FXML
	private RadioButton rbUomo;
	@FXML
	private RadioButton rbDonna;
	@FXML
	private TextField txtRole;
	@FXML
	private Button btnAdd;
	@FXML
	private Button btnUpdate;
	@FXML
	private Button btnDelete;
	@FXML
	private Button btnDownloadData;
	@FXML
	private Button btnEmptyFields;
	@FXML
	private TextField txtSearch;
	@FXML
	private TextField level;
	@FXML
	private ComboBox<String> cercaPer;
	@FXML
	private DatePicker DoB;
	@FXML
	private ComboBox<String> Role;

	// Inizializzare observableList

	private ObservableList<Employee> data;
	private final ObservableList<String> roles = FXCollections.observableArrayList();
	private DbConnection dc;
	private boolean isConnected;
	private int age;
	private String sesso = "";

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		dc = new DbConnection();

		//RIEMPIMENTO AUTOMATICO DELLA TABELLA
		loadDataFromDatabase();
		
		//VERIFICA DELLA CONNESSIONE
		if (isConnected == true)
			fillComboBox();

		//RIEMPIMENTO DELLA COMBOBOX DEL CERCA-PER
		cercaPer.getItems().clear();
		cercaPer.getItems().addAll("Nome", "Cognome", "Mansione", "Matricola");
		cercaPer.setValue("Nome");

	}

	// METODO PER IL CALCOLO DELL'ETÀ

	private void yearEmployee() {

		Calendar now = Calendar.getInstance();  	//CREO NUOVO CALENDARIO
		int cYear = now.get(Calendar.YEAR); 		//RICAVO ANNO CORRENTE
		int birthYear = (DoB.getValue().getYear());	//CALCOLO ETA'
		age = cYear - birthYear;

	}

	// MEDOTO PER IL RIEMPIMENTO DELLA TABELLA

	private void loadDataFromDatabase() {

		// INTERROGAZIONE AL DATABASE

		try {
			Connection conn = dc.Connect();
			data = FXCollections.observableArrayList();

			String query = "SELECT nome, cognome, password, livello,  matricola, dataNascita, mansione, sesso FROM dipendente;";

			ResultSet rs = conn.createStatement().executeQuery(query);
			while (rs.next()) {
				
				//AGGIUNGO LA TUPA ALL'ARRAYLIST
				data.add(new Employee(rs.getString("nome"), rs.getString("cognome"), rs.getString("password"),
						rs.getInt("livello"), rs.getString("mansione"), rs.getInt("matricola"),
						rs.getString("dataNascita"), rs.getString("sesso")));

			}

			isConnected = true;  

		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.println("ERRORE Caricamento dati");

			ErrorMessage("Non sono riuscito a collegarmi al database.");
			
			isConnected = false;

		}

		// IMPOSTO L'ORDINE DEI DATI NELLA TABELLA 

		columnFirstName.setCellValueFactory(new PropertyValueFactory<>("nome"));
		columnSurname.setCellValueFactory(new PropertyValueFactory<>("cognome"));
		columnPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
		columnSesso.setCellValueFactory(new PropertyValueFactory<>("sesso"));
		columnMatriculation.setCellValueFactory(new PropertyValueFactory<>("matricola"));
		columnRole.setCellValueFactory(new PropertyValueFactory<>("mansione"));
		columnDateOfBirth.setCellValueFactory(new PropertyValueFactory<>("dataNascita"));

		table.setItems(null);
		table.setItems(data);  //AGGIUNGO I DATI DELL'ARRAYLIST NELLA TABELLA

		// SVUOTA CAMPI
		vuotaCampi(null);
		txtSearch.setText("");
		fillComboBox();

	}

	// SETTAGGIO DEL SESSO M

	public void sceltaSessoM(ActionEvent event) {

		sesso = rbUomo.getText();
		
		rbDonna.setSelected(false);

	}
	
	// SETTAGGIO DEL SESSO F
	
	public void sceltaSessoF(ActionEvent event) {

		sesso = rbDonna.getText();
			
		rbUomo.setSelected(false);

	}

	// AGGIUNGI DIPENDENTE

	public void nuovoDipendente(ActionEvent event) {

		// GESTIONE ERRORI
		if (txtFirstName.getText().equals("")) {					//CAMPO NOME NON COMPILATO
			
			ErrorMessage("Campo NOME obbligatorio");					
			
		} else if (txtSurname.getText().equals("")) {				//CAMPO COGNOME NON COMPILATO
			
			ErrorMessage("Campo COGNOME obbligatorio");					
			
		} else if (txtPassword.getText().equals("")) {				//CAMPO PASSWORD NON COMPILATO
			
			ErrorMessage("Campo PASSWORD obbligatorio");				
			
		} else if (Role.getValue() == null) {						//MANSIONE NON SELEZIONATA
			
			ErrorMessage("Campo MANSIONE obbligatorio");			
			
		} else if (sesso.equals("")) {								//SESSO NON SELEZIONATO
			
			ErrorMessage("Selezione il GENERE del dipendente");		
			
		} else if (txtMatriculation.getText().equals("")) {			//CAMPO MATRICOLA NON COMPILATO
			
			ErrorMessage("Campo MATRICOLA obbligatorio");				
			
		} else if (isInt(txtMatriculation.getText()) == false) {	//VERIFICA MATRICOLA
			
			ErrorMessage("MATRICOLA deve essere un numero");			
			
		} else if (DoB.getEditor().getText().equals("")) {			//DATA DI NASCITA NON INSERITA                        
			
			ErrorMessage("Seleziona DATA DI NASCITA del dipendnte");
			
		} else {
			yearEmployee();											//CALCOLO DELL'ETÀ
			
			if (age > 17) {											//VERIFICA DELLA MAGGIORE ETÀ

				//INSERIMENTO VERO E PROPRIO DEL DIPENDENTE
				
				//CREAZIONE DELL'OGGETTO DIPENDENTE
				Employee nuovo = new Employee(txtFirstName.getText(), txtSurname.getText(), txtPassword.getText(),
						Integer.parseInt(level.getText()), Role.getValue(), Integer.parseInt(txtMatriculation.getText()),
						DoB.getEditor().getText(), sesso);
				
				//INSERIMENTO DEL NUOVO DIPENDENTE NEL DATABASE
				nuovo.aggiungi();

				//AGGIORNAMENTO TABELLA
				loadDataFromDatabase();
				
			} else {
				
				//MESSAGGIO DI AVVISO NEL CASO IN CUI IL NUOVO DIPENDENTE NON ABBIA 18 ANNI DI ETA'
				warningMessage("Il dipendente che vuoi inserire non ha un'età conforme alle normative vigenti");
				
			}

		}

	}

	// AGGIORNA DIPENDENTE

	public void aggiornaDipendente(ActionEvent event) {

		// GESTIONE ERRORI
		if (txtFirstName.getText().equals("")) {					//CAMPO NOME NON COMPILATO
					
			ErrorMessage("Campo NOME obbligatorio");					
					
		} else if (txtSurname.getText().equals("")) {				//CAMPO COGNOME NON COMPILATO
					
			ErrorMessage("Campo COGNOME obbligatorio");					
					
		} else if (txtPassword.getText().equals("")) {				//CAMPO PASSWORD NON COMPILATO
					
			ErrorMessage("Campo PASSWORD obbligatorio");				
					
		} else if (Role.getValue() == null) {						//MANSIONE NON SELEZIONATA
					
			ErrorMessage("Campo MANSIONE obbligatorio");			
					
		} else if (sesso.equals("")) {								//SESSO NON SELEZIONATO
					
			ErrorMessage("Selezione il GENERE del dipendente");		
					
		} else if (txtMatriculation.getText().equals("")) {			//CAMPO MATRICOLA NON COMPILATO
					
			ErrorMessage("Campo MATRICOLA obbligatorio");				
					
		} else if (isInt(txtMatriculation.getText()) == false) {	//VERIFICA MATRICOLA
					
			ErrorMessage("MATRICOLA deve essere un numero");			
				
		} else if (DoB.getEditor().getText().equals("")) {			//DATA DI NASCITA NON INSERITA                        
					
			ErrorMessage("Seleziona DATA DI NASCITA del dipendnte");
					
		} else {
			yearEmployee();											//CALCOLO DELL'ETÀ
			
			if (age > 17) {											//VERIFICA DELLA MAGGIORE ETÀ


				Employee aggiorna = new Employee(txtFirstName.getText(), txtSurname.getText(), txtPassword.getText(),
					Integer.parseInt(level.getText()), Role.getValue(), Integer.parseInt(txtMatriculation.getText()), DoB.getEditor().getText(),sesso);
					
				aggiorna.aggiorna();

				loadDataFromDatabase();
			} else {
				warningMessage("Il dipendente che vuoi inserire non ha un'età conforme alle normative vigenti");
			}
		}

	}

	// CANCELLA DIPENDENTE

	public void cancellaDipendente(ActionEvent event) {

		// IMPLEMENTARE ERRORI

		Employee cancella = new Employee(null, null, null, 0, null, Integer.parseInt(txtMatriculation.getText()), null,
				null);

		cancella.cancella();
		
		loadDataFromDatabase();
	}
	
	//METODO PER TORNARE ALLA SCHERMATA DI MANAGEMENT

	public void changeSceneButtonPressed(ActionEvent event) throws Exception {

		FXRouter.goTo("management");

	}
	
	//METODO PER LA RICERCA DINAMICA CERCA-PER

	public void searchKeyReleased(KeyEvent event) throws SQLException {

		try {
			Connection conn = dc.Connect();
			
			data = FXCollections.observableArrayList();
			
			//QUERY PER CERCARE UN DETERMINATO ELEMENTO NEL DATABASE
			//RESTITUITA OGNI TUPLA CHE CONTERRA IL VALORE/CARATTERI INSERITI NEL CAMPO DI RICERCA COME DATO DELL'ATTRIBUTO SELEZIONATO CON LA COMBOBOX
			String query = "SELECT nome, cognome, password, livello,  matricola, dataNascita, mansione, sesso "
					+ "FROM dipendente " + "WHERE " + cercaPer.getValue() + " LIKE '" + txtSearch.getText() + "%';";

			ResultSet rs = conn.createStatement().executeQuery(query);
			
			while (rs.next()) {

				data.add(new Employee(rs.getString("nome"), rs.getString("cognome"), rs.getString("password"),
						rs.getInt("livello"), rs.getString("mansione"), rs.getInt("matricola"),
						rs.getString("dataNascita"), rs.getString("sesso")));

			}

		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.println("ERRORE Caricamento dati");
			
			ErrorMessage("Non sono riuscito a collegarmi al database.");

		}

		columnFirstName.setCellValueFactory(new PropertyValueFactory<>("nome"));
		columnSurname.setCellValueFactory(new PropertyValueFactory<>("cognome"));
		columnPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
		columnSesso.setCellValueFactory(new PropertyValueFactory<>("sesso"));
		columnMatriculation.setCellValueFactory(new PropertyValueFactory<>("matricola"));
		columnRole.setCellValueFactory(new PropertyValueFactory<>("mansione"));
		columnDateOfBirth.setCellValueFactory(new PropertyValueFactory<>("dataNascita"));

		table.setItems(null);
		table.setItems(data);

		vuotaCampi(null);

	}
	
	//METODO PER VUOTARE I CAMPI

	private void emptyFields() {

		txtFirstName.setText("");
		txtSurname.setText("");
		txtPassword.setText("");
		txtMatriculation.setText("");
		DoB.getEditor().setText("");
		Role.setValue(null);
		level.setText("");
		sesso = "";
		rbUomo.setSelected(false);
		rbDonna.setSelected(false);
		
	}
	
	//METODO PER LA SELEZIONE DI UN DIPENDENTE DALLA TABELLA

	public void selectUser(MouseEvent event) throws SQLException {
		try {
			
			//QUERY PER IDENTIFICARE UN DIPENDENTE IN BASE ALLA MATRICOLA DELLA RIGA DELLLA TABELLA SELEZIONATA
			String query = "SELECT * FROM dipendente WHERE matricola = '"
					+ table.getSelectionModel().getSelectedItem().getMatricola() + "';";
			
			Connection conn = dc.Connect();
			
			PreparedStatement pst = conn.prepareStatement(query);
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {

				//RIEMPIO I VARI CAMPI DI TESTO CON I DATI APPROPRIATI OTTENUTI CON L'INTERROGAZIONE AL DATABASE
				txtFirstName.setText(rs.getString("nome"));
				txtSurname.setText(rs.getString("cognome"));
				txtPassword.setText(rs.getString("password"));
				DoB.getEditor().setText(rs.getString("dataNascita"));
				Role.setValue(rs.getString("mansione"));
				automaticLevel(null);
				txtMatriculation.setText(rs.getString("matricola"));
				sesso = rs.getString("sesso");
				
				
				if (rs.getString("sesso").equals("M")) {
					rbUomo.setSelected(true);
					rbDonna.setSelected(false);
					
				} else {
					rbDonna.setSelected(true);
					rbUomo.setSelected(false);
					
				}
				
				System.out.println("Utente selezionato: " + rs.getString("matricola"));

			}
			
			//CHIUSURA CONNESSIONE
			pst.close();
			rs.close();

		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.print("ERRORE selezione dati");

			ErrorMessage("Non sono riuscito a collegarmi al database.");
		}

	}

	//METODO PER VERIFICARE CHE UN ELEMENTO SIA INT
	
	private static boolean isInt(String str) {

		try {
			int iCheck = Integer.parseInt(str);
			System.out.println(iCheck);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}

	}
	
	//MESSAGGIO DI ERRORE

	private void ErrorMessage(String txt) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("ERRORE insermento dati");
		alert.setHeaderText(null);
		alert.setContentText(txt);
		alert.showAndWait();
	}
	
	//MESSAGGIO DI AVVISO

	private void warningMessage(String txt) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Informatio Dialog");
		alert.setHeaderText(null);
		alert.setContentText(txt);
		alert.showAndWait();
	}
	
	//METODO CHE VUOTA I CAMPI

	public void vuotaCampi(ActionEvent event) {
		emptyFields();
	}
	
	//METODO PER RIEMPIRE LA COMBOBOX MANSIONE

	private void fillComboBox() {
		
		Role.getItems().clear();  //VUOTO LA COMBOBOX PER EVITARE DUPLICATI

		String query = "SELECT distinct mansione from dipendente";

		try {
			Connection conn = dc.Connect();
			ResultSet rs = conn.createStatement().executeQuery(query);

			while (rs.next()) {

				roles.add(rs.getString("mansione")); //RIEMPIMENTO ARRAYLIST CON I DATI DELLE TUPLE OTTENUTE
				Role.setItems(roles); //RIEMPIMENTO DELLA COMBOBOX CON L'ARRAYLIST

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			ErrorMessage("Non sono riuscito a collegarmi al database.");
		}

		Role.setItems(roles); //RIEMPIMENTO DELLA COMBOBOX CON L'ARRAYLIST

	}
	
	//METODO PER SETTARE AUTOMATICAMENTE IL LIVELLO IN RELAZIONE ALLA MANSIONE SCELTA

	public void automaticLevel(ActionEvent event) {

		String livello = Role.getValue();
		
		if(Role.getValue() != null) {
		switch (livello) {
		// ADMIN
		case "admin":
			level.setText("0");
			break;

		// Responsabile personale
		case "RP":
			level.setText("1");
			break;

		// Responsabile deposito
		case "RD":
			level.setText("2");
			break;

		// responsabile corse
		case "RC":
			level.setText("3");
			break;

		// autista
		case "autista":
			level.setText("4");
			break;
			
		// default
		default:
			level.setText("5");
			break;
		}
	} else {
		System.out.println("Errore nell'assegnazione del livello");
	}

	}
	
	//METODO PER CERCARE AUTOMATICAMENTE QUANDO SI CAMBIA L'ARGOMENTO DI SCELTA NEL CERCA-PER

	public void autoSearch(Event event) throws SQLException {
		if (txtSearch.getText() != "")
			searchKeyReleased(null);
	}
	
	//FIN

}
