package utassystem.GestioneDipendenti.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import utassystem.GestioneDipendenti.risorse.WorkShift;
import utassystem.DbConnection;
import javafx.scene.control.ComboBox;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

public class ChangeDataController extends WorkShiftController implements Initializable {
	@FXML
	private Label lblNome;
	@FXML
	private Label lblCognome;
	@FXML
	private Label lblMatricola;
	@FXML
	private Label txtGiorno;
	@FXML
	private Button btnSave;
	@FXML
	private Label lblMezzo;
	@FXML
	private ComboBox<String> cbTurno;
	@FXML
	private CheckBox checkStraordinario;
	@FXML
	private ComboBox<String> cbTurnoStraordinario;
	@FXML
	private ComboBox<String> cbMezzo;
	@FXML
	private ComboBox<String> cbMezzoStraordinario;

	private int column;
	private int matricola;
	private int settimana;
	private int cYear;
	private String giorno;
	private int giornoN;
	private List<String> setting;
	private DbConnection dc;
	private int livello;


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		dc = new DbConnection();
		fillComboBoxTurni();
		
		currentYear();
	}

	@FXML
	private void closeAction(ActionEvent e) throws IOException, SQLException {
		
		save();

		final Node source = (Node) e.getSource();
		final Stage stage = (Stage) source.getScene().getWindow();
		stage.close();

	}

	public void currentYear() {
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		cYear = year;
	}

	public void getDati(int matricola, int column, int settimana) {
		this.matricola = matricola;
		this.column = column;
		this.settimana = settimana;
		setGiorno();
		setIdentity();
		isDriver();
		
		riempimentoAutomaticoCombobox();
		straordinario(null);
	}
	
	private void riempimentoAutomaticoCombobox() {
		
		String query = "SELECT oraTurno, refMezzo, oraTurnoS, refMezzoS FROM turno WHERE refMatricola = "+ this.matricola +" AND giorno = "+ giornoN +" AND settimana = "+ this.settimana +" AND anno = " + cYear;
		System.out.println("QUI " + query);
		try {
			Connection conn = dc.Connect();
			ResultSet rs = conn.createStatement().executeQuery(query);

			if (rs.next()) {
				
				if(rs.getString("oraTurnoS").equals("") || rs.getString("oraTurnoS") == null) {
					checkStraordinario.setSelected(false);
					enableStraordinario(null);
				} else {
					checkStraordinario.setSelected(true);
					enableStraordinario(null);
				}
				
				cbMezzo.setValue(rs.getString("refMezzo"));
				cbMezzoStraordinario.setValue(rs.getString("refMezzoS"));
				cbTurno.setValue(rs.getString("oraTurno"));
				cbTurnoStraordinario.setValue(rs.getString("oraTurnoS"));
				
			} else {
				
				System.out.println("Si e verificato un errore con il riconoscimento della persona.");
			}

		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.print("ERRORE Caricamento dati");

		}
		
	}

	public void setGiorno() {

		switch (column) {
		case 3:
			giorno = "LUNEDI";
			txtGiorno.setText(giorno);
			giornoN = 1;
			break;
		case 4:
			giorno = "MARTEDI";
			txtGiorno.setText(giorno);
			giornoN = 2;
			break;
		case 5:
			giorno = "MERCOLEDI";
			txtGiorno.setText(giorno);
			giornoN = 3;
			break;
		case 6:
			giorno = "GIOVEDI";
			txtGiorno.setText(giorno);
			giornoN = 4;
			break;
		case 7:
			giorno = "VENERDI";
			txtGiorno.setText(giorno);
			giornoN = 5;
			break;
		case 8:
			giorno = "SABATO";
			txtGiorno.setText(giorno);
			giornoN = 6;
			break;
		case 9:
			giorno = "DOMENICA";
			txtGiorno.setText(giorno);
			giornoN = 7;
			break;

		default:
			giorno = "ERROR";
			txtGiorno.setText(giorno);
			giornoN = -1;
			break;
		}

	}

	public void fillComboBoxTurni() {
		
		ArrayList<String> orari = new ArrayList<String>();
		
		orari.add("08:00-14:00");
		orari.add("15:00-19:00");
		orari.add("19:00-01:00");
		orari.add("01:00-08:00");
		orari.add("FERIE");
		orari.add("MALATTIA");
		orari.add(null);
		cbTurno.getItems().clear();
		cbTurno.getItems().addAll(orari);  //DA AGGIUNGERE FERIE E MALATTIA
		
		
	}
	
	public void straordinario(ActionEvent event) {
		
		fillComboBoxMezzi();
		
		ArrayList<String> orari = new ArrayList<String>();
		orari.add("08:00-14:00");
		orari.add("15:00-19:00");
		orari.add("19:00-01:00");
		orari.add("01:00-08:00");
		orari.add(null);
		
		orari.remove(cbTurno.getValue());
		
		cbTurnoStraordinario.getItems().clear();
		cbTurnoStraordinario.getItems().addAll(orari); //DA AGGIUNGERE FERIE E MALATTIA
		
	}

	public void fillComboBoxMezzi() {
		
		String query = "SELECT m.codM"
					+ " FROM mezzo m"
					+ " WHERE stato = 0"
					+ " AND m.codM NOT IN (SELECT t.refMezzo"
										+ " FROM turno t"
										+ " WHERE t.refMezzo = m.codM "
										+ " AND giorno = "+ giornoN
										+ " AND oraTurno = '"+ cbTurno.getValue() +"'"
										+ " AND settimana = " + settimana
										+ " AND anno = " + cYear
										+ " ORDER BY refMezzo);";
		
		System.out.println("QUI " +query);

		try {
			Connection conn = dc.Connect();
			setting = new ArrayList<String>();
			ResultSet rs = conn.createStatement().executeQuery(query);

			while (rs.next()) {
				
				setting.add(rs.getString("codM"));

			}

		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.print("ERRORE Caricamento dati");

		}

		cbMezzo.getItems().clear();
		cbMezzo.getItems().addAll(setting);
		

	}

	public void fillComboBoxMezziStraordinario(ActionEvent event) {
		
		String query = "SELECT m.codM"
					+ " FROM mezzo m"
					+ " WHERE stato = 0"
					+ " AND m.codM NOT IN (SELECT t.refMezzo"
										+ " FROM turno t"
										+ " WHERE t.refMezzo = m.codM "
										+ " AND giorno = "+ giornoN
										+ " AND oraTurnoS = '"+ cbTurnoStraordinario.getValue() +"'"
										+ " AND settimana = " + settimana
										+ " AND anno = " + cYear
										+ " ORDER BY refMezzo);";
		
		System.out.println("QUI " +query);

		try {
			Connection conn = dc.Connect();
			setting = new ArrayList<String>();
			ResultSet rs = conn.createStatement().executeQuery(query);

			while (rs.next()) {
				
				setting.add(rs.getString("codM"));

			}

		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.print("ERRORE Caricamento dati");

		}

		cbMezzoStraordinario.getItems().clear();
		cbMezzoStraordinario.getItems().addAll(setting);

	}

	public void isDriver() {
		if (livello != 4) {
			cbMezzoStraordinario.setDisable(true);
			cbMezzo.setDisable(true);
			lblMezzo.setDisable(true);
		}
	}

	public void enableStraordinario(Event event) {
		
		if (checkStraordinario.isSelected() == true) {
			
			if (livello == 4) {
				cbMezzoStraordinario.setDisable(false);
			}

			cbTurnoStraordinario.setDisable(false);

		} else {
			cbMezzoStraordinario.setDisable(true);
			cbTurnoStraordinario.setDisable(true);
		}
		
	}

	public void setIdentity() {
		String query = "SELECT nome, cognome, livello FROM dipendente WHERE matricola = '" + matricola + "';";

		try {
			Connection conn = dc.Connect();
			ResultSet rs = conn.createStatement().executeQuery(query);

			if (rs.next()) {
				lblCognome.setText(rs.getString("cognome"));
				lblNome.setText(rs.getString("nome"));
				System.out.println(matricola);
				lblMatricola.setText(Integer.toString(matricola));
				livello = rs.getInt("livello");
			} else {
				System.out.println("Si e verificato un errore con il riconoscimento della persona.");
			}

		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.print("ERRORE Caricamento dati");

		}
	}

	public void save() throws IOException, SQLException {

		
		if (cbTurno.getValue() == null || cbTurno.getValue().equals("")) {

			WorkShift nuovoTurno = new WorkShift(settimana, cYear, giornoN, "", null,"",null,  matricola, null, null, null, null,
					null, null, null, null, null);
			nuovoTurno.aggiorna();
			
		} else {
			if (checkStraordinario.isSelected() == false) {
				if (livello == 4) {
					
					WorkShift nuovoTurno = new WorkShift(settimana, cYear, giornoN, cbTurno.getValue(), cbMezzo.getValue(),"",null, matricola, null, null, null, null,
							null, null, null, null, null);
					nuovoTurno.aggiorna();
					
				} else {

					WorkShift nuovoTurno = new WorkShift(settimana, cYear, giornoN, cbTurno.getValue(), null,"",null, matricola, null, null, null, null,
							null, null, null, null, null);
					nuovoTurno.aggiorna();
				}
			} else {
				
				if (livello == 4) {

					WorkShift nuovoTurno = new WorkShift(settimana, cYear, giornoN, cbTurno.getValue(), cbMezzo.getValue(),cbTurnoStraordinario.getValue(), cbMezzoStraordinario.getValue(), matricola, null, null, null, null,
							null, null, null, null, null);
					nuovoTurno.aggiorna();
					
		
					

				} else {
					
					WorkShift nuovoTurno = new WorkShift(settimana, cYear, giornoN, cbTurno.getValue(), null, cbTurnoStraordinario.getValue(), null,matricola, null, null, null, null,
							null, null, null, null, null);
					nuovoTurno.aggiorna();

					
				}

			}

		}

	}
	
}