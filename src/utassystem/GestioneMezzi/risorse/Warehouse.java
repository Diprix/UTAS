package utassystem.GestioneMezzi.risorse;

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
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import utassystem.DbConnection;

public class Warehouse {
	
	private final IntegerProperty conM;
	private final StringProperty descM;
	private final StringProperty targa;
	private final IntegerProperty AnnoImm;
	private final IntegerProperty kmUltimaRev;
	private final IntegerProperty kmTot;
	private final IntegerProperty stato;
	private final StringProperty note;
	private ImageView image;

	
	
	public Warehouse(int conM, String descM, String targa, int annoImm, int kmUltimaRev, int kmTot, 
			int stato, String note, ImageView image) {
		super();
		this.conM = new SimpleIntegerProperty(conM);
		this.descM = new SimpleStringProperty(descM);
		this.targa = new SimpleStringProperty(targa);
		this.AnnoImm = new SimpleIntegerProperty(annoImm);
		this.kmUltimaRev = new SimpleIntegerProperty(kmUltimaRev) ;
		this.kmTot =new SimpleIntegerProperty(kmTot) ;
		this.stato = new SimpleIntegerProperty(stato) ;
		this.note = new SimpleStringProperty(note) ;
		this.image = image;
	}



	public ImageView getImage() {
		return image;
	}



	public void setImage(ImageView image) {
		this.image = image;
	}



	public final IntegerProperty conMProperty() {
		return this.conM;
	}
	



	public final int getConM() {
		return this.conMProperty().get();
	}
	



	public final void setConM(final int conM) {
		this.conMProperty().set(conM);
	}
	



	public final StringProperty descMProperty() {
		return this.descM;
	}
	



	public final String getDescM() {
		return this.descMProperty().get();
	}
	



	public final void setDescM(final String descM) {
		this.descMProperty().set(descM);
	}
	

	public final StringProperty targaProperty() {
		return this.targa;
	}
	



	public final String getTarga() {
		return this.targaProperty().get();
	}
	



	public final void setTarga(final String targa) {
		this.targaProperty().set(targa);
	}
	

	public final IntegerProperty AnnoImmProperty() {
		return this.AnnoImm;
	}
	



	public final int getAnnoImm() {
		return this.AnnoImmProperty().get();
	}
	



	public final void setAnnoImm(final int AnnoImm) {
		this.AnnoImmProperty().set(AnnoImm);
	}
	



	public final IntegerProperty kmUltimaRevProperty() {
		return this.kmUltimaRev;
	}
	



	public final int getKmUltimaRev() {
		return this.kmUltimaRevProperty().get();
	}
	



	public final void setKmUltimaRev(final int kmUltimaRev) {
		this.kmUltimaRevProperty().set(kmUltimaRev);
	}
	



	public final IntegerProperty kmTotProperty() {
		return this.kmTot;
	}
	



	public final int getKmTot() {
		return this.kmTotProperty().get();
	}
	



	public final void setKmTot(final int kmTot) {
		this.kmTotProperty().set(kmTot);
	}
	



	public final IntegerProperty statoProperty() {
		return this.stato;
	}
	



	public final int getStato() {
		return this.statoProperty().get();
	}
	



	public final void setStato(final int stato) {
		this.statoProperty().set(stato);
	}
	



	public final StringProperty noteProperty() {
		return this.note;
	}
	



	public final String getNote() {
		return this.noteProperty().get();
	}
	



	public final void setNote(final String note) {
		this.noteProperty().set(note);
	}
	
	
	// METODI INTERAZIONE DATABASE

		private DbConnection dc;
		boolean result;
		

	// METODO PER INSERIRE UNA NUOVA TAPPA

	public void aggiungi() {
		
		dc = new DbConnection();

		try {
			
				//CONNESSIONE AL DATABASE
				Connection conn = dc.Connect();
				//QUERY PER OTTENERE TUTTI I DATI DEL MEZZO CHE HA QUEL codM, PER VERIFICARE SE GIA ESISTE
				String query = "SELECT * FROM mezzo WHERE codM = " + this.getConM();
				System.out.println(query);
				
				//ELABORO IL RISULTATO
				ResultSet rs = conn.createStatement().executeQuery(query);
				//SE ESISTE UN MEZZO CON LO STESSO CODICE MEZZO IL SISTEMA BLOCCA L'OPERAZIONE
				if (rs.next()) {

					ErrorMessage("ERRORE, il MEZZO che vuoi inserire gia esiste!"); //MESSAGGIO D'ERRORE

				} else {
					
					//QUERY PER INSERIRE UN NUOVO MEZZO NEL DATABASE
					String query2 = "INSERT INTO mezzo (codM, descM, targa, AnnoImm, kmUltimaRev, kmTot, stato, note) values ("
							+ this.getConM() + ",'" + this.getDescM() + "', '"+ this.getTarga() +"'," + this.getAnnoImm() + "," + this.getKmUltimaRev() + ","
							+ this.getKmTot() + ", "+ this.getStato() +", '"+ this.getNote() +"');";

					System.out.println(query2);
					//ESEGUO LA QUERY
					conn.createStatement().executeUpdate(query2);

				}

			} catch (SQLException e) {
				System.err.println("Errore " + e);
				System.out.println("ERRORE inserimento dati");

				ErrorMessage("Non sono riuscito a collegarmi al database.");
			}

		}
	
	// METODO PER AGGIORNARE UNA TAPPA

		public void aggiorna() {
			dc = new DbConnection();

			try {

				//CONNESSIONE AL DATABASE
				Connection conn = dc.Connect(); 
				Statement st = conn.createStatement();
				//QUERY UPDATE
				String query = "UPDATE mezzo set stato = " + this.getStato() + ", kmUltimaRev = "+ this.getKmUltimaRev() +" , kmTot = "+ this.getKmTot() +" , note = '"+ this.getNote() +"' WHERE codM = " + this.getConM();
				
				System.out.println(query);

				//ESECUZIONE QUERY
				st.executeUpdate(query); 

			} catch (SQLException e) {
				System.err.println("Errore " + e);
				System.out.println("ERRORE aggiornamento dati");

				ErrorMessage("Non sono riuscito a collegarmi al database.");
			}

		}
		
		public void cancella() {
			dc = new DbConnection();
			try {
				//CONNESSIONE
				Connection conn = dc.Connect();
				Statement st = conn.createStatement();
				
				//QUERY PER VERIFICARE SE ESISTE UN MEZZO NEL DATABASE
				String query = "SELECT codM from mezzo where codM = " + this.getConM();
				ResultSet rs = conn.createStatement().executeQuery(query);
				
				//SE ESISTE
				if (rs.next()) {
					//QUERY PER ELIMINARE IL MEZZO DALLA TABELLA mezzo
					String sql = "DELETE FROM mezzo " + "where codM = '" + this.getConM() + "';";
					//QUERY PER ELIMINARE IL MEZZO DALLA TABELLA corsa
					String sql2 = "DELETE FROM corsa " + "where refMezzo = '" + this.getConM() + "';";
					
					//IL SISTEMA CHIEDE LA CONFERMA PRIMA DI ELIMINATE LA TUPLA DAL DATABASE
					boolean result = confirmBox("Dismetti", "Sei sicuro di voler dismettere il mezzo "
							+ this.getConM() + "\nQuesta operazione non potra essere annullata.");
					//SE L'UTENTE DA LA CONFERMA
					if (result == true) {
						//IL SISTEMA ESEGUE LA QUERY (PRIMA LE SLAVE, POI LA MASTER)
						st.executeUpdate(sql2);
						st.executeUpdate(sql);
						
						System.out.println("Mezzo '" + this.getConM() + "' eliminato correttamente");

						InformationMessage("Il mezzo " + this.getConM() + " è stato eliminato correttamente.");

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

		public void warningMessage(String txt) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Informatio Dialog");
			alert.setHeaderText(null);
			alert.setContentText(txt);
			alert.showAndWait();
		}

		public boolean confirmBox(String title, String message) {

			Alert alert = new Alert(AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
			alert.setTitle(title);

			alert.showAndWait();

			if (alert.getResult() == ButtonType.YES) {
				result = true;
			} else
				result = false;

			return result;

		}




	
}