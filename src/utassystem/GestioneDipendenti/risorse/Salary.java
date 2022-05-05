package utassystem.GestioneDipendenti.risorse;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import utassystem.DbConnection;

public class Salary {

	private final IntegerProperty anno;
	private final IntegerProperty mese;
	private DoubleProperty stipendio;
	private IntegerProperty oreTot;
	private IntegerProperty oreStraord;
	private IntegerProperty oreFest;
	private IntegerProperty oreFerie;
	private IntegerProperty oreMalattia;
	private final IntegerProperty refMatricola;
	private final StringProperty refNome;
	private final StringProperty refCognome;
	
	
	public Salary(int anno, int mese, double stipendio, int oreTot,
			int oreStraord, int oreFest, int oreFerie, int oreMalattia,
			int refMatricola, String refNome, String refCognome) {
		super();
		this.anno = new SimpleIntegerProperty(anno);
		this.mese = new SimpleIntegerProperty(mese);
		this.stipendio = new SimpleDoubleProperty(stipendio);
		this.oreTot = new SimpleIntegerProperty(oreTot);
		this.oreStraord = new SimpleIntegerProperty(oreStraord);
		this.oreFest = new SimpleIntegerProperty(oreFest);
		this.oreFerie = new SimpleIntegerProperty(oreFerie);
		this.oreMalattia = new SimpleIntegerProperty(oreMalattia);
		this.refMatricola = new SimpleIntegerProperty(refMatricola);
		this.refNome = new SimpleStringProperty(refNome);
		this.refCognome = new SimpleStringProperty(refCognome);
	}
	
	public Salary(int anno, int mese, int refMatricola) {
		super();
		this.anno = new SimpleIntegerProperty(anno);
		this.mese = new SimpleIntegerProperty(mese);
		this.refMatricola = new SimpleIntegerProperty(refMatricola);
		this.stipendio = new SimpleDoubleProperty(0.0);
		this.oreTot = new SimpleIntegerProperty(0);
		this.oreStraord = new SimpleIntegerProperty(0);
		this.oreFest = new SimpleIntegerProperty(0);
		this.oreFerie = new SimpleIntegerProperty(0);
		this.oreMalattia = new SimpleIntegerProperty(0);
		this.refCognome = new SimpleStringProperty("");
		this.refNome = new SimpleStringProperty("");
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
	


	public final IntegerProperty meseProperty() {
		return this.mese;
	}
	


	public final int getMese() {
		return this.meseProperty().get();
	}
	


	public final void setMese(final int mese) {
		this.meseProperty().set(mese);
	}
	


	public final DoubleProperty stipendioProperty() {
		return this.stipendio;
	}
	


	public final double getStipendio() {
		return this.stipendioProperty().get();
	}
	


	public final void setStipendio(final double stipendio) {
		this.stipendioProperty().set(stipendio);
	}
	

/*
	public final IntegerProperty oreTotProperty() {
		return this.oreTot;
	}
*/	

/*
	public final int getOreTot() {
		return this.oreTotProperty().get();
	}
*/	

/*
	public final void setOreTot(final int oreTot) {
		this.oreTotProperty().set(oreTot);
	}
*/


	public final IntegerProperty oreStraordProperty() {
		return this.oreStraord;
	}
	


	public final int getOreStraord() {
		return this.oreStraordProperty().get();
	}
	


	public final void setOreStraord(final int oreStraord) {
		this.oreStraordProperty().set(oreStraord);
	}
	


	public final IntegerProperty oreFestProperty() {
		return this.oreFest;
	}
	


	public final int getOreFest() {
		return this.oreFestProperty().get();
	}
	


	public final void setOreFest(final int oreFest) {
		this.oreFestProperty().set(oreFest);
	}
	


	public final IntegerProperty oreFerieProperty() {
		return this.oreFerie;
	}
	


	public final int getOreFerie() {
		return this.oreFerieProperty().get();
	}
	


	public final void setOreFerie(final int oreFerie) {
		this.oreFerieProperty().set(oreFerie);
	}
	


	public final IntegerProperty oreMalattiaProperty() {
		return this.oreMalattia;
	}
	


	public final int getOreMalattia() {
		return this.oreMalattiaProperty().get();
	}
	


	public final void setOreMalattia(final int oreMalattia) {
		this.oreMalattiaProperty().set(oreMalattia);
	}
	
	public final IntegerProperty oreTotProperty() {
		return this.oreTot;
	}
	


	public final int getOreTot() {
		return this.oreMalattiaProperty().get();
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


	public final StringProperty refNomeProperty() {
		return this.refNome;
	}
	


	public final String getRefNome() {
		return this.refNomeProperty().get();
	}
	


	public final void setRefNome(final String refNome) {
		this.refNomeProperty().set(refNome);
	}
	


	public final StringProperty refCognomeProperty() {
		return this.refCognome;
	}
	


	public final String getRefCognome() {
		return this.refCognomeProperty().get();
	}
	


	public final void setRefCognome(final String refCognome) {
		this.refCognomeProperty().set(refCognome);
	}
	
	//Metodo che ritorna lo stipendio, in base alle ore svolte
	
	public double calcolaStipendio(int livello) {
		if(livello == 0) {
			return this.oreTot.intValue() * 18 + this.oreFest.intValue() * 20 + this.oreStraord.intValue() * 21 + this.oreFerie.intValue()*8*17;
		}
		if(livello < 4) {
			return this.oreTot.intValue() * 12 + this.oreFest.intValue() * 14 + this.oreStraord.intValue() * 15 + this.oreFerie.intValue()*8*11;
		} else {
			return this.oreTot.intValue() * 9 + this.oreFest.intValue() * 10 + this.oreStraord.intValue() * 11 + this.oreFerie.intValue()*8*8;
		}
		
	}
	
	//Metodo per il calcolo delle ore svolte in base al turno
	public int oreSvolte(String turno) {
		
		if(turno.contains(":")) {
			turno = turno.replace(turno.charAt(2), '.');
		}

		if(turno.compareTo("") == 0 ) {
			return 0;
		}
		String ora1 = turno.substring(0, 5);
		String ora2 = turno.substring(6, 11);
		double o1 = Double.parseDouble(ora1);
		double o2 = Double.parseDouble(ora2);
		int oresvolte;
		if(o2 < o1) {
			oresvolte = (int)((o2+24.0) - o1);
		} else {
			oresvolte = (int)(o2 - o1);
		}
		return oresvolte;
	}
	
	
	public void calcolaStipendio(int settimanaCorrente, int livello, DbConnection dc) throws SQLException {
		Connection conn = dc.Connect();
		Statement st = conn.createStatement();
		//Query per ottenere tutti i turni fatti dall'inizio del mese a oggi
		String query = "SELECT * FROM turno WHERE anno = '" + this.getAnno() + "' and settimana > '" + (this.getMese()-1)*4 + "' and settimana < '" + settimanaCorrente + "' and refMatricola = '" + this.refMatricola.intValue() + "'";
		System.out.println(this.refMatricola.intValue());
		ResultSet rs = conn.createStatement().executeQuery(query);
		String up = "";
		while(rs.next()) {
			//System.out.println("OraTurno: " + rs.getString("oraTurno") + " OreSvolte: " + oreSvolte(rs.getString("oraTurno")) + " Settimana: " + rs.getInt("settimana"));
			
			//In base al contenuto del turno aggiorno il corrispettivo attributo
			
			
			if(rs.getString("oraTurno").toUpperCase().compareTo("MALATTIA") == 0) {
				this.oreMalattia = new SimpleIntegerProperty(this.oreMalattia.intValue() + 1);
			} else if(rs.getString("oraTurno").toUpperCase().compareTo("FERIE") == 0) {
				this.oreMalattia = new SimpleIntegerProperty(this.oreMalattia.intValue() + 1);
			} else if(rs.getInt("giorno") == 6 || rs.getInt("giorno") == 7) {
				this.oreFest = new SimpleIntegerProperty(this.oreFest.intValue() + oreSvolte(rs.getString("oraTurno")));
			} else {
				this.oreTot = new SimpleIntegerProperty(this.oreTot.intValue() + oreSvolte(rs.getString("oraTurno")));
			}
			
			//Verifico la presenza di straordinari
			
			if(this.oreSvolte(rs.getString("oraTurnoS")) != 0) {
				this.oreStraord = new SimpleIntegerProperty(this.oreStraord.intValue() + oreSvolte(rs.getString("oraTurnoS")));
				//System.out.println("OraTurnoS: " + rs.getString("oraTurnoS") + " OreSvolte: " + oreSvolte(rs.getString("oraTurnoS")) + " Settimana: " + rs.getInt("settimana"));
			} else {
				continue;
			}
		}
		
		System.out.println("OreTotali: " + this.oreTot.intValue());
		System.out.println("OreStraordinari: " + this.oreStraord.intValue());
		System.out.println("Ferie: " + this.oreFerie.intValue());
		System.out.println("Stipendio: " + this.calcolaStipendio(livello));
		System.out.println("Festivi: " + this.oreFest.intValue());
		this.stipendio = new SimpleDoubleProperty(this.calcolaStipendio(livello));
		
		up = "UPDATE stipendio set oreTot = '" + this.oreTot.intValue() + "', oreStraord = '" 
				+ this.oreStraord.intValue() + "', oreFerie = '" + this.oreFerie.intValue() +
				"', oreMalattia = '" + this.oreMalattia.intValue() + "', stipendio = '"
				+ this.calcolaStipendio(livello) + "', oreFest = '" + this.oreFest.intValue() +"' where refMatricola = '" + 
				this.refMatricola.intValue() + "' AND mese = "+ this.getMese();
		st.executeUpdate(up);
		System.out.println(up);
	}
	
}
