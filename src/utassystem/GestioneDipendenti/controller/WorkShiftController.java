package utassystem.GestioneDipendenti.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.ResourceBundle;
import com.github.fxrouter.FXRouter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
//import javafx.css.Style;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utassystem.DbConnection;
import utassystem.GestioneDipendenti.risorse.WorkShift;

public class WorkShiftController implements Initializable {
	// elementi tabella
	@FXML
	private TableView<WorkShift> table;
	@FXML
	private TableColumn<WorkShift, String> columnMatricola;
	@FXML
	private TableColumn<WorkShift, String> columnNome;
	@FXML
	private TableColumn<WorkShift, String> columnCognome;
	@FXML
	private TableColumn<WorkShift, String> columnLunedi;
	@FXML
	private TableColumn<WorkShift, String> columnMartedi;
	@FXML
	private TableColumn<WorkShift, String> columnMercoledi;
	@FXML
	private TableColumn<WorkShift, String> columnGiovedi;
	@FXML
	private TableColumn<WorkShift, String> columnVenerdi;
	@FXML
	private TableColumn<WorkShift, String> columnSabato;
	@FXML
	private TableColumn<WorkShift, String> columnDomenica;

	// bottoni vari
	@FXML
	private Button btnIndietro;
	@FXML
	private Button btnPrevWeek;
	@FXML
	private Button btnNextWeek;
	@FXML
	private Label lblSettimana;
	@FXML
	private Button save;

	// campi di testo
	@FXML
	private TextField matricola;
	@FXML
	private TextField nome;
	@FXML
	private TextField cognome;

	// combobox
	@FXML
	private ComboBox<String> cbLunedi;
	@FXML
	private ComboBox<String> cbMartedi;
	@FXML
	private ComboBox<String> cbMercoledi;
	@FXML
	private ComboBox<String> cbGiovedi;
	@FXML
	private ComboBox<String> cbVenerdi;
	@FXML
	private ComboBox<String> cbSabato;
	@FXML
	private ComboBox<String> cbDomenica;

	// elementi per la ricerca
	@FXML
	private ComboBox<String> cercaPer;
	@FXML
	private TextField search;

	// variabili varie
	private ObservableList<WorkShift> data;
	private DbConnection dc;
	private int cWeek;
	private int cDay;
	private int cYear;
	private int cMonth;
	private int indiceGiorno;
	public Stage window;
	private boolean isConnected;

	// istruzzioni all'avvio
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

		dc = new DbConnection();
		currentWeek(null);
		loadDataFromDBMS();
		if (isConnected == true)
			automaticFillingDatabaseTable();

		// popolo il combobox per la ricerca
		cercaPer.getItems().clear();
		cercaPer.getItems().addAll("Matricola", "Nome", "Cognome");
		cercaPer.setValue("Matricola");

	}
	
	public String convertiGiorno(int g) {
		int []v = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
		int i, tmp, mese = 0, giornoMese = 0;
		tmp = g;
		
		//Ciclo for per individuare il mese
		for(i=0; i<12; i++) {
			tmp = tmp - v[i];
			if(tmp<0) {
				mese = i+1;
				break;
			}
		}
		tmp = g;
		
		//Ciclo for per ottenere il giorno per quel mese
		for(i=0; i<mese-1; i++) {
			tmp = tmp - v[i];
		}
		if(tmp == 0) {
			mese = mese -1;
			tmp = 30;
		}
		giornoMese = tmp;
		String data = "";
		data = giornoMese + "/" + mese;
		return data;
	}

	// metodo per il calcolo della settimana corrente
	public void currentWeek(ActionEvent event) {

		Calendar now = Calendar.getInstance();
		int week = now.get(Calendar.WEEK_OF_YEAR);
		lblSettimana.setText(Integer.toString(week));
		cWeek = week;
		int year = now.get(Calendar.YEAR);
		cYear = year;
		int month = now.get(Calendar.MONTH);
		cMonth = ++month;
		int day = now.get(Calendar.DAY_OF_YEAR);
		cDay = day;
		int index = now.get(Calendar.DAY_OF_WEEK);
		this.indiceGiorno = cDay - index + 2;
		
		//Giorno mese - giorno nell settimana
		System.out.println("Giorno nella settimana: " + now.get(Calendar.DAY_OF_WEEK));
		
		
		//int giorno = now.get(Calendar.DAY_OF_YEAR);
		//System.out.println(convertiGiorno(giorno));
		String data = convertiGiorno(indiceGiorno) + " - " + convertiGiorno(indiceGiorno + 6);
		lblSettimana.setText(cWeek + ":  " + data);
		
	}

	// metodo per tornare alla schermata di management
	public void changeSceneButtonPressed(Event event) throws Exception {
		FXRouter.goTo("management");
	}

	// metodo per mostrare la settimana precedente
	public void prevWeek(ActionEvent event) {
		if (cWeek > 1) { // controllo che impedisce di andare oltre la prima settimana dell'anno, quindi
							// tornare nell'anno precedente
			cWeek--; // decremento la variabile currentWeek
			
			
			System.out.println("Giorno attuale: " + indiceGiorno);
			if(indiceGiorno > 7) {
				indiceGiorno = indiceGiorno - 7;
			}
			
			String data = convertiGiorno(indiceGiorno) + " - " + convertiGiorno(indiceGiorno + 6);
			lblSettimana.setText(cWeek + ":  " + data);
			//lblSettimana.setText(Integer.toString(cWeek));
			loadDataFromDBMS(); // carico la tabella
			search.setText("");
			automaticFillingDatabaseTable(); // riempimento automatico tabella
		}
	}

	// metodo per mostrare la settimana successiva
	public void nextWeek(ActionEvent event) {
		cWeek++; // incremento la variabile currentWeek
		indiceGiorno = indiceGiorno + 7;
		String data = convertiGiorno(indiceGiorno) + " - " + convertiGiorno(indiceGiorno + 6);
		lblSettimana.setText(cWeek + ":  " + data);
		loadDataFromDBMS(); // carico la tabella
		search.setText("");
		automaticFillingDatabaseTable(); // riempimento automatico tabella
	}

	// METODO PER CARICARE I DATI DEL DBMS NELLA TABELLA
	public void loadDataFromDBMS() {

		String tLunedi = "";
		String tMartedi = "";
		String tMercoledi = "";
		String tGiovedi = "";
		String tVenerdi = "";
		String tSabato = "";
		String tDomenica = "";

		try {

			Connection conn = dc.Connect();
			data = FXCollections.observableArrayList();

			String query = "SELECT distinct t.refMatricola, d.nome, d.cognome"
					+ " FROM turno t, dipendente d WHERE t.refMatricola = d.matricola AND settimana = " + cWeek;

			System.out.println(query);

			ResultSet rs = conn.createStatement().executeQuery(query);
			while (rs.next()) {

				int matricola = rs.getInt("refMatricola");

				String query2 = "SELECT giorno, refMatricola, oraTurno, refMezzo, oraTurnoS, refMezzoS FROM turno WHERE anno = "+ cYear +" AND refMatricola = "+matricola+" AND  settimana = " + cWeek;

				System.out.println(query2);
				ResultSet rs2 = conn.createStatement().executeQuery(query2);


				while (rs2.next()) {
					System.out.println(rs2.getInt("giorno"));

					int giorno = rs2.getInt("giorno");

					String turno = "";
					
					

					if (rs2.getString("oraTurnoS").equals("") || rs2.getString("oraTurnoS") == null) {
						if(rs2.getString("oraTurno").equals("") || rs2.getString("oraTurno") == null) {
							turno = "";
						}else {
						if (rs2.getInt("refMezzo") == 0) {
							turno = "T: "+rs2.getString("oraTurno");
						} else {
							turno = "T: "+rs2.getString("oraTurno") + "\nM: " + rs2.getInt("refMezzo");
						}}
					} else {

						if (rs2.getInt("refMezzo") == 0) {
							turno = "T: "+rs2.getString("oraTurno") + "\nTs: " + rs2.getString("oraTurnoS");
						} else {
							turno = "T: " + rs2.getString("oraTurno") + "\nM: " + rs2.getInt("refMezzo") + "\nTs: "
									+ rs2.getString("oraTurnoS") + "\nMs: " + rs2.getInt("refMezzoS");
						}
						

					}

					switch (giorno) {

					case 1:
						tLunedi = turno;
						break;
					case 2:
						tMartedi = turno;
						break;
					case 3:
						tMercoledi = turno;
						break;
					case 4:
						tGiovedi = turno;
						break;
					case 5:
						tVenerdi = turno;
						break;
					case 6:
						tSabato = turno;
						break;
					case 7:
						tDomenica = turno;
						break;
					}

				}

				data.add(new WorkShift(cWeek, cYear, 0, null, null,null, null, matricola, tLunedi, tMartedi, tMercoledi, tGiovedi,
						tVenerdi, tSabato, tDomenica, rs.getString("nome"), rs.getString("cognome")));

			}

			isConnected = true;
		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.print("ERRORE Caricamento dati");
			ErrorMessage("Non sono riuscito a collegarmi al database.");
			isConnected = false;
		}

		columnLunedi.setCellValueFactory(new PropertyValueFactory<>("lunedi"));
		columnMartedi.setCellValueFactory(new PropertyValueFactory<>("martedi"));
		columnMercoledi.setCellValueFactory(new PropertyValueFactory<>("mercoledi"));
		columnGiovedi.setCellValueFactory(new PropertyValueFactory<>("giovedi"));
		columnVenerdi.setCellValueFactory(new PropertyValueFactory<>("venerdi"));
		columnSabato.setCellValueFactory(new PropertyValueFactory<>("sabato"));
		columnDomenica.setCellValueFactory(new PropertyValueFactory<>("domenica"));
		columnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		columnCognome.setCellValueFactory(new PropertyValueFactory<>("cognome"));
		columnMatricola.setCellValueFactory(new PropertyValueFactory<>("refMatricola"));

		table.setItems(null);
		table.setItems(data);

	}

	// metodo per cercvare un turno
	public void searchKeyReleased(KeyEvent event) {

		String tLunedi = "";
		String tMartedi = "";
		String tMercoledi = "";
		String tGiovedi = "";
		String tVenerdi = "";
		String tSabato = "";
		String tDomenica = "";

		try {
			Connection conn = dc.Connect(); // mi connetto ad database
			data = FXCollections.observableArrayList();

			String query = "SELECT distinct t.refMatricola, d.nome, d.cognome " + "FROM turno t, dipendente d "
					+ "WHERE " + cercaPer.getValue() + " LIKE '" + search.getText() + "%' "
					+ "AND t.refMatricola = d.matricola  AND settimana = " + cWeek + ";";

			System.out.println(query); // stampo la query per controllare che sia corretta

			ResultSet rs = conn.createStatement().executeQuery(query); // eseguo la query
			while (rs.next()) {

				int matricola = rs.getInt("refMatricola");

				String query2 = "SELECT giorno, refMatricola, oraTurno, refMezzo, oraTurnoS, refMezzoS FROM turno WHERE anno = "+ cYear +" AND refMatricola = "+matricola+" AND  settimana = " + cWeek;

				System.out.println(query2);
				ResultSet rs2 = conn.createStatement().executeQuery(query2);


				while (rs2.next()) {
					System.out.println(rs2.getInt("giorno"));

					int giorno = rs2.getInt("giorno");

					String turno = "";
					
					

					if (rs2.getString("oraTurnoS").equals("") || rs2.getString("oraTurnoS") == null) {
						if(rs2.getString("oraTurno").equals("") || rs2.getString("oraTurno") == null) {
							turno = "";
						}else {
						if (rs2.getInt("refMezzo") == 0) {
							turno = "T: "+rs2.getString("oraTurno");
						} else {
							turno = "T: "+rs2.getString("oraTurno") + "\nM: " + rs2.getInt("refMezzo");
						}}
					} else {

						if (rs2.getInt("refMezzo") == 0) {
							turno = "T: "+rs2.getString("oraTurno") + "\nTs: " + rs2.getString("oraTurnoS");
						} else {
							turno = "T: " + rs2.getString("oraTurno") + "\nM: " + rs2.getInt("refMezzo") + "\nTs: "
									+ rs2.getString("oraTurnoS") + "\nMs: " + rs2.getInt("refMezzoS");
						}
						

					}

					switch (giorno) {

					case 1:
						tLunedi = turno;
						break;
					case 2:
						tMartedi = turno;
						break;
					case 3:
						tMercoledi = turno;
						break;
					case 4:
						tGiovedi = turno;
						break;
					case 5:
						tVenerdi = turno;
						break;
					case 6:
						tSabato = turno;
						break;
					case 7:
						tDomenica = turno;
						break;
					}

				}

				data.add(new WorkShift(cWeek, cYear, 0, null, null,null, null, matricola, tLunedi, tMartedi, tMercoledi, tGiovedi,
						tVenerdi, tSabato, tDomenica, rs.getString("nome"), rs.getString("cognome")));

			}
		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.print("ERRORE Caricamento dati");
			ErrorMessage("Non sono riuscito a collegarmi al database.");
		}

		columnLunedi.setCellValueFactory(new PropertyValueFactory<>("lunedi"));
		columnMartedi.setCellValueFactory(new PropertyValueFactory<>("martedi"));
		columnMercoledi.setCellValueFactory(new PropertyValueFactory<>("mercoledi"));
		columnGiovedi.setCellValueFactory(new PropertyValueFactory<>("giovedi"));
		columnVenerdi.setCellValueFactory(new PropertyValueFactory<>("venerdi"));
		columnSabato.setCellValueFactory(new PropertyValueFactory<>("sabato"));
		columnDomenica.setCellValueFactory(new PropertyValueFactory<>("domenica"));
		columnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		columnCognome.setCellValueFactory(new PropertyValueFactory<>("cognome"));
		columnMatricola.setCellValueFactory(new PropertyValueFactory<>("refMatricola"));

		table.setItems(null);
		table.setItems(data);

	}

	/**
	 * Questo metodo crea un messaggio di errore che viene mostrato quando l'utente
	 * inserisce un dato errato. Per funzionare ha bisogno che gli si passi una
	 * stringa.
	 * 
	 * @param txt
	 */
	public void ErrorMessage(String txt) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("ERRORE insermento dati");
		alert.setHeaderText(null);
		alert.setContentText(txt);
		alert.showAndWait();
	}

	public void automaticFillingDatabaseTable() {

		String query1 = "SELECT d.matricola" + " FROM dipendente d "
				+ "WHERE d.Matricola NOT IN (SELECT t.refMatricola " + "FROM turno t WHERE settimana = " + cWeek
				+ " AND anno = " + cYear + ");";
		System.out.println(query1);

		try {
			Connection conn = dc.Connect();

			ResultSet rs1 = conn.createStatement().executeQuery(query1);

			while (rs1.next()) {

				try {

					Statement st = conn.createStatement();

					for (int i = 1; i < 8; i++) {

						String query2 = "INSERT INTO turno VALUES (" + cYear + ", " + cWeek + ", " + i + ", "
								+ rs1.getInt("matricola") + ", '',null, '', null);";
						System.out.println(query2);
						st.executeUpdate(query2);
						
						
					}

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("ERRORE query 2");
					ErrorMessage("Non sono riuscito a collegarmi al database.");
				}

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("ERRORE query 1");
			ErrorMessage("Non sono riuscito a collegarmi al database.");
		}

		loadDataFromDBMS();

	}

	public void emptyValue() {

		matricola.setText("");
		nome.setText("");
		cognome.setText("");
		cbLunedi.setValue("");
		cbMartedi.setValue("");
		cbMercoledi.setValue("");
		cbGiovedi.setValue("");
		cbVenerdi.setValue("");
		cbSabato.setValue("");
		cbDomenica.setValue("");

	}

	public void autoSearch(Event event) {
		if (search.getText() != "")
			searchKeyReleased(null);
	}

	public void selectUser(MouseEvent event) throws SQLException, IOException {

		int matricola = table.getSelectionModel().getSelectedItem().getRefMatricola();

		TablePosition<?, ?> pos = table.getSelectionModel().getSelectedCells().get(0);
		int column = pos.getColumn();
		
		Calendar now = Calendar.getInstance();
		int week = now.get(Calendar.WEEK_OF_YEAR);

		if(cWeek < week) {
			System.out.println("Non puoi modificare un turno di una settimana passata!");
		} else {
		if (column > 2) {

			try {

				Stage window = new Stage();
				FXMLLoader loader = new FXMLLoader();
				Pane root = loader.load(getClass().getResource("../interfacce/ChangeData.fxml").openStream());
				ChangeDataController modificaDati = (ChangeDataController) loader.getController();
				modificaDati.getDati(matricola, column, cWeek);
				Scene scene = new Scene(root);
					scene.getStylesheets().addAll(
							getClass().getResource("../risorse/changeDataStyle.css").toExternalForm(), 
							getClass().getResource("../../settingsStyle.css").toExternalForm()
					);
				window.setResizable(false);
				window.initStyle(StageStyle.UTILITY);
				window.initModality(Modality.APPLICATION_MODAL);
				window.setScene(scene);
				window.showAndWait();

			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("Errore apertura Finestra di Modifica" + e);
			}

		} else {
			System.out.println("Devi cliccare un giorno... Ma che combini?");
		}

		loadDataFromDBMS();
		}
	}
}