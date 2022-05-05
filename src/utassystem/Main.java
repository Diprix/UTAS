package utassystem;

import javafx.application.Application;
import javafx.stage.Stage;
import utassystem.GestioneDipendenti.risorse.Employee;

import com.github.fxrouter.FXRouter;

public class Main extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		stage.setResizable(false);

		FXRouter.bind(this, stage, 1280, 720);
		
		FXRouter.setAnimationType("fade", 400);
		
		//----------------------v PARTE DA RIMUOVERE PRIMA DELLA CONSEGNA v---------------------------------------------

				Employee loggato = new Employee("NOME", "COGNOME", "password", 
						0, "mansione", 51000, "dataNascita", "sesso");
				
		//-------------------------^ FINO A QUI ^-----------------------------------------------------------------------
				
		// Utas
		FXRouter.when("home", "HomeView.fxml", "UTAS System - Accesso");
		FXRouter.when("login", "LoginView.fxml", "UTAS System - Login");			
		FXRouter.when("management", "ManagementView.fxml", "UTAS System - Managment");	
		
		// Utas.GestioneDipendenti
		FXRouter.when("salary", "GestioneDipendenti/interfacce/SalaryManagementView.fxml", "UTAS System - Gestione Stipendi");
		FXRouter.when("employee", "GestioneDipendenti/interfacce/EmployeeManagementView.fxml", "UTAS System - Gestione Dipendenti");
		FXRouter.when("workShift", "GestioneDipendenti/interfacce/WorkShiftView.fxml", "UTAS System - Gestione Turni");
				
		// Utas.GestioneMezzi
		FXRouter.when("warehouse", "GestioneMezzi/interfacce/InterfacciaDeposito.fxml", "UTAS System - Gestione Deposito.");
				
		// Utas.GestioneLinee
		FXRouter.when("course", "GestioneLinee/interfacce/RouteManagementView.fxml", "UTAS System - Gestione Corse");
		FXRouter.when("linee", "GestioneLinee/interfacce/LineeView.fxml", "UTAS System - Impostazioni.");
			
		// Utas.RicercaPercorso
		FXRouter.when("map", "RicercaPercorso/interfacce/MapView.fxml", "UTAS System - Mappa");
		
		
		FXRouter.startFrom("management", loggato); //METTERE "home" PRIMA DELLA CONSEGNA
		
	}

	public static void main(String[] args) {
		launch(args);
	}	
	

}
