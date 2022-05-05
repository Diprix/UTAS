//Questa classe funge da classe modello, ha solo metodi getters e setters e proprieta varie.
package utassystem.GestioneDipendenti.risorse;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import utassystem.DbConnection;

public class WorkShift {
	private final IntegerProperty settimana;
	private final IntegerProperty anno;
	private final IntegerProperty giorno;
	private final StringProperty turno;
	private final StringProperty mezzo;
	private final StringProperty turnoS;
	private final StringProperty mezzoS;
	private final IntegerProperty refMatricola;
	private final StringProperty lunedi;
	private final StringProperty martedi;
	private final StringProperty mercoledi;
	private final StringProperty giovedi;
	private final StringProperty venerdi;
	private final StringProperty sabato;
	private final StringProperty domenica;
	private final StringProperty nome;
	private final StringProperty cognome;

	// Costruttori di Default
	public WorkShift(int settimana, int anno, int giorno, String turno, String mezzo, String turnoS, String mezzoS, int refMatricola, String lunedi ,String martedi, String mercoledi,
			String giovedi, String venerdi, String sabato, String domenica, String nome, String cognome) {
		super();
		this.settimana = new SimpleIntegerProperty(settimana);
		this.anno = new SimpleIntegerProperty(anno);
		this.giorno = new SimpleIntegerProperty(giorno);
		this.turno = new SimpleStringProperty(turno);
		this.mezzo = new SimpleStringProperty(mezzo);
		this.turnoS = new SimpleStringProperty(turnoS);
		this.mezzoS = new SimpleStringProperty(mezzoS);
		this.refMatricola = new SimpleIntegerProperty(refMatricola);
		this.lunedi = new SimpleStringProperty(lunedi);
		this.martedi = new SimpleStringProperty(martedi);
		this.mercoledi = new SimpleStringProperty(mercoledi);
		this.giovedi = new SimpleStringProperty(giovedi);
		this.venerdi = new SimpleStringProperty(venerdi);
		this.sabato = new SimpleStringProperty(sabato);
		this.domenica = new SimpleStringProperty(domenica);
		this.nome = new SimpleStringProperty(nome);
		this.cognome = new SimpleStringProperty(cognome);
		
	}

	


	public final IntegerProperty settimanaProperty() {
		return this.settimana;
	}
	


	public final int getSettimana() {
		return this.settimanaProperty().get();
	}
	


	public final void setSettimana(final int settimana) {
		this.settimanaProperty().set(settimana);
	}
	


	public final IntegerProperty annoProperty() {
		return this.anno;
	}
	


	public final int getAnno() {
		return this.annoProperty().get();
	}
	


	public final void setAnno(final int anno) {
		this.annoProperty().set(anno);
	}
	


	public final IntegerProperty giornoProperty() {
		return this.giorno;
	}
	


	public final int getGiorno() {
		return this.giornoProperty().get();
	}
	


	public final void setGiorno(final int giorno) {
		this.giornoProperty().set(giorno);
	}
	


	public final StringProperty turnoProperty() {
		return this.turno;
	}
	


	public final String getTurno() {
		return this.turnoProperty().get();
	}
	


	public final void setTurno(final String turno) {
		this.turnoProperty().set(turno);
	}
	
	

	public final IntegerProperty refMatricolaProperty() {
		return this.refMatricola;
	}
	


	public final int getRefMatricola() {
		return this.refMatricolaProperty().get();
	}
	


	public final void setRefMatricola(final int refMatricola) {
		this.refMatricolaProperty().set(refMatricola);
	}
	


	public final StringProperty lunediProperty() {
		return this.lunedi;
	}
	


	public final String getLunedi() {
		return this.lunediProperty().get();
	}
	


	public final void setLunedi(final String lunedi) {
		this.lunediProperty().set(lunedi);
	}
	


	public final StringProperty martediProperty() {
		return this.martedi;
	}
	


	public final String getMartedi() {
		return this.martediProperty().get();
	}
	


	public final void setMartedi(final String martedi) {
		this.martediProperty().set(martedi);
	}
	


	public final StringProperty mercolediProperty() {
		return this.mercoledi;
	}
	


	public final String getMercoledi() {
		return this.mercolediProperty().get();
	}
	


	public final void setMercoledi(final String mercoledi) {
		this.mercolediProperty().set(mercoledi);
	}
	


	public final StringProperty giovediProperty() {
		return this.giovedi;
	}
	


	public final String getGiovedi() {
		return this.giovediProperty().get();
	}
	


	public final void setGiovedi(final String giovedi) {
		this.giovediProperty().set(giovedi);
	}
	


	public final StringProperty venerdiProperty() {
		return this.venerdi;
	}
	


	public final String getVenerdi() {
		return this.venerdiProperty().get();
	}
	


	public final void setVenerdi(final String venerdi) {
		this.venerdiProperty().set(venerdi);
	}
	


	public final StringProperty sabatoProperty() {
		return this.sabato;
	}
	


	public final String getSabato() {
		return this.sabatoProperty().get();
	}
	


	public final void setSabato(final String sabato) {
		this.sabatoProperty().set(sabato);
	}
	


	public final StringProperty domenicaProperty() {
		return this.domenica;
	}
	


	public final String getDomenica() {
		return this.domenicaProperty().get();
	}
	


	public final void setDomenica(final String domenica) {
		this.domenicaProperty().set(domenica);
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
	
	
	// METODI INTERAZIONE DATABASE

		private DbConnection dc;
		boolean result;
		int isEsiste = 0;

	// METODO PER AGGIORNARE IL TURNO DI UN DIPENDENTE
	
	public void aggiorna() throws SQLException {

		dc = new DbConnection();
		
		try {
		
		Connection conn = dc.Connect();
		Statement st = conn.createStatement();
		
		
		
		String query = "UPDATE turno set oraTurno =  '"+ this.getTurno() +"' , refMezzo = "+ this.getMezzo() + ", oraTurnos =  '"+ this.getTurnoS() +"' , refMezzoS = "+ this.getMezzoS() + " WHERE refMatricola = "
				+ this.getRefMatricola() + " AND settimana = " + this.getSettimana() + " AND anno = " + this.getAnno() +" AND giorno = "+ this.getGiorno() + ";";
		System.out.println(query);
	
		st.executeUpdate(query); 
		
		
		} catch (SQLException e) {
			
			System.err.println("Errore " + e);
			System.out.println("");
			System.out.print("ERRORE update data");
			
			ErrorMessage("Non sono riuscito a collegarmi al database.");

		}
		
	}
	
	
	
	//ALERT VARI
	
	public void ErrorMessage(String txt) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("ERRORE insermento dati");
		alert.setHeaderText(null);
		alert.setContentText(txt);
		alert.showAndWait();
	}




	public final StringProperty mezzoProperty() {
		return this.mezzo;
	}
	




	public final String getMezzo() {
		return this.mezzoProperty().get();
	}




	public final StringProperty turnoSProperty() {
		return this.turnoS;
	}
	




	public final String getTurnoS() {
		return this.turnoSProperty().get();
	}
	




	public final void setTurnoS(final String turnoS) {
		this.turnoSProperty().set(turnoS);
	}
	




	public final StringProperty mezzoSProperty() {
		return this.mezzoS;
	}
	




	public final String getMezzoS() {
		return this.mezzoSProperty().get();
	}
	




	public final void setMezzoS(final String mezzoS) {
		this.mezzoSProperty().set(mezzoS);
	}
	

	
	
	
	
}
