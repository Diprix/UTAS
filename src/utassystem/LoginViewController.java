package utassystem;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import com.github.fxrouter.FXRouter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.css.PseudoClass;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import utassystem.GestioneDipendenti.risorse.Employee;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.event.Event;

public class LoginViewController implements Initializable {
	private PseudoClass wrongCredentials = PseudoClass.getPseudoClass("wrong");

	@FXML
	private PasswordField password;

	@FXML
	private TextField matricola;


	private DbConnection dc;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		dc = new DbConnection();

		
	}

	@FXML
	void onUsername(Event event) {
		if (matricola.getPseudoClassStates().contains(wrongCredentials)) {
			matricola.pseudoClassStateChanged(wrongCredentials, false);
			matricola.setPromptText("Inserisci matricola");
			password.pseudoClassStateChanged(wrongCredentials, false);
			password.setPromptText("Inserisci password");
		}
	}

	@FXML
	void onPassword(Event event) {
		if (password.getPseudoClassStates().contains(wrongCredentials)) {
			password.pseudoClassStateChanged(wrongCredentials, false);
			password.setPromptText("Inserisci password");
			matricola.pseudoClassStateChanged(wrongCredentials, false);
			matricola.setPromptText("Inserisci matricola");
		}
	}
	
	@FXML
	void authenticate(ActionEvent event) throws IOException {
		try {
			Connection conn = dc.Connect();
			System.out.println("Mi connetto al DBMS ...");
			System.out.println("Connesso.");
			
			
			
			String sql = "select * from dipendente where matricola = '" + matricola.getText() + "' and password = '"
					+ password.getText() + "' and livello < 4";

			ResultSet rs = conn.prepareStatement(sql).executeQuery();
			System.out.println("Interrogo il DBMS");
			System.out.println(sql);
			
			if (rs.next()) {


				Employee loggato = new Employee(rs.getString("nome"), rs.getString("cognome"), rs.getString("password"), 
						rs.getInt("livello"), rs.getString("mansione"), rs.getInt("matricola"), rs.getString("dataNascita"), rs.getString("sesso"));

				
				System.out.println(rs.getString("livello"));
				FXRouter.goTo("management", loggato);
				System.out.println("Login Success");
				System.out.println("Benvenuto " + matricola.getText());
				
			} else {
				
				System.out.println("Login Failed - User not found");
				matricola.pseudoClassStateChanged(wrongCredentials, true);
				matricola.clear();
				matricola.setPromptText("Matricola o password errati");
				password.pseudoClassStateChanged(wrongCredentials, true);
				password.clear();
				password.setPromptText("Matricola o password errati");
				
			}
			
			
			conn.close();
			
		} catch (Exception e) {
			//e.printStackTrace();
			
			System.out.println("ERRORE Login " + e);

			ErrorMessage("Non sono riuscito a collegarmi al database.");
		}

	}

	@FXML
	void goToEntry(ActionEvent event) throws IOException {
		FXRouter.goTo("home");
	}
	
	public void ErrorMessage(String txt) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("ERRORE accesso al database");
		alert.setHeaderText(null);
		alert.setContentText(txt);
		alert.showAndWait();
	}
	
	public void enterPressed(KeyEvent event) throws IOException {
		
		if(event.getCode().toString().equals("ENTER"))
			authenticate(null);
	
	}

}
