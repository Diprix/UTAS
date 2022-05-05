package utassystem;

import java.net.URL;
import java.util.ResourceBundle;
import com.github.fxrouter.FXRouter;

import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import utassystem.GestioneDipendenti.risorse.Employee;
import javafx.scene.control.Alert.AlertType;

public class ManagementController implements Initializable {
	private PseudoClass disabledCategory = PseudoClass.getPseudoClass("disabledcat");
	
    @FXML
    private VBox salariesBox;

    @FXML
    private VBox routesBox;

    @FXML
    private VBox workshiftsBox;

    @FXML
    private VBox yardBox;

    @FXML
    private VBox employeesBox;
    
    @FXML
    private VBox linesBox;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		int level = (Integer) ((Employee) FXRouter.getData()).getLivello();
		System.out.println(level);
		switch(level) {
			// Responsabile dipendenti
			case 1:
				this.disableCategory(yardBox);
				this.disableCategory(routesBox);
				this.disableCategory(linesBox);
			break;
			
			// Responsabile deposito
			case 2:
				this.disableCategory(salariesBox);
				this.disableCategory(employeesBox);
				this.disableCategory(workshiftsBox);
				this.disableCategory(routesBox);
				this.disableCategory(linesBox);
			break;
			
			// Responsabile corse
			case 3:
				this.disableCategory(salariesBox);
				this.disableCategory(employeesBox);
				this.disableCategory(workshiftsBox);
				this.disableCategory(yardBox);
			default:
				break;
		}

        

	}

	public void BackButtonPressed(ActionEvent event) throws Exception {
		
		boolean result = confirmBox("Conferma Logout.", "Sei sicuro di voler uscite?");
		if (result == true) {
			System.out.println("Effettuo Logout ...");

			FXRouter.goTo("home");
			System.out.println("Logout");
		}
		
	}

	public void StipendioButtonPressed(Event event) throws Exception {
		FXRouter.goTo("salary");

	}

	public void DipendenteButtonPressed(Event event) throws Exception {
		
		FXRouter.goTo("employee");
	}

	public void GestioneTurniButtonPressed(Event event) throws Exception {

		FXRouter.goTo("workShift");
		
	}
	
	public void settingButtonPressed(Event event) throws Exception {
		
		FXRouter.goTo("linee");
	}
	
	public void CORSEButtonPressed(Event event) throws Exception {
		
		FXRouter.goTo("course");

	}
	
	public void DEPOSITOButtonPressed(Event event) throws Exception {
		
		FXRouter.goTo("warehouse");
	}
	
    private void disableCategory(VBox cat) {
    	cat.setOnMouseClicked(null);
    	cat.pseudoClassStateChanged(disabledCategory, true);
    	cat.getChildren().get(0).pseudoClassStateChanged(disabledCategory, true);
    	cat.getChildren().get(1).pseudoClassStateChanged(disabledCategory, true);
    }
	
	public static boolean confirmBox(String title, String message) {

		Alert alert = new Alert(AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.CANCEL);
		alert.setTitle(title);

		alert.showAndWait();

		boolean result;
		
		if (alert.getResult() == ButtonType.YES) {
			result = true;
		} else
			result = false;

		return result;

	}
	
	public void warningMessage(String txt) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Information");
		alert.setHeaderText(null);
		alert.setContentText(txt);
		alert.showAndWait();
	}

}
