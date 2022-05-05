package utassystem.GestioneDipendenti.controller;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.github.fxrouter.FXRouter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import utassystem.DbConnection;
import utassystem.GestioneDipendenti.risorse.Salary;

public class SalaryManagementController implements Initializable {
	@FXML
	private TextField txtFirstName;
	@FXML
	private TextField txtSurname;
	@FXML
	private TextField txtMatricola;
	@FXML
	private Button btnSearch;
	@FXML
	private Button btnBack;
	@FXML
	private Button btnPrintReport;
	@FXML
	private Button btnPrintPaycheck;
	@FXML
	private ComboBox<String> searchFor;
	@FXML
	private TextField search;
	@FXML
	private TableView<Salary> table;
	@FXML
	private TableColumn<Salary, String> columnMatricola;
	@FXML
	private TableColumn<Salary, String> columnFirstName;
	@FXML
	private TableColumn<Salary, String> columnSurname;
	@FXML
	private TableColumn<Salary, String> columnSalary;
	@FXML
	private TableColumn<Salary, String> columnOreTot;
	@FXML
	private TableColumn<Salary, String> columnOreFest;
	@FXML
	private TableColumn<Salary, String> columnOreStraord;

	private ObservableList<Salary> data;
	private DbConnection dc;
	private XSSFWorkbook wb;
	private String cMonth;
	private int cYear, cMonthN;
	private boolean isConnected;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

		//STABILISCO UNA NUOVA CONNESSIONE COL DATABASE
		dc = new DbConnection();

		//CALCOLO IL MESE CORRENTE
		currentMonth();
		
		//AGGIORNO LA TABELLA
		loadDataFromDatabase();
		
		//SE LA CONNESSIONE E' AVVENUTA CORRETTAMENTE
		if (isConnected == true) {
			System.out.println("Connessione Riuscita");
			
			//AGGIORNO LA TABELLA DEL DATABASE (AUTORIEMPIMENTO)
			automaticFillingDBMSTable();
			
			//AGGIORNO LO STIPENDIO
			try {
				this.aggiornaStipendi();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//RIEMPIMENTO COMBOBOX DEL CERCA-PER
		searchFor.getItems().clear();
		searchFor.getItems().addAll("Matricola", "Nome", "Cognome");
		searchFor.setValue("Matricola");
	}

	// METODO PER CARICARE I DATI DEL DBMS NELLA TABELLA
	private void loadDataFromDatabase() {

		try {
			Connection conn = dc.Connect();

			data = FXCollections.observableArrayList();
			
			//QUERY PER OTTENERE I DATI DI STIPENDIO PER OGNI DIPENDENTE
			String query = "SELECT * FROM stipendio s, dipendente d WHERE s.refMatricola = d.matricola AND mese = " + cMonthN + " AND anno = " + cYear;

			ResultSet rs = conn.createStatement().executeQuery(query);

			while (rs.next()) {

				//AGGIUNGO I DATI OTTENUTI AD UN ARRAYLIST
				data.add(new Salary(rs.getInt("anno"), rs.getInt("mese"), rs.getInt("stipendio"), rs.getInt("oreTot"),
						rs.getInt("oreStraord"), rs.getInt("oreFest"), rs.getInt("oreFerie"), rs.getInt("oreMalattia"),
						rs.getInt("refMatricola"), rs.getString("nome"), rs.getString("cognome")));
			}

			isConnected = true; 

		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.print("ERRORE Caricamento dati");

			ErrorMessage("Non sono riuscito a collegarmi al database.");

			isConnected = false;
		}

		//IMPOSTO L'ORDINE DEI RISULTATI NELLA TABELLA
		columnFirstName.setCellValueFactory(new PropertyValueFactory<>("refNome"));
		columnSurname.setCellValueFactory(new PropertyValueFactory<>("refCognome"));
		columnMatricola.setCellValueFactory(new PropertyValueFactory<>("refMatricola"));
		columnSalary.setCellValueFactory(new PropertyValueFactory<>("stipendio"));
		columnOreTot.setCellValueFactory(new PropertyValueFactory<>("oreTot"));
		columnOreStraord.setCellValueFactory(new PropertyValueFactory<>("oreStraord"));
		columnOreFest.setCellValueFactory(new PropertyValueFactory<>("oreFest"));

		//INSERISCO I DATI DELL'ARRAYLIST NELLA TABELLA
		table.setItems(null);
		table.setItems(data);
	}

	// METODO PER TORNARE NELLA SCHERMATA DI MANAGEMENT
	public void changeSceneButtonPressed(ActionEvent event) throws Exception {
		FXRouter.goTo("management");

	}

	// METODO PER STAMPARE UN REPORT MENSILE SU TUTTI I DIPENDENTI 
	public void exportExelReportButtonPressed(ActionEvent event) throws SQLException, IOException {
		ChoiceMessage();
	}

	//METODO PER CREARE UN REPORT
	private void creaReport(int meseN, String meseT, int anno) throws IOException {

		try {

			//QUERY PER OTTENERE I DATI DELLO STIPENDIO PER OGNI DIPENDENTE
			String query = "SELECT * FROM stipendio s, dipendente d WHERE refMatricola = matricola AND mese = " + meseN + " AND anno = " + anno;

			Connection conn = dc.Connect();
			PreparedStatement pst = conn.prepareStatement(query);
			ResultSet rs = pst.executeQuery();

			// Apache POI Jar Link
			// https://poi.apache.org/download.html

			// CREAZIONE DEL FILE XLSX

			wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet("Report Stipendi - " + meseT + " " + anno);
			XSSFRow header = sheet.createRow(0);
			header.createCell(0).setCellValue("MATRICOLA");
			header.createCell(1).setCellValue("NOME");
			header.createCell(2).setCellValue("COGNOME");
			header.createCell(3).setCellValue("STIPENDIO");
			header.createCell(4).setCellValue("ORE TOTALI");
			header.createCell(5).setCellValue("ORE STRAORDINARIO");
			header.createCell(6).setCellValue("ORE FESTIVO");

			sheet.setColumnWidth(0, 256 * 25);// 256 caratteri
			sheet.setColumnWidth(1, 256 * 25);// 256 caratteri
			sheet.setColumnWidth(2, 256 * 25);// 256 caratteri
			sheet.setColumnWidth(3, 256 * 25);// 256 caratteri
			sheet.setColumnWidth(4, 256 * 25);// 256 caratteri
			sheet.setColumnWidth(5, 256 * 25);// 256 caratteri
			sheet.setColumnWidth(6, 256 * 25);// 256 caratteri

			sheet.setZoom(150);// zoom 150%

			// RIEMPIMENTO DELLE RIGHE

			int index = 1;
			while (rs.next()) {
				XSSFRow row = sheet.createRow(index);
				row.createCell(0).setCellValue(rs.getString("refMatricola"));
				row.createCell(1).setCellValue(rs.getString("nome"));
				row.createCell(2).setCellValue(rs.getString("cognome"));
				row.createCell(3).setCellValue(rs.getString("stipendio"));
				row.createCell(4).setCellValue(rs.getString("oreTot"));
				row.createCell(5).setCellValue(rs.getString("oreStraord"));
				row.createCell(6).setCellValue(rs.getString("oreFest"));
				index++;
			}

			// STAMPA DEL FILE

			FileOutputStream fileOut = new FileOutputStream("Report Stipendi - " + meseT + " " + anno + ".xlsx"); // versione
																													// 2007
																													// o
																													// inferiori
																													// xls
			wb.write(fileOut);
			fileOut.close();

			// creo una finestra per notificare il corretto funzionamento.
			InformationMessage("Il report Ã¨ stato creato correttamente.");

			pst.close();
			rs.close();

		} catch (SQLException | FileNotFoundException e) {
			System.err.println("Errore " + e);
			ErrorMessage("Non sono riuscito a collegarmi al database.");
		}

	}
	
	// METODO PER CERCARE NELLA TABELLA
	public void searchKeyReleased(KeyEvent event) throws SQLException {

		try {
			Connection conn = dc.Connect();
			data = FXCollections.observableArrayList();

			//QUERY PER CERCARE NELLA TABELLA I DATI DI UN DIPENDENTE CERCANDO PER UN DETERMINATO ATTRIBUTO
			String query = "SELECT * FROM stipendio s, dipendente d WHERE s.refMatricola = d.matricola AND " + searchFor.getValue() + " LIKE '" + search.getText() + "%' AND mese = "
					+ cMonthN + " AND anno = " + cYear + ";";

			ResultSet rs = conn.createStatement().executeQuery(query);

			while (rs.next()) {

				data.add(new Salary(rs.getInt("anno"), rs.getInt("mese"), rs.getInt("stipendio"), rs.getInt("oreTot"),
						rs.getInt("oreStraord"), rs.getInt("oreFest"), rs.getInt("oreFerie"), rs.getInt("oreMalattia"),
						rs.getInt("refMatricola"), rs.getString("nome"), rs.getString("cognome")));
			}

		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.print("ERRORE Caricamento dati");

			ErrorMessage("Non sono riuscito a collegarmi al database.");

		}

		columnFirstName.setCellValueFactory(new PropertyValueFactory<>("refNome"));
		columnSurname.setCellValueFactory(new PropertyValueFactory<>("refCognome"));
		columnMatricola.setCellValueFactory(new PropertyValueFactory<>("refMatricola"));
		columnSalary.setCellValueFactory(new PropertyValueFactory<>("stipendio"));
		columnOreTot.setCellValueFactory(new PropertyValueFactory<>("oreTot"));
		columnOreStraord.setCellValueFactory(new PropertyValueFactory<>("oreStraord"));
		columnOreFest.setCellValueFactory(new PropertyValueFactory<>("oreFest"));

		table.setItems(null);
		table.setItems(data);

	}
	
	int turno(String s) {
		if(s.compareTo("") == 0) {
			return 0;
		}
		System.out.println("Questo " + s);
		//String y = "08.00";
		String x = s.substring(0,2);
		System.out.println(x);
		return Integer.parseInt(x);
	}
	
	//Metodo per il calcolo aggiornato degli stipendi
	private void aggiornaStipendi() throws SQLException {
	
		/*
		 * Da una query ottengo tutte le matricole
		 * Per ogni matricola creo la bustaPaga di quel dipendente
		 * Invoco il metodo della bustaPaga che richiede tutti i turni e aggiorna l'oggetto
		 * Infine calcola lo stipendio per le ore che ha svolto
		 * Aggiorno lo stipendio sul db
		 */
		
		Connection conn = dc.Connect();
		
		//Query per ottenere tutte le matricole
			String query = "SELECT * FROM dipendente";
			ResultSet rs = conn.createStatement().executeQuery(query);
			Calendar now = Calendar.getInstance();
			int week = now.get(Calendar.WEEK_OF_YEAR);
			while(rs.next()) {
				Salary stip = new Salary(this.cYear, this.cMonthN, rs.getInt("matricola"));
				//Passo come parametro la settimana corrente, per indicare quale periodo calcolare
				stip.calcolaStipendio(week, rs.getInt("livello"), dc);
			}
		
	}

	// METODO PER CALCOLARE IL MESE CORRENTE

	private void currentMonth() {
		Calendar now = Calendar.getInstance();
		int month = now.get(Calendar.MONTH);
		month++;
		System.out.println(month);
		int year = now.get(Calendar.YEAR);
		this.cYear = year;
		this.cMonthN = month;
		switch (month) {

		// CONVERSIONE DEL MESE IN FORMA ESTESA

		case 1:
			cMonth = "Gennaio";
			break;
		case 2:
			cMonth = "Febbraio";
			break;
		case 3:
			cMonth = "Marzo";
			break;
		case 4:
			cMonth = "Aprile";
			break;
		case 5:
			cMonth = "Maggio";
			break;
		case 6:
			cMonth = "Giugno";
			break;
		case 7:
			cMonth = "Luglio";
			break;
		case 8:
			cMonth = "Agosto";
			break;
		case 9:
			cMonth = "Settembre";
			break;
		case 10:
			cMonth = "Ottobre";
			break;
		case 11:
			cMonth = "Novembre";
			break;
		case 12:
			cMonth = "Dicembre";
			break;
		}

		System.out.println("Il mese corrente è : " + cMonth);
	}

	// METODO PER CERCARE AUTOMATICAMENTE QUANDO SI CAMBIA L'ARGOMENTO DI RICERCA

	public void autoSearch(Event event) throws SQLException {
		if (search.getText() != "")
			searchKeyReleased(null);
	}

	// ALERT VARI

	private void ErrorMessage(String txt) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("ERRORE accesso al database");
		alert.setHeaderText(null);
		alert.setContentText(txt);
		alert.showAndWait();
	}

	private void InformationMessage(String txt) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Informatio Dialog");
		alert.setHeaderText(null);
		alert.setContentText(txt);
		alert.showAndWait();
	}
	
	
	//METODO PER CONVERTIRE IL MESE IN FORMA ESTESA --CLONE
	private String convertiMese(int i) {
		String mese = "";
		switch(i) {

		case 1:
			mese = "Gennaio";
			break;
		case 2:
			mese = "Febbraio";
			break;
		case 3:
			mese = "Marzo";
			break;
		case 4:
			mese = "Aprile";
			break;
		case 5:
			mese = "Maggio";
			break;
		case 6:
			mese = "Giugno";
			break;
		case 7:
			mese = "Luglio";
			break;
		case 8:
			mese = "Agosto";
			break;
		case 9:
			mese = "Settembre";
			break;
		case 10:
			mese = "Ottobre";
			break;
		case 11:
			mese = "Novembre";
			break;
		case 12:
			mese = "Dicembre";
			break;
			}
		return mese;
	}

	private String getMese(String meseAnno) {
		String mese = "";
		int index = meseAnno.indexOf(' ');
		mese = meseAnno.substring(0, index);
		System.out.println("Mese substring: " + mese);
		return mese;
	}
	
	//METODO PER GESTIRE LA SCELTA DEL MESE QUANDO SI VUOLE STAMPARE UN REPORT
	private void ChoiceMessage() throws IOException {

		//QUERY PER OTTENERE I MESI DI FATTURAZIONE TRASCORSI
		String query = "SELECT DISTINCT mese FROM stipendio WHERE anno = " + cYear + " ORDER BY mese DESC";
		System.out.println(query);
		try {
			Connection conn = dc.Connect();
			PreparedStatement pst = conn.prepareStatement(query);
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				System.out.println("Mese: " + rs.getInt("mese"));
			}

			//INERISCO I 12 MESI NELLA COMBOBOX DI SCELTA. I MESI NON ANCORA TRASCORSI, FARANNO RIFERIMENTO ALL'ANNO PRECEDENTE
			List<String> choices = new ArrayList<>();
			
			for(int i=1; i<13; i++) {
				if(i <= cMonthN) {
					choices.add(convertiMese(i) + " " +  this.cYear); 
				} else {
					choices.add(convertiMese(i) + " " + (this.cYear-1)); 
				}
			}
		
			//CREO L'ALERT
			ChoiceDialog<String> dialog = new ChoiceDialog<>(cMonth + " " + cYear, choices);
			dialog.setTitle("Stampa Report");
			dialog.setHeaderText("DI quale mese vuoi stampare il Report?");
			dialog.setContentText("Scegli il mese:");

			//GESTISCO LA SCELTA
			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()) {
				String meseAnno = result.get();
				String meseSub = getMese(meseAnno);
				
				System.out.println("Mese scelto: " + result.get());
				switch (meseSub) {
				case "Gennaio":
					if (1 <= rs.getInt("mese"))
						creaReport(1, "Gennaio", cYear);
					else
						creaReport(1, "Gennaio", cYear - 1);
					break;
				case "Febbraio":
					if (2 <= rs.getInt("mese"))
						creaReport(2, "Febbraio", cYear);
					else
						creaReport(2, "Febbraio", cYear - 1);
					break;
				case "Marzo":
					if (3 <= rs.getInt("mese"))
						creaReport(3, "Marzo", cYear);
					else
						creaReport(3, "Marzo", cYear -1 );
					break;
				case "Aprile":
					if (4 <= rs.getInt("mese"))
						creaReport(4, "Aprile", cYear);
					else
						creaReport(4, "Aprile", cYear - 1);
					break;
				case "Maggio":
					if (5 <= rs.getInt("mese"))
						creaReport(5, "Maggio", cYear);
					else
						creaReport(5, "Maggio", cYear - 1);
					break;
				case "Giugno":
					if (6 <= rs.getInt("mese"))
						creaReport(6, "Giugno", cYear);
					else
						creaReport(6, "Giugno", cYear - 1);
					break;
				case "Luglio":
					if (7 <= rs.getInt("mese"))
						creaReport(7, "Luglio", cYear);
					else
						creaReport(7, "Luglio", cYear - 1);
					break;
				case "Agosto":
					if (8 <= rs.getInt("mese"))
						creaReport(8, "Agosto", cYear);
					else
						creaReport(8, "Agosto", cYear - 1);
					break;
				case "Settembre":
					if (9 <= rs.getInt("mese"))
						creaReport(9, "Settembre", cYear);
					else
						creaReport(9, "Settembre", cYear - 1);
					break;
				case "Ottobre":
					if (10 <= rs.getInt("mese"))
						creaReport(10, "Ottobre", cYear);
					else
						creaReport(10, "Ottobre", cYear - 1);
					break;
				case "Novembre":
					if (11 <= rs.getInt("mese"))
						creaReport(11, "Novembre", cYear);
					else
						creaReport(11, "Novembre", cYear - 1);
					break;
				case "Dicembre":
					if (12 <= rs.getInt("mese"))
						creaReport(12, "Dicembre", cYear);
					else
						creaReport(12, "Dicembre", cYear - 1);
					break;
				default:
					break;
				}
			}
		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.print("ERRORE Caricamento dati");
		}

	}

	// METODO PER RIEMPIRE AUTOMATICAMENTE LA TABELLA NEL DATABASE.
	private void automaticFillingDBMSTable() {

		//CONTROLLO SE ESISTONO NUOVI DIPENDENTI NELL'APPOSITA TABELLA NEL DATABASE
		String query1 = "SELECT d.matricola, d.nome, d.cognome " + "FROM dipendente d "
				+ "WHERE d.Matricola NOT IN (SELECT s.refMatricola " + "FROM stipendio s WHERE mese = " + cMonthN
				+ " AND anno = " + cYear + ");";

		System.out.println(query1);

		try {
			Connection conn = dc.Connect();
			ResultSet rs1 = conn.createStatement().executeQuery(query1);

			//PER OGNI NUOVO DIPENDENTE, INSERISCO I SUOI DATI NELLA TABELLA STIPENDIO DEL DATABASE
			while (rs1.next()) {

				String query2 = "INSERT INTO stipendio"
						+ "(anno, mese, stipendio, oreTot, oreStraord, oreFest, oreFerie, oreMalattia, refMatricola)"
						+ "values (" + cYear + ", " + cMonthN + ", 1000, 0, 0, 0, 168, 168, "
						+ rs1.getInt("d.matricola") + ");";

				System.out.println(query2);

				try {

					Statement st = conn.createStatement();

					st.executeUpdate(query2);

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

		//AGGIORNO LA TABELLA DEL SW
		loadDataFromDatabase();

	}

	//METODO PER CREARE LE BUSTE PAGA PER OGNI DIPENDENTE
	public void creaBustaPaga(ActionEvent event) throws SQLException, IOException {

		//QUERY PER OTTENERE I DATI DEI DIPENDENTI
		String query = "SELECT * FROM dipendente d, stipendio s WHERE d.matricola = s.refMatricola AND s.mese = "
				+ cMonthN + " AND anno = " + cYear + ";";

		System.out.println(query);

		Connection conn = dc.Connect();
		ResultSet rs = conn.createStatement().executeQuery(query);

		//PER OGNI DIPENDENTE...
		while (rs.next()) {

			wb = new XSSFWorkbook();

			//CREO UN NUOVO FILE XLSX
			XSSFSheet sheet = wb.createSheet("Busta Paga " + rs.getString("nome") + " " + rs.getString("cognome")
					+ " " + cMonth + " " + cYear);

			//IMPOSTO DE DIMENSIONI DELLE CELLE
			sheet.setColumnWidth(0, 180 * 25);// 180 caratteri
			sheet.setColumnWidth(1, 180 * 25);// 180 caratteri
			sheet.setColumnWidth(2, 180 * 25);// 180 caratteri
			sheet.setColumnWidth(3, 180 * 25);// 180 caratteri
			sheet.setColumnWidth(4, 180 * 25);// 180 caratteri
			sheet.setColumnWidth(5, 180 * 25);// 180 caratteri
			sheet.setColumnWidth(6, 180 * 25);// 180 caratteri

			sheet.setZoom(150);// zoom 150%

			// DATI AZIENDA
			XSSFRow header = sheet.createRow(0);
			header.createCell(0).setCellValue("DITTA");
			XSSFRow nomeDitta = sheet.createRow(1);
			nomeDitta.createCell(0).setCellValue("Utas srl.");
			XSSFRow via = sheet.createRow(2);
			via.createCell(0).setCellValue("Via Roma, 20");
			XSSFRow citta = sheet.createRow(3);
			citta.createCell(0).setCellValue("90133 Palermo");
			XSSFRow codiceFiscale = sheet.createRow(4);
			codiceFiscale.createCell(0).setCellValue("CF 00701710634");

			// MATRICOLA
			XSSFRow riga5 = sheet.createRow(5);
			riga5.createCell(2).setCellValue("MATRICOLA");
			riga5.createCell(3).setCellValue(rs.getInt("matricola"));

			// DATI PERSONALI
			XSSFRow riga6 = sheet.createRow(6);
			riga6.createCell(0).setCellValue("COGNOME");
			riga6.createCell(1).setCellValue(rs.getString("cognome"));
			riga6.createCell(2).setCellValue("NOME");
			riga6.createCell(3).setCellValue(rs.getString("nome"));

			XSSFRow riga7 = sheet.createRow(7);
			riga7.createCell(0).setCellValue("MANSIONE");
			riga7.createCell(1).setCellValue(rs.getString("mansione"));
			riga7.createCell(2).setCellValue("LIVELLO");
			riga7.createCell(3).setCellValue(rs.getString("livello"));

			XSSFRow riga8 = sheet.createRow(8);
			riga8.createCell(0).setCellValue("DATA DI NASCITA");
			riga8.createCell(1).setCellValue(rs.getString("dataNascita"));
			riga8.createCell(2).setCellValue("PERIODO");
			riga8.createCell(3).setCellValue(cMonth + " " + cYear);

			// INTESTAZIONE DATI STIPENDIO
			XSSFRow riga9 = sheet.createRow(10);
			riga9.createCell(0).setCellValue("DESCRIZIONE");
			riga9.createCell(1).setCellValue("ORE EFFETTUATE");
			//riga9.createCell(2).setCellValue("VALORE PER ORA");
			//riga9.createCell(3).setCellValue("IMPORTO LORDO");

			// DATI STIPENDIO FERIALE

			XSSFRow riga10 = sheet.createRow(11);
			riga10.createCell(0).setCellValue("Feriale");
			riga10.createCell(1).setCellValue(rs.getInt("oreTot"));

			// DATI STIPENDIO STRAORDINARIO

			XSSFRow riga11 = sheet.createRow(12);
			riga11.createCell(0).setCellValue("Straordinario");
			riga11.createCell(1).setCellValue(rs.getInt("oreStraord"));

			// DATI STIPENDIO FESTIVO
			XSSFRow riga12 = sheet.createRow(13);
			riga12.createCell(0).setCellValue("Festivo");
			riga12.createCell(1).setCellValue(rs.getInt("oreFest"));

			// STIPENDIO LORDO
			XSSFRow riga13 = sheet.createRow(15);
			riga13.createCell(2).setCellValue("TOTALE LORDO");
			riga13.createCell(3).setCellValue(rs.getInt("stipendio") + "€");

			// TASSAZIONE
			int tassa = 22;

			XSSFRow riga15 = sheet.createRow(16);
			riga15.createCell(2).setCellValue("TASSAZIONE");
			riga15.createCell(3).setCellValue(tassa + "%");

			// STIPENDIO NETTO
			XSSFRow riga17 = sheet.createRow(18);
			riga17.createCell(2).setCellValue("TOTALE NETTO");
			riga17.createCell(3).setCellValue(rs.getInt("stipendio")
					- (rs.getInt("stipendio") * tassa / 100) + "€");

			// CREAZIONE DEL FILE XLSX
			FileOutputStream fileOut = new FileOutputStream("Busta Paga di " + rs.getString("nome") + " "
					+ rs.getString("cognome") + " " + rs.getString("matricola") + " " + cMonth + " " + cYear + ".xlsx");

			wb.write(fileOut);
			fileOut.close();
			
			

		}
		InformationMessage("Le buste paga sono state create correttamente.");
	}

}
