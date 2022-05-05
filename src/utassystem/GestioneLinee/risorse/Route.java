//Questa classe funge da classe modello, ha solo metodi getters e setters e proprieta varie.
package utassystem.GestioneLinee.risorse;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import utassystem.DbConnection;

public class Route {
	private final IntegerProperty settimana;
	private final IntegerProperty giorno;
	private final StringProperty codLinea;
	private final IntegerProperty refMezzo;
	private final IntegerProperty anno;
	private final StringProperty lunedi;
	private final StringProperty martedi;
	private final StringProperty mercoledi;
	private final StringProperty giovedi;
	private final StringProperty venerdi;
	private final StringProperty sabato;
	private final StringProperty domenica;

	// Costruttori di Default
	public Route(int settimana, int giorno, String codLinea, int refMezzo, int anno, String lunedi, String martedi,
			String mercoledi, String giovedi, String venerdi, String sabato, String domenica) {
		super();
		this.settimana = new SimpleIntegerProperty(settimana);
		this.giorno = new SimpleIntegerProperty(giorno);
		this.codLinea = new SimpleStringProperty(codLinea);
		this.refMezzo = new SimpleIntegerProperty(refMezzo);
		this.anno = new SimpleIntegerProperty(anno);
		this.lunedi = new SimpleStringProperty(lunedi);
		this.martedi = new SimpleStringProperty(martedi);
		this.mercoledi = new SimpleStringProperty(mercoledi);
		this.giovedi = new SimpleStringProperty(giovedi);
		this.venerdi = new SimpleStringProperty(venerdi);
		this.sabato = new SimpleStringProperty(sabato);
		this.domenica = new SimpleStringProperty(domenica);

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

	public final IntegerProperty giornoProperty() {
		return this.giorno;
	}

	public final int getGiorno() {
		return this.giornoProperty().get();
	}

	public final void setGiorno(final int giorno) {
		this.giornoProperty().set(giorno);
	}

	public final StringProperty codLineaProperty() {
		return this.codLinea;
	}

	public final String getCodLinea() {
		return this.codLineaProperty().get();
	}

	public final void setCodLinea(final String codLinea) {
		this.codLineaProperty().set(codLinea);
	}

	public final IntegerProperty refMezzoProperty() {
		return this.refMezzo;
	}

	public final int getRefMezzo() {
		return this.refMezzoProperty().get();
	}

	public final void setRefMezzo(final int refMezzo) {
		this.refMezzoProperty().set(refMezzo);
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

	// METODI INTERAZIONE DATABASE

	private DbConnection dc;
	boolean result;

	// METODO PER AGGIORNARE IL TURNO DI UN DIPENDENTE

	public void aggiorna() throws SQLException {

		dc = new DbConnection();

		try {

			Connection conn = dc.Connect();
			Statement st = conn.createStatement();

			String query2 = "SELECT stato FROM mezzo WHERE codM = " + this.getRefMezzo();
			ResultSet rs = conn.createStatement().executeQuery(query2);

			if (rs.next()) {
				if (rs.getInt("stato") == 0) {
					String query = "UPDATE corsa set refLinea = " + this.getCodLinea() + "  WHERE refMezzo = "
							+ this.getRefMezzo() + " AND anno = " + this.getAnno() + " AND settimana = "
							+ this.getSettimana() + " AND giorno = " + this.getGiorno();
					System.out.println(query);

					st.executeUpdate(query);
				} else {
					ErrorMessage("Mezzo attualmente non disponibile");

					// forse è superfluo
					String query = "UPDATE corsa set refLinea = " + null + "  WHERE refMezzo = " + this.getRefMezzo()
							+ " AND anno = " + this.getAnno() + " AND settimana = " + this.getSettimana()
							+ " AND giorno = " + this.getGiorno();
					System.out.println(query);

					st.executeUpdate(query);
				}
			}
		} catch (SQLException e) {

			System.err.println("Errore " + e);
			System.out.println("");
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

}
