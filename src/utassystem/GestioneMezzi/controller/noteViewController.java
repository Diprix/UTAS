package utassystem.GestioneMezzi.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;

import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import utassystem.DbConnection;
import utassystem.GestioneMezzi.risorse.Warehouse;

public class noteViewController {
	@FXML
	private TextArea txtArea;
	@FXML
	private Button btnSalva;

	private int codM;
	private int stato;
	private DbConnection dc;

	//METODO PER PRENDERE I DATI PASSATI DALLA SCHERMATA GESTIONE-MEZZI
	public void getDati(int codM, int stato) {
		this.codM = codM;
		this.stato = stato;
		//RIEMPIMENTO AUTOMATICO DELLA TEXTAREA CON I DATI PRESENTI NEL DATABASE
		riempimentoTextArea();
		verificaStato();
	}

	//METODO PER SALVARE LA NOTA E CHIUDERE LA FINESTRA IN AUTOMATICO
	@FXML
	private void closeAction(ActionEvent e) throws IOException, SQLException {

		salva();

		final Node source = (Node) e.getSource();
		final Stage stage = (Stage) source.getScene().getWindow();
		stage.close();

	}

	//METODO PER SALVARE LA NOTA NEL DATABASE
	public void salva() {
		//QUERY PER OTTENERE I DATI DEL MEZZO SELEZIONATO
		String query = "SELECT * FROM mezzo WHERE codM = " + this.codM;

		//CREO UNA NUOVA CONNESSIONE
		dc = new DbConnection();

		try {

			Connection conn = dc.Connect();
			ResultSet rs = conn.createStatement().executeQuery(query);
			
			//SE IL MEZZO ESISTE
			if (rs.next()) {
				//CREO UN OGGETTO CON I DATI DEL MEZZO E LA NUOVA NOTA
				Warehouse mezzo = new Warehouse(this.codM, null, null, rs.getInt("AnnoImm"), rs.getInt("kmUltimaRev"),
						rs.getInt("kmTot"), 1, txtArea.getText(), null);
				//AGGIORNO I DATI DEL MEZZO NEL DATABASE
				
				mezzo.aggiorna();
				System.out.println("aggiornato con nota");
			}
			/*
			 * FACCIO QUESTA INTERROGAZIONE PRIMA DELL'AGGIORNAMENTO DEL MEZZO ALTRIMENTI MI SETTEREBBE I DATI DI TIPO INT COME 0
			 */

		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.println("ERRORE Caricamento dati");

		}

	}
	
	//METODO PER IL RIEMPIMENTO AUTOMATICO DELLA TEXTAREA	
	private void riempimentoTextArea() {
		//QUERY PER PRELEVARE IL TESTO DI note DAL DATABASE
		String query = "SELECT note FROM mezzo WHERE codM = " + this.codM;
		
		//CREO UNA NUOVA CONNESSIONE
		dc = new DbConnection();

		try {

			Connection conn = dc.Connect();
			ResultSet rs = conn.createStatement().executeQuery(query);
			//SE IL MEZZO ESISTE
			if (rs.next()) {
				//RIEMPIO LA TEXTAREA CON I DATI PRELEVATI DAL DATABASE
				txtArea.setText(rs.getString("note"));
			}

		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.println("ERRORE Caricamento dati");

		}
	}
	
	private void verificaStato() {
		if(stato == 2) {
			btnSalva.setDisable(true);
			txtArea.setEditable(false);
		} else {
			btnSalva.setDisable(false);
			txtArea.setEditable(true);
		}
	}

	

}
