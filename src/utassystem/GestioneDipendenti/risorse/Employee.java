//Questa classe funge da classe modello, ha solo metodi getters e setters e proprieta varie.
package utassystem.GestioneDipendenti.risorse;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import utassystem.DbConnection;

public class Employee {

	private final StringProperty nome;
	private final StringProperty cognome;
	private final StringProperty password;
	private final IntegerProperty livello;
	private final StringProperty mansione;
	private final IntegerProperty matricola;
	private final StringProperty dataNascita;
	private final StringProperty sesso;

	
	public Employee(String nome, String cognome, String password, int livello, String mansione, int matricola,
			String dataNascita, String sesso) {
		super();
		this.nome = new SimpleStringProperty(nome);
		this.cognome = new SimpleStringProperty(cognome);
		this.password = new SimpleStringProperty(password);
		this.livello = new SimpleIntegerProperty(livello);
		this.mansione = new SimpleStringProperty(mansione);
		this.matricola = new SimpleIntegerProperty(matricola);
		this.dataNascita = new SimpleStringProperty(dataNascita);
		this.sesso = new SimpleStringProperty(sesso);
	}

	public final StringProperty nomeProperty() {
		return this.nome;
	}

	public final String getNome() {
		return this.nomeProperty().get();
	}

	public final void setNome(final String nome) {
		this.nomeProperty().set(nome);
	}

	public final StringProperty cognomeProperty() {
		return this.cognome;
	}

	public final String getCognome() {
		return this.cognomeProperty().get();
	}

	public final void setCognome(final String cognome) {
		this.cognomeProperty().set(cognome);
	}

	public final StringProperty passwordProperty() {
		return this.password;
	}

	public final String getPassword() {
		return this.passwordProperty().get();
	}

	public final void setPassword(final String password) {
		this.passwordProperty().set(password);
	}

	public final IntegerProperty livelloProperty() {
		return this.livello;
	}

	public final int getLivello() {
		return this.livelloProperty().get();
	}

	public final void setLivello(final int livello) {
		this.livelloProperty().set(livello);
	}

	public final StringProperty mansioneProperty() {
		return this.mansione;
	}

	public final String getMansione() {
		return this.mansioneProperty().get();
	}

	public final void setMansione(final String mansione) {
		this.mansioneProperty().set(mansione);
	}

	public final IntegerProperty matricolaProperty() {
		return this.matricola;
	}

	public final int getMatricola() {
		return this.matricolaProperty().get();
	}

	public final void setMatricola(final int matricola) {
		this.matricolaProperty().set(matricola);
	}

	public final StringProperty dataNascitaProperty() {
		return this.dataNascita;
	}

	public final String getDataNascita() {
		return this.dataNascitaProperty().get();
	}

	public final void setDataNascita(final String dataNascita) {
		this.dataNascitaProperty().set(dataNascita);
	}

	public final StringProperty sessoProperty() {
		return this.sesso;
	}

	public final String getSesso() {
		return this.sessoProperty().get();
	}

	public final void setSesso(final String sesso) {
		this.sessoProperty().set(sesso);
	}
	
	// METODI INTERAZIONE DATABASE

	private DbConnection dc;
	boolean result;

	// METODO PER INSERIRE UN NUOVO DIPENDENTE

	public void aggiungi() {
		dc = new DbConnection();
		try {
			Connection conn = dc.Connect();
			Statement st = conn.createStatement();

			String queryM = "SELECT matricola from dipendente where matricola = " + this.getMatricola() + "";
			System.out.println(queryM);
			ResultSet rsM = conn.createStatement().executeQuery(queryM);

			if (rsM.next() == false) {
				try {

					String sql = "INSERT INTO dipendente"
							+ "(matricola, password, nome, cognome, dataNascita, mansione, livello, sesso)"
							+ "values ('" + this.getMatricola() + "', '" + this.getPassword() + "', '" + this.getNome()
							+ "'," + " '" + this.getCognome() + "', '" + this.getDataNascita() + "', '"
							+ this.getMansione() + "'," + " '" + this.getLivello() + "', '" + this.getSesso() + "');";
					System.out.println(sql);
					st.executeUpdate(sql);

					System.out.println("Insermento completato");

				} catch (SQLException e) {
					System.err.println("Errore " + e);
					System.out.print("ERRORE Add Data interno");
				}
			} else {

				ErrorMessage("La MATRICOLA dell'utente che vuoi inserire è gia in uso. \nCambia MATRICOLA e riprova.");
			}

		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.println("ERRORE Caricamento dati");
			
			ErrorMessage("Non sono riuscito a collegarmi al database.");

		}

	}

	// METODO PER AGGIORNARE UN DIPENDENTE

	public void aggiorna() {
		dc = new DbConnection();
		try {
			Connection conn = dc.Connect();
			Statement st = conn.createStatement();

			String query = "SELECT * from dipendente where matricola = '" + this.getMatricola() + "'";
			ResultSet rs = conn.createStatement().executeQuery(query);

			if (rs.next() == false) {
				
				ErrorMessage("L'utente " + this.getMatricola() + " non esiste.");
				
			} else {
				if (rs.getInt("matricola") == this.getMatricola()) {
					String sql = "UPDATE dipendente " + "set nome = '" + this.getNome() + "', cognome = '"
							+ this.getCognome() + "', dataNascita = '" + this.getDataNascita() + "', mansione = '"
							+ this.getMansione() + "', livello = '" + this.getLivello() + "', password = '"
							+ this.getPassword() + "', sesso = '" + this.getSesso() + "' " + " where matricola = "
							+ this.getMatricola();
					
					System.out.println("Utente: " + this.getMatricola() + " aggiornato correttamente");
					
					st.executeUpdate(sql);
				}
			}

		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.print("ERRORE update data");
			
			ErrorMessage("Non sono riuscito a collegarmi al database.");

		}
	}

	// METODO PER LA CANCELLAZIONE DI UN DIPENDENTE

	public void cancella() {
		dc = new DbConnection();
		try {
			Connection conn = dc.Connect();
			Statement st = conn.createStatement();

			String query = "SELECT matricola from dipendente where matricola = '" + this.getMatricola() + "'";
			ResultSet rs = conn.createStatement().executeQuery(query);

			if (rs.next()) {
				String sql = "DELETE FROM dipendente " + "where matricola = '" + this.getMatricola() + "';";
				String sql2 = "DELETE FROM turno " + "where refMatricola = '" + this.getMatricola() + "';";
				String sql3 = "DELETE FROM stipendio " + "where refMatricola = '" + this.getMatricola() + "';";
				
				boolean result = confirmBox("Conferma",
						"Sei sicuro di voler eliminare " + this.getMatricola() + " dal Database?");
				if (result == true) {

					st.executeUpdate(sql2);
					st.executeUpdate(sql3);
					st.executeUpdate(sql);
					
					System.out.println("Utente '" + this.getMatricola() + "' eliminato correttamente");

					InformationMessage("L'utente " + this.getMatricola() + " è stato eliminato correttamente.");

				} else {

					InformationMessage("Operazione annullata");

				}
			} else {
				
				ErrorMessage("L'utente che vuoi eliminare non esiste.");
				
			}

		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.print("ERRORE update data");

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
		alert.setTitle("Informatio Dialog");
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

}
