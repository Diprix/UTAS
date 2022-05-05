package utassystem.GestioneMezzi.controller;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;

import com.github.fxrouter.FXRouter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utassystem.DbConnection;
import utassystem.GestioneMezzi.risorse.Warehouse;
import javafx.scene.control.Label;

import javafx.scene.control.TableView;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;

public class WarehouseManagementViewController implements Initializable {
	@FXML
	private Button btnIndietro;
	@FXML
	private TableView<Warehouse> table;
	@FXML
	private TableColumn<Warehouse, String> columnCeck;
	@FXML
	private TableColumn<Warehouse, Integer> columnMezzo;
	@FXML
	private TableColumn<Warehouse, String> columnModello;
	@FXML
	private TableColumn<Warehouse, String> columnTarga;
	@FXML
	private TableColumn<Warehouse, Integer> columnUltimaRevisione;
	@FXML
	private TableColumn<Warehouse, Integer> columnKmTotali;
	@FXML
	private TableColumn<Warehouse, Integer> columnDataImmatricolazione;
	@FXML
	private TableColumn<Warehouse, String> columnNote;
	@FXML
	private TextField txtCodiceMezzo;
	@FXML
	private TextField txtModello;
	@FXML
	private TextField txtTarga;
	@FXML
	private Button btnVuota;
	@FXML
	private TextField txtPosti;
	@FXML
	private Label lblCodiceMezzo;
	@FXML
	private Label lblModello;
	@FXML
	private Label lblImmatricolazione;
	@FXML
	private Label lblPosti;
	@FXML
	private Button btnDismetti;
	@FXML
	private Button btnRevisiona;
	@FXML
	private Button btnAggiungi;

	private ObservableList<Warehouse> data;  
	private DbConnection dc;
	private int cYear;
	private boolean isConnected;
	private boolean result;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

		dc = new DbConnection(); 	 //CREO UNA NUOVA CONNESSIONE COL DATABASE
		caricaDatiDalDatabse();		//AGGIORNO LA TABELLA DEL SOFTWARE CON I DATI PRESENTI NEL DATABASE
		annoCorrente();             //ANNO CORRENTE
		if (isConnected == true) {  //SE LA COSSESSIONE E' AVVENUTA CORRETTAMENTE FAI...
			controlloAutomatico();	//CONTROLLO AUTOMATICO SUI MEZZI
		}
	}

	//METODO PER CARICARE I DATI PRESI DAL DATABASE NELLA TABELLA
	public void caricaDatiDalDatabse() {
		try {

			Connection conn = dc.Connect();             //USO LA CONNESSIONE APERTA PRIMA
			
			data = FXCollections.observableArrayList();

			String query = "SELECT * FROM mezzo";		//QUERY PER OTTENERE TUTTI I DATI DELLA TABELLA mezzo DAL DATABASE

			ResultSet rs = conn.createStatement().executeQuery(query);		//ELABORO IL RISULTATO

			while (rs.next()) {

				//AGGIUNGO OGNI TUPLA OTTENUTA NELL'ARRAY LIST DEDICATO
				data.add(new Warehouse(rs.getInt("codM"), rs.getString("descM"), rs.getString("targa"),
						rs.getInt("AnnoImm"), rs.getInt("kmUltimaRev"), rs.getInt("kmTot"), rs.getInt("stato"),
						rs.getString("note"), null));


			}
			
			isConnected = true;

		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.println("ERRORE Caricamento dati");

			ErrorMessage("Non sono riuscito a collegarmi al database.");

			isConnected = false;

		}
		
		//IMPOSTO L'ORDINE DEI DATI NELLA TABELLA
		columnCeck.setCellValueFactory(new PropertyValueFactory<>("stato"));
		columnMezzo.setCellValueFactory(new PropertyValueFactory<>("conM"));
		columnModello.setCellValueFactory(new PropertyValueFactory<>("descM"));
		columnTarga.setCellValueFactory(new PropertyValueFactory<>("targa"));
		columnUltimaRevisione.setCellValueFactory(new PropertyValueFactory<>("kmUltimaRev"));
		columnKmTotali.setCellValueFactory(new PropertyValueFactory<>("kmTot"));
		columnDataImmatricolazione.setCellValueFactory(new PropertyValueFactory<>("AnnoImm"));
		columnNote.setCellValueFactory(new PropertyValueFactory<>("note"));

		//INSERISCO I DATI DELL'ARRAYLIST NELLA TABELLA
		table.setItems(null);
		table.setItems(data);
		
	}
	
	//METODO PER RICAVARE L'ANNO CORRENTE
	private void annoCorrente() {
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		this.cYear = year;
	}

	//METODO PER TORNARE NELLA SCHERMATA DI MANAGEMENT
	public void back(ActionEvent event) throws IOException {

		FXRouter.goTo("management");
	}

	//METODO PER INSERIRE UN NUOVO MEZZO
	public void inserisciMezzo(ActionEvent event) {
		
		//CONTROLLO CHE TUTTI I CAMPI SIANO STATI COMPILATI
		if (txtCodiceMezzo.getText().equals("") || txtModello.getText().equals("") || txtTarga.getText().equals("")) {

			//IN CASO NEGATILO AVVISO L'UTENTE
			ErrorMessage("Ti sei dimenticato qualcosa.\nRiempi tutti i campi e riprova."); 

		} else {

			//CREO L'OGGETTO mezzo
			Warehouse mezzo = new Warehouse(Integer.parseInt(txtCodiceMezzo.getText()), txtModello.getText(),
					txtTarga.getText(), cYear, 0, 0, 1, "", null);

			//AGGIUNGO L'OGGETTO MEZZO AL DATABASE 
			mezzo.aggiungi();

			//AGGIORNO LA TABELLA
			caricaDatiDalDatabse();
		}
	}

	//ALERT PER SEGNALARE UN ERRORE
	public void ErrorMessage(String txt) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("ERRORE insermento dati");
		alert.setHeaderText(null);
		alert.setContentText(txt);
		alert.showAndWait();
	}

	//METODO PER CALCOLARE L'ANNO CORRENTE (IL NOME DEL METODO E' ERRATO)
	public void currentWeek(ActionEvent event) {

		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		cYear = year;
	}

	//ALERT PER LA CONFERMA DI UN'OPERAZIONE
	private boolean confirmBox(String title, String message) {
		//CREO UN NUOVO ALERT DI TIPO CONFERMA, CON DUE BOTTONI
		Alert alert = new Alert(AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
		//IMPOSTO IL TITOLO
		alert.setTitle(title);

		//MOSTRO L'ALERT E ATTENDO
		alert.showAndWait();

		//ELABORO LA SCELTA DELL'UTENTE
		if (alert.getResult() == ButtonType.YES) {
			result = true;
		} else
			result = false;

		return result;

	}
	
	//ALERT PER LA CONFERMA DI UN'OPERAZIONE DEDICATO AL CHECK DI UN MEZZO
	private boolean confirmBoxCheck(String title, String message) {
		
		ButtonType Operativo = new ButtonType("Operativo", ButtonData.OK_DONE);
		ButtonType Guasto = new ButtonType("Guasto", ButtonData.CANCEL_CLOSE);
		Alert alert = new Alert(AlertType.CONFIRMATION, "Qual'è lo stato del mezzo " + table.getSelectionModel().getSelectedItem().getConM() + "?", Operativo, Guasto);

		alert.setTitle("Stato del mezzo");
		alert.showAndWait();

		//GESTISCO LA SCELTA DELL'UTENTE
				if (alert.getResult() == Operativo){
					result = true;
				} else {
					result = false;
				}

				return result;
	}
	
	

	
	//METODO PER CAPIRE IN QUALE CELLA L'UTENTE HA CLICCATO E LANCIARE IL METODO OPPORTUNO
	public void selezione(MouseEvent event) throws SQLException, IOException {
		
		//RICAVO IL NUMERO DELLA COLONNA DOVE L'UTENTE HA CLICCATO
		TablePosition<?, ?> pos = table.getSelectionModel().getSelectedCells().get(0);
		int column = pos.getColumn();
		
		//SE L'UTENTE HA CLICCATO UNA CELLA NELLA COLONNA 0, CIOE' CHECK
		if (column == 0) {
			
			//VIENE LANCIATO IL METODO PER IL CHECK DI UN MEZZO
			checkMezzo(); 
			
		//SE L'UTENTE HA CLICCATO UNA CELLA NELLA COLONNA 7, CIOE' NOTE	
		} else if (column == 7) {
			
			//IL SISTEMA CONTROLLA CHE IL MEZZO SIA GUASTO
			String query = "SELECT stato FROM mezzo WHERE stato <> 0 AND codM = "+ table.getSelectionModel().getSelectedItem().getConM();
			
			try {
				
				Connection conn = dc.Connect();
				data = FXCollections.observableArrayList();

				ResultSet rs = conn.createStatement().executeQuery(query);
				//SE IL MEZZO E' GUASTO
				if(rs.next()) {
					
					//VIENE LANCIATO IL METODO PER MOSTRARE LA FINESTRA DELLE NOTE PASSANDOGLI IL CODICE DEL MEZZO DELLA RIGA SELEZIONATA
					note(table.getSelectionModel().getSelectedItem().getConM(), table.getSelectionModel().getSelectedItem().getStato());
				}
				
			} catch (SQLException e) {
				System.err.println("Errore " + e);
				System.out.println("ERRORE Caricamento dati");

				ErrorMessage("Non sono riuscito a collegarmi al database.");
			}
		}
		
		//ABILITO IL TASTO REVISIONA SE...
		try {
			//LO STATO DEL MEZZO SELEZIONATO E' DIVERSO DA 0
		if(table.getSelectionModel().getSelectedItem().getStato() != 0) {
			
			btnRevisiona.setDisable(false);
			
		} else {
			
			btnRevisiona.setDisable(true);
		}
		} catch (Exception e) {
			System.out.println("Errore disabilitazione tasto revisiona");
		}
	}
	
	
	//METODO PER IL CHECK DEL MEZZO
	public void checkMezzo() throws SQLException, IOException {

		//SE LO STATO DEL MEZZO E' UGUALE A 3, IL SISTEMA NON PERMETTE DI MODIFICARE LO STATO DEL MEZZO
			if(table.getSelectionModel().getSelectedItem().getStato() == 3) {
				
				ErrorMessage("Questo mezzo è stato dismesso."); 
				
				
			} else if(table.getSelectionModel().getSelectedItem().getStato() == 2) {
				
				boolean result = confirmBox("Pass revisione.", "Il mezzo è pronto per tornare in servizio?");
				if (result == true) {
					
					//VIENE CREATO UN OGGETTO mezzo CHE CONTIENE IL NUOVO STATO
					Warehouse mezzo = new Warehouse(table.getSelectionModel().getSelectedItem().getConM(), null, null, 0, table.getSelectionModel().getSelectedItem().getKmUltimaRev(),
							table.getSelectionModel().getSelectedItem().getKmTot(), 0, "", null);
					
					//VIENE AGGIORNATO LO STATO DEL MEZZO
					mezzo.aggiorna();

					//VIENE AGGIORNATA LA TABELLA
					caricaDatiDalDatabse();

					System.out.println("aggiornato");
				}

				
				
			} else if (table.getSelectionModel().getSelectedItem().getStato() == 0){
			
		//IL SISTEMA FA SCEGLIERE ALL'UTENTE SE INDICARE IL MEZZO COME OPERATIVO O COME GUASTO
			boolean result = confirmBoxCheck("Check",
					"Indicare lo stato del mezzo " + table.getSelectionModel().getSelectedItem().getConM());
			
			//SE L'UTENTE INDICA IL MEZZO COME OPERATIVO
			if (result == true) {
				
				//VIENE CREATO UN OGGETTO mezzo CHE CONTIENE IL NUOVO STATO
				Warehouse mezzo = new Warehouse(table.getSelectionModel().getSelectedItem().getConM(), null, null, 0, table.getSelectionModel().getSelectedItem().getKmUltimaRev(),
						table.getSelectionModel().getSelectedItem().getKmTot(), 0, "", null);
				
				//VIENE AGGIORNATO LO STATO DEL MEZZO
				mezzo.aggiorna();

				//VIENE AGGIORNATA LA TABELLA
				caricaDatiDalDatabse();

				System.out.println("aggiornato");
				
			//SE L'UTENTE INDICA IL MEZZO COME GUASTO
			} else {

				//VIENE APERTA LA FINESTRA note DOVE SARA POSSIBILE INDICARE IL PROBLEMA DEL MEZZO SELEZIONATO
				note(table.getSelectionModel().getSelectedItem().getConM(), table.getSelectionModel().getSelectedItem().getStato());
				
			}
		}
		

	}

	//METODO PER LA REVISIONE DI UN MEZZO
	public void revisionaMezzo(ActionEvent event) {
		//IL SISTEMA VERIFICA CHE IL MEZZO SELEZIONATO SIA STATO SEGNALATO COME TALE
		if (table.getSelectionModel().getSelectedItem().getStato() != 1) {
			
			//SE IL MEZZO E' OPERATIVO, IL SISTEMA IMPEDISCE CHE VENGA MANDATOI IN REVISIONE
			ErrorMessage("Questo mezzo no ha motivo di essere revisionato.");
			
		//SE IL MEZZO SELEZIONATO E' SEGNALATO COME GUASTO	
		} else {
			//IL SISTEMA CHIEDE DI CONFERMARE L'AZIONE 
			boolean result = confirmBox("Revisiona", "Sei sicuro di voler mandare il mezzo "
					+ table.getSelectionModel().getSelectedItem().getConM() + " in revisone?");
			//SE L'UTENTE DA LA CONFERMA
			if (result == true) {
				
				//VIENE CREATO UN OGGETTO CON IL NUOVO STATO
				Warehouse mezzo = new Warehouse(table.getSelectionModel().getSelectedItem().getConM(), null, null, 0, table.getSelectionModel().getSelectedItem().getKmUltimaRev(),
						table.getSelectionModel().getSelectedItem().getKmTot(), 2, table.getSelectionModel().getSelectedItem().getNote(), null);
				
				//VIENE AGGIORNATO LO STATO DEL MEZZO SELEZIONATO
				mezzo.aggiorna();
				
				//VIENE AGGIORNATA LA TABELLA
				caricaDatiDalDatabse();
				
				System.out.println("aggiornato");
			
			//SE L'UTENTE NON DA IL CONSENSO IL SISTEMA ANNULLA L'OPERAZIONE
			} else {

				System.out.println("annullato");

			}

		}
	}
	
	//METODO PER LA DISMESSA DI UN MEZZO
	public void dismettiMezzo(ActionEvent event) {
			
				//VIENE CREATO UN OGGETTO CON I DATI DEL MEZZO DA ELIMINARE
				Warehouse mezzo = new Warehouse(table.getSelectionModel().getSelectedItem().getConM(), null, null, 0, 0,
						0, 3, null, null);
				
				//IL SISTEMA ELIMINA IL MEZZO (IN QUESTA FASE DI TEST, VIENE SOLO AGGIORNATOLO STATO A 3 CHE INDICA DA DISMISSIONE DEL MEZZO)
				mezzo.cancella();

				//VIENE AGGIORNATA LA TABELLA
				caricaDatiDalDatabse();

				System.out.println("aggiornato");
	
	}
	
	//METODO PER IL CONTROLLO AUTOMATICO DEI MEZZI
	private void controlloAutomatico() {
		
		//QUERY PER OTTENERE I DATI DELLA TABELLA mezzo DAL DATABASE
		String query = "SELECT * FROM mezzo";
		
		try {
			//CONNESSIONE AL DATABASE
			Connection conn = dc.Connect();
			data = FXCollections.observableArrayList();

			ResultSet rs = conn.createStatement().executeQuery(query);

			while (rs.next()) {
				//SE IL MEZZO ESAMINATO HA SUPERATO I 40000(40 MILA) CHILOMETRI DALL'ULTIMA REVISIONE
				//IL SISTEMA SEGNERA AUTOMATICAMENTE IL MEZZO COME GUASTO
				
				if(rs.getInt("kmUltimaRev") >= 40000) {
					Warehouse mezzo = new Warehouse(rs.getInt("codM"), null, null, 0, 0,
							0, 1, "Raggiuna sogli massima dei km dall'ultima revisione", null);

					mezzo.aggiorna();

					caricaDatiDalDatabse();

					System.out.println("aggiornamento automatico per kmUltimaRev");
				
				} //METODO ANALOGO PER LA DISMESSA AUTOMATICA NEL CASO IN CUI IL MEZZO SUPERI I 500000 (500 MILA) CHILOMETRI TOTALI
				
				/*else if(rs.getInt("kmTot") >= 500000) {
					Warehouse mezzo = new Warehouse(rs.getInt("codM"), null, null, 0, 0,
							0, 3, "Questo mezzo non è piu autorizzato alla marcia su suolo pubblico.\nKm massimi raggiunti.");

					mezzo.aggiorna();

					caricaDatiDalDatabse();

					System.out.println("aggiornamento automatico per kmUltimaRev");
					
				} */
				
			}

		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.println("ERRORE Caricamento dati");

			ErrorMessage("Non sono riuscito a collegarmi al database.");

		

		}
		
	}
	
	//METODO PER LA GESTIONE E L'APERTURA DELLA FINESTRA note
	public void note(int codM, int stato) throws IOException {

		
			try {
				
				Stage window = new Stage();
				FXMLLoader loader = new FXMLLoader();
				Pane root = loader.load(getClass().getResource("../interfacce/noteView.fxml").openStream());
				noteViewController note = (noteViewController) loader.getController();
				note.getDati(codM, stato);
				Scene scene = new Scene(root);
					scene.getStylesheets().addAll(
							getClass().getResource("../risorse/noteStyle.css").toExternalForm(), 
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
			
			//DOPO L'INTERAZIONE CON LA FINESTRA note IL SISTEMA AGGIORNA LA TABELLA
			caricaDatiDalDatabse();

	}
	
	//METODO PER RESETTARE I CAMPI
	public void resettaCampi(ActionEvent event) {
		
		txtCodiceMezzo.setText(null);
		txtModello.setText(null);
		txtTarga.setText(null);
		
	}
	
	//METODO PER INSERIRE L'IMMAGINE NELLA TABELLA
//---------------------------------------------------------------------LAVORI IN CORSO----------------------------------------------------------------
	/*
	private void immagine() {
		String query = "SELECT count(codM) FROM mezzo";
		
		try {
			
			Connection conn = dc.Connect();
			data = FXCollections.observableArrayList();

			ResultSet rs = conn.createStatement().executeQuery(query);
			
			if(rs.next()) {
				
				int i;
				
				for(i = 0; i <= rs.getInt("count(codM)"); i++) {
					int stato = table.getItems().get(i);
				}
				
			}
			
		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.println("ERRORE Caricamento IMMAGINI");

			ErrorMessage("Non sono riuscito a collegarmi al database.");
		}
		
	}
	*/

}