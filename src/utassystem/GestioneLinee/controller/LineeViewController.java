package utassystem.GestioneLinee.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.github.fxrouter.FXRouter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import utassystem.DbConnection;
import utassystem.GestioneLinee.risorse.Line;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class LineeViewController implements Initializable {
	@FXML
	private Tab lineeTab;
	@FXML
	private ComboBox<String> txtNumeroLinea;
	@FXML
	private TextField txtLatitudine;
	@FXML
	private TextField txtLongitudine;
	@FXML
	private TextField txtNomeTappa;
	@FXML
	private TextField txtNumeroTappa;
	@FXML
	private TextField txtKmTappa;
	@FXML
	private TableView<Line> table;
	@FXML
	private TableColumn<Line, Integer> columnNumeroLinea;
	@FXML
	private TableColumn<Line, Integer> ColumnNumeroTappa;
	@FXML
	private TableColumn<Line, Double> columnLatitudine;
	@FXML
	private TableColumn<Line, Double> columnLongitudine;
	@FXML
	private TableColumn<Line, String> columnNomeTappa;
	@FXML
	private TableColumn<Line, Double> columnKmLinea;
	@FXML
	private Button btnAggiungi;
	@FXML
	private Button btnResetta;
	@FXML
	private Button btnAggiorna;
	@FXML
	private Button btnElimina;

	

	private ObservableList<Line> data;
	private DbConnection dc;
	private static boolean result;
	boolean isSelected = false;
	List<String> list;
	private boolean isConnected;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		dc = new DbConnection();

		loadDataFromDBMS();
		if (isConnected == true) {
			fillComboBoxLinea();
		}

	}

	public void back(ActionEvent event) throws Exception {

		FXRouter.goTo("management");

	}

	// line area

	public void loadDataFromDBMS() {
		try {

			Connection conn = dc.Connect(); // mi connetto al database
			data = FXCollections.observableArrayList();

			// creo la query
			String query = "SELECT * FROM tappa ORDER BY nLine, nTappa ASC";

			System.out.println(query); // stampo la query per controllare che sia corretta

			ResultSet rs = conn.createStatement().executeQuery(query); // eseguo la query
			while (rs.next()) { // gestisco il risultato dell'interrogazione

				// Prendo le String dal DBMS, e le aggiungo nell' arrayList data
				data.add(new Line(rs.getDouble("latitudine"), rs.getDouble("longitudine"), rs.getString("nomeTappa"),
						rs.getInt("nLine"), rs.getInt("nTappa"), rs.getDouble("kmLine")));

			}

			isConnected = true;

		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.print("ERRORE Caricamento dati");
			ErrorMessage("Non sono riuscito a collegarmi al database.");
			isConnected = false;

		}

		fillComboBoxLinea();
		
		columnLatitudine.setCellValueFactory(new PropertyValueFactory<Line, Double>("lat"));
		columnLongitudine.setCellValueFactory(new PropertyValueFactory<Line, Double>("lng"));
		columnNomeTappa.setCellValueFactory(new PropertyValueFactory<Line, String>("name"));
		columnNumeroLinea.setCellValueFactory(new PropertyValueFactory<Line, Integer>("nLinea"));
		ColumnNumeroTappa.setCellValueFactory(new PropertyValueFactory<Line, Integer>("nTappa"));
		columnKmLinea.setCellValueFactory(new PropertyValueFactory<Line, Double>("kmLinea"));

		table.setItems(null);
		table.setItems(data); // inserisco i dati ottenuti nella tabella

	}

	public void addLineButtonPressed(ActionEvent event) throws SQLException {

		if (txtNumeroLinea.getValue() == "" || txtLatitudine.getText().equals("") || txtLongitudine.getText().equals("")
				|| txtNomeTappa.getText().equals("") || txtNumeroTappa.getText().equals("")) {
			ErrorMessage("Ti sei dimenticato qualcosa.\n Riempi tutti i campi e riprova.");
		} else {
			if (isInt(txtNumeroLinea.getValue()) == false) {
				ErrorMessage("Attenzione, numero LINEA deve essere un numero!");
			} else if (isDouble(txtLatitudine.getText()) == false) {
				ErrorMessage("Attenzione, LATITUDINE deve essere un numero!");

			} else if (isDouble(txtLongitudine.getText()) == false) {
				ErrorMessage("Attenzione, LONGITUDINE deve essere un numero!");

			} else if (isInt(txtNumeroTappa.getText()) == false) {
				ErrorMessage("Attenzione, numero TAPPA deve essere un numero!");

			} else {

					Line tappa = new Line(Double.parseDouble(txtLatitudine.getText()), Double.parseDouble(txtLongitudine.getText()), txtNomeTappa.getText(),
							Integer.parseInt(txtNumeroLinea.getValue()), Integer.parseInt(txtNumeroTappa.getText()), Double.parseDouble(txtKmTappa.getText()));
					
					tappa.aggiungi();
					String linea = txtNumeroLinea.getValue();
					loadDataFromDBMS();
					txtNumeroLinea.setValue(linea);
					
			}
		}
		
		
		
	}

	public void deleteLineDataOnDBMS(ActionEvent event) {

		if (isSelected == false) {
			ErrorMessage("Devi prima selezionare la Tappa da eliminate!");
		} else {
			
				Line tappa = new Line(0, 0, null,
						table.getSelectionModel().getSelectedItem().getNLinea(), table.getSelectionModel().getSelectedItem().getNTappa(), 0);
				
				tappa.elimina();
				String linea = txtNumeroLinea.getValue();
				loadDataFromDBMS();
				txtNumeroLinea.setValue(linea);
		}
	}

	public static boolean isDouble(String str) {

		try {
			double iCheck = Double.parseDouble(str);
			System.out.println(iCheck);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}

	}

	public static boolean isInt(String str) {

		try {
			int iCheck = Integer.parseInt(str);
			System.out.println(iCheck);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}

	}

	public void select(MouseEvent event) {
		isSelected = true;
	}

	public void vuotaCampi(ActionEvent event) {

		txtLatitudine.setText(null);
		txtLongitudine.setText(null);
		txtNomeTappa.setText(null);
		txtNumeroLinea.setValue(null);
		txtNumeroTappa.setText(null);
		txtKmTappa.setText(null);

		loadDataFromDBMS();
	}

	public void fillComboBoxLinea() {
		String query = "SELECT DISTINCT nLine FROM tappa";

		try {

			Connection conn = dc.Connect();

			list = new ArrayList<String>();
			System.out.println(query);
			ResultSet rs = conn.createStatement().executeQuery(query);
			while (rs.next()) {

				list.add(rs.getString("nLine"));

			}

		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.print("ERRORE Caricamento dati");

		}

		txtNumeroLinea.getItems().clear();
		txtNumeroLinea.getItems().addAll(list);
	}

	// vari messaggi

	public void ErrorMessage(String txt) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("ERRORE insermento dati");
		alert.setHeaderText(null);
		alert.setContentText(txt);
		alert.showAndWait();
	}

	public void InformationMessage(String txt) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Dialog");
		alert.setHeaderText(null);
		alert.setContentText(txt);
		alert.showAndWait();
	}

	public void warningMessage(String txt) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Information Dialog");
		alert.setHeaderText(null);
		alert.setContentText(txt);
		alert.showAndWait();
	}

	public static boolean confirmBox(String title, String message) {

		Alert alert = new Alert(AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.CANCEL);
		alert.setTitle(title);

		alert.showAndWait();

		if (alert.getResult() == ButtonType.YES) {
			result = true;
		} else
			result = false;

		return result;

	}

	public void searchKeyReleased(ActionEvent event) {
		
		try {
			Connection conn = dc.Connect();
			data = FXCollections.observableArrayList();
			String query = "SELECT * from tappa WHERE nLine LIKE '" + txtNumeroLinea.getValue() + "%';";

			ResultSet rs = conn.createStatement().executeQuery(query);
			System.out.println(query);
			while (rs.next()) {

				data.add(new Line(rs.getDouble("latitudine"), rs.getDouble("longitudine"), rs.getString("nomeTappa"),
						rs.getInt("nLine"), rs.getInt("nTappa"), rs.getDouble("kmLine")));

			}

			

		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.print("ERRORE Caricamento dati");
			
			ErrorMessage("Non sono riuscito a collegarmi al database.");

		}

		columnLatitudine.setCellValueFactory(new PropertyValueFactory<Line, Double>("lat"));
		columnLongitudine.setCellValueFactory(new PropertyValueFactory<Line, Double>("lng"));
		columnNomeTappa.setCellValueFactory(new PropertyValueFactory<Line, String>("name"));
		columnNumeroLinea.setCellValueFactory(new PropertyValueFactory<Line, Integer>("nLinea"));
		ColumnNumeroTappa.setCellValueFactory(new PropertyValueFactory<Line, Integer>("nTappa"));
		columnKmLinea.setCellValueFactory(new PropertyValueFactory<Line, Double>("kmLinea"));

		table.setItems(null);
		table.setItems(data); 

	}
	
	//METODO PER LA SELEZIONE DI UN DIPENDENTE DALLA TABELLA

		public void selezioneTappa(MouseEvent event) throws SQLException {
			select(null);
			try {
				
				String query = "SELECT * FROM tappa WHERE ntappa = "
						+ table.getSelectionModel().getSelectedItem().getNTappa() + " AND nLine = " + table.getSelectionModel().getSelectedItem().getNLinea() + ";";
				
				Connection conn = dc.Connect();
				
				PreparedStatement pst = conn.prepareStatement(query);
				ResultSet rs = pst.executeQuery();

				while (rs.next()) {

					txtNumeroLinea.setValue(rs.getString("nLine"));
					txtLatitudine.setText(rs.getString("latitudine"));
					txtLongitudine.setText(rs.getString("longitudine"));
					txtNumeroTappa.setText(rs.getString("nTappa"));
					txtNomeTappa.setText(rs.getString("nomeTappa"));
					txtKmTappa.setText(rs.getString("kmLine"));
					
					System.out.println("Hai selezionato la tappa "+ rs.getString("nTappa") +" della linea " + rs.getString("nLine"));

				}
				
				
				pst.close();
				rs.close();

			} catch (SQLException e) {
				System.err.println("Errore " + e);
				System.out.print("ERRORE selezione dati");

				ErrorMessage("Non sono riuscito a collegarmi al database.");
			}

		}
		
		// AGGIORNA TAPPA

		public void aggiornaTappa(ActionEvent event) {

			if(txtLatitudine.getText().equals("") || txtLatitudine.getText().equals(null) || txtLongitudine.getText().equals("") || txtLongitudine.getText().equals(null) || 
					txtNomeTappa.getText().equals("") || txtNomeTappa.getText().equals(null) || txtNumeroLinea.getValue().equals("") || txtNumeroLinea.getValue().equals(null) || 
					txtNumeroTappa.getText().equals("") || txtNumeroTappa.getText().equals(null) || txtKmTappa.getText().equals("") || txtKmTappa.getText().equals(null)) {
				
				warningMessage("ATTENZIONE, devi prima selezionare una tappa.");
				
				loadDataFromDBMS();
					
			} else {
				
				Line tappa = new Line(Double.parseDouble(txtLatitudine.getText()), Double.parseDouble(txtLongitudine.getText()),
						txtNomeTappa.getText(), Integer.parseInt(txtNumeroLinea.getValue()), Integer.parseInt(txtNumeroTappa.getText()),
						Double.parseDouble(txtKmTappa.getText()));
				
				tappa.aggiorna();
				fillComboBoxLinea();
				loadDataFromDBMS();
				
			}
			
			
			}

		


}
