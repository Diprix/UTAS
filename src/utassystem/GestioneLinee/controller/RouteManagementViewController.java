package utassystem.GestioneLinee.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.ResourceBundle;

import com.github.fxrouter.FXRouter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import utassystem.DbConnection;
import utassystem.GestioneLinee.risorse.Route;
import javafx.scene.control.TableColumn;

public class RouteManagementViewController implements Initializable {
	@FXML
	private Button btnIndietro;
	@FXML
	private Label lblSettimana;
	@FXML
	private Button btnPrevWeek;
	@FXML
	private Button btnNextWeek;
	@FXML
	private TableView<Route> table;
	@FXML
	private TableColumn<Route, Integer> columnID;
	@FXML
	private TableColumn<Route, String> columnLunedi;
	@FXML
	private TableColumn<Route, String> columnMartedi;
	@FXML
	private TableColumn<Route, String> columnMercoledi;
	@FXML
	private TableColumn<Route, String> columnGiovedi;
	@FXML
	private TableColumn<Route, String> columnVenerdi;
	@FXML
	private TableColumn<Route, String> columnSabato;
	@FXML
	private TableColumn<Route, String> columnDomenica;

	// variabili varie
	private ObservableList<Route> data;
	private DbConnection dc;
	private int cWeek;
	private int cYear;
	private int cMonth;
	public Stage window;
	private int cDay;
	private int indiceGiorno;
	private boolean isConnected;
	private ObservableList<String> linee = FXCollections.observableArrayList();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

		dc = new DbConnection();

		currentWeek(null);
		loadDataFromDBMS();
		if (isConnected == true)
			automaticFillingDatabaseTable();
		fillComboBoxTableCell();

		Calendar now = Calendar.getInstance();
		int week = now.get(Calendar.WEEK_OF_YEAR);


		
		columnLunedi.setCellFactory(new Callback<TableColumn<Route, String>, TableCell<Route, String>>() {
			@Override
			public TableCell<Route, String> call(TableColumn<Route, String> param) {
				return new ComboBoxTableCell<>();
			}
		});

		columnLunedi.setCellFactory(ComboBoxTableCell.forTableColumn(linee));
		columnLunedi.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Route, String>>() {
			@Override
			public void handle(TableColumn.CellEditEvent<Route, String> event) {
				((Route) event.getTableView().getItems().get(event.getTablePosition().getRow()))
						.setLunedi(event.getNewValue());

				int refMezzo = table.getSelectionModel().getSelectedItem().getRefMezzo();
				Route corsa = new Route(cWeek, 1, event.getNewValue(), refMezzo, cYear, null, null, null, null, null, null, null);
				try {
					if(cWeek < week) {
						ErrorMessage("Non puoi modificare un turno di una settimana passata!");
					} else {
					corsa.aggiorna();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				loadDataFromDBMS();
			}

		});

		columnMartedi.setCellFactory(new Callback<TableColumn<Route, String>, TableCell<Route, String>>() {
			@Override
			public TableCell<Route, String> call(TableColumn<Route, String> param) {
				return new ComboBoxTableCell<>();
			}
		});

		columnMartedi.setCellFactory(ComboBoxTableCell.forTableColumn(linee));
		columnMartedi.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Route, String>>() {
			@Override
			public void handle(TableColumn.CellEditEvent<Route, String> event) {
				((Route) event.getTableView().getItems().get(event.getTablePosition().getRow()))
						.setMartedi(event.getNewValue());

				int refMezzo = table.getSelectionModel().getSelectedItem().getRefMezzo();
				Route corsa = new Route(cWeek, 2, event.getNewValue(), refMezzo, cYear, null, null, null, null, null, null, null);
				try {
					if(cWeek < week) {
						ErrorMessage("Non puoi modificare un turno di una settimana passata!");
					} else {
					corsa.aggiorna();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				loadDataFromDBMS();
			}

		});

		columnMercoledi.setCellFactory(new Callback<TableColumn<Route, String>, TableCell<Route, String>>() {
			@Override
			public TableCell<Route,  String> call(TableColumn<Route, String> param) {
				return new ComboBoxTableCell<>();
			}
		});

		columnMercoledi.setCellFactory(ComboBoxTableCell.forTableColumn(linee));
		columnMercoledi.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Route, String>>() {
			@Override
			public void handle(TableColumn.CellEditEvent<Route, String> event) {
				((Route) event.getTableView().getItems().get(event.getTablePosition().getRow()))
						.setMercoledi(event.getNewValue());

				int refMezzo = table.getSelectionModel().getSelectedItem().getRefMezzo();
				Route corsa = new Route(cWeek, 3, event.getNewValue(), refMezzo, cYear, null, null, null, null, null, null, null);
				try {
					if(cWeek < week) {
						ErrorMessage("Non puoi modificare un turno di una settimana passata!");
					} else {
					corsa.aggiorna();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				loadDataFromDBMS();
			}

		});

		columnGiovedi.setCellFactory(new Callback<TableColumn<Route, String>, TableCell<Route, String>>() {
			@Override
			public TableCell<Route, String> call(TableColumn<Route, String> param) {
				return new ComboBoxTableCell<>();
			}
		});

		columnGiovedi.setCellFactory(ComboBoxTableCell.forTableColumn(linee));
		columnGiovedi.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Route, String>>() {
			@Override
			public void handle(TableColumn.CellEditEvent<Route, String> event) {
				((Route) event.getTableView().getItems().get(event.getTablePosition().getRow()))
						.setGiovedi(event.getNewValue());

				int refMezzo = table.getSelectionModel().getSelectedItem().getRefMezzo();
				Route corsa = new Route(cWeek, 4, event.getNewValue(), refMezzo, cYear, null, null, null, null, null, null, null);
				try {
					if(cWeek < week) {
						ErrorMessage("Non puoi modificare un turno di una settimana passata!");
					} else {
					corsa.aggiorna();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				loadDataFromDBMS();
			}

		});

		columnVenerdi.setCellFactory(new Callback<TableColumn<Route, String>, TableCell<Route, String>>() {
			@Override
			public TableCell<Route, String> call(TableColumn<Route, String> param) {
				return new ComboBoxTableCell<>();
			}
		});

		columnVenerdi.setCellFactory(ComboBoxTableCell.forTableColumn(linee));
		columnVenerdi.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Route, String>>() {
			@Override
			public void handle(TableColumn.CellEditEvent<Route, String> event) {
				((Route) event.getTableView().getItems().get(event.getTablePosition().getRow()))
						.setVenerdi(event.getNewValue());

				int refMezzo = table.getSelectionModel().getSelectedItem().getRefMezzo();
				Route corsa = new Route(cWeek, 5, event.getNewValue(), refMezzo, cYear, null, null, null, null, null, null, null);
				try {
					if(cWeek < week) {
						ErrorMessage("Non puoi modificare un turno di una settimana passata!");
					} else {
					corsa.aggiorna();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				loadDataFromDBMS();
			}

		});

		columnSabato.setCellFactory(new Callback<TableColumn<Route, String>, TableCell<Route, String>>() {
			@Override
			public TableCell<Route, String> call(TableColumn<Route, String> param) {
				return new ComboBoxTableCell<>();
			}
		});

		columnSabato.setCellFactory(ComboBoxTableCell.forTableColumn(linee));
		columnSabato.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Route, String>>() {
			@Override
			public void handle(TableColumn.CellEditEvent<Route, String> event) {
				((Route) event.getTableView().getItems().get(event.getTablePosition().getRow()))
						.setSabato(event.getNewValue());

				int refMezzo = table.getSelectionModel().getSelectedItem().getRefMezzo();
				Route corsa = new Route(cWeek, 6, event.getNewValue(), refMezzo, cYear, null, null, null, null, null, null, null);
				try {
					if(cWeek < week) {
						ErrorMessage("Non puoi modificare un turno di una settimana passata!");
					} else {
					corsa.aggiorna();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				loadDataFromDBMS();
			}

		});

		columnDomenica.setCellFactory(new Callback<TableColumn<Route, String>, TableCell<Route, String>>() {
			@Override
			public TableCell<Route, String> call(TableColumn<Route, String> param) {
				return new ComboBoxTableCell<>();
			}
		});

		columnDomenica.setCellFactory(ComboBoxTableCell.forTableColumn(linee));
		columnDomenica.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Route, String>>() {
			@Override
			public void handle(TableColumn.CellEditEvent<Route, String> event) {
				((Route) event.getTableView().getItems().get(event.getTablePosition().getRow()))
						.setDomenica(event.getNewValue());

				int refMezzo = table.getSelectionModel().getSelectedItem().getRefMezzo();
				Route corsa = new Route(cWeek, 7, event.getNewValue(), refMezzo, cYear, null, null, null, null, null, null, null);
				try {
					if(cWeek < week) {
						ErrorMessage("Non puoi modificare un turno di una settimana passata!");
					} else {
					corsa.aggiorna();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				loadDataFromDBMS();
			}

		});

	}
	
	public String convertiGiorno(int g) {
		int []v = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
		int i, tmp, mese = 0, giornoMese = 0;
		tmp = g;
		
		//Ciclo for per individuare il mese
		for(i=0; i<12; i++) {
			tmp = tmp - v[i];
			if(tmp<0) {
				mese = i+1;
				break;
			}
		}
		tmp = g;
		
		//Ciclo for per ottenere il giorno per quel mese
		for(i=0; i<mese-1; i++) {
			tmp = tmp - v[i];
		}
		if(tmp == 0) {
			mese = mese -1;
			tmp = 30;
		}
		giornoMese = tmp;
		String data = "";
		data = giornoMese + "/" + mese;
		return data;
	}

	
	public void currentWeek(ActionEvent event) {

		
		Calendar now = Calendar.getInstance();
		int week = now.get(Calendar.WEEK_OF_YEAR);
		lblSettimana.setText(Integer.toString(week));
		cWeek = week;
		int year = now.get(Calendar.YEAR);
		cYear = year;
		int month = now.get(Calendar.MONTH);
		cMonth = ++month;
		int day = now.get(Calendar.DAY_OF_YEAR);
		cDay = day;
		int index = now.get(Calendar.DAY_OF_WEEK);
		this.indiceGiorno = cDay - index + 2;
		
		//Giorno mese - giorno nell settimana
		System.out.println("Giorno nella settimana: " + now.get(Calendar.DAY_OF_WEEK));
		
		
		//int giorno = now.get(Calendar.DAY_OF_YEAR);
		//System.out.println(convertiGiorno(giorno));
		String data = convertiGiorno(indiceGiorno) + " - " + convertiGiorno(indiceGiorno + 6);
		lblSettimana.setText(cWeek + ":  " + data);
	}

	public void changeSceneButtonPressed(Event event) throws Exception {
		FXRouter.goTo("management");
	}

	public void prevWeek(ActionEvent event) {
		if (cWeek > 1) { // controllo che impedisce di andare oltre la prima settimana dell'anno, quindi
			// tornare nell'anno precedente
		cWeek--; // decremento la variabile currentWeek
		
		
		System.out.println("Giorno attuale: " + indiceGiorno);
		if(indiceGiorno > 7) {
		indiceGiorno = indiceGiorno - 7;
		}
		
		String data = convertiGiorno(indiceGiorno) + " - " + convertiGiorno(indiceGiorno + 6);
		lblSettimana.setText(cWeek + ":  " + data);
		//lblSettimana.setText(Integer.toString(cWeek));
		loadDataFromDBMS(); // carico la tabella
		automaticFillingDatabaseTable(); // riempimento automatico tabella
		}
	}

	public void nextWeek(ActionEvent event) {
		cWeek++; // incremento la variabile currentWeek
		indiceGiorno = indiceGiorno + 7;
		String data = convertiGiorno(indiceGiorno) + " - " + convertiGiorno(indiceGiorno + 6);
		lblSettimana.setText(cWeek + ":  " + data);
		loadDataFromDBMS(); // carico la tabella
		automaticFillingDatabaseTable(); // riempimento automatico tabella
	}

	public void loadDataFromDBMS() {
		String tLunedi = "";
		String tMartedi = "";
		String tMercoledi = "";
		String tGiovedi = "";
		String tVenerdi = "";
		String tSabato = "";
		String tDomenica = "";
		int mezzo = 0;

		try {

			Connection conn = dc.Connect();
			data = FXCollections.observableArrayList();

			
			String query = "SELECT distinct c.refMezzo FROM corsa c WHERE settimana = " + cWeek + " AND anno = " + cYear;

			System.out.println(query);

			ResultSet rs = conn.createStatement().executeQuery(query);

			while (rs.next()) {

				int Mezzo = rs.getInt("refMezzo");

				String query2 = "SELECT * FROM corsa WHERE refMezzo = '" + Mezzo + "'" + "AND settimana = " + cWeek
						+ " AND anno =" + cYear;

				System.out.println(query2);
				
				ResultSet rs2 = conn.createStatement().executeQuery(query2);

				while (rs2.next()) {
					System.out.println(rs2.getString("giorno"));

					int giorno = rs2.getInt("giorno");
					mezzo = rs.getInt("refMezzo");

					String linea = "";
					
					if(rs2.getInt("refLinea") == 0) {
						linea = "";
					}else {
						linea = rs2.getString("refLinea");
					}
					
					switch (giorno) {

					case 1:
						tLunedi = linea;
						break;
					case 2:
						tMartedi = linea;
						break;
					case 3:
						tMercoledi = linea;
						break;
					case 4:
						tGiovedi = linea;
						break;
					case 5:
						tVenerdi = linea;
						break;
					case 6:
						tSabato = linea;
						break;
					case 7:
						tDomenica = linea;
						break;

					}

				}

				data.add(new Route(cWeek, 0, null, mezzo, cYear, tLunedi, tMartedi, tMercoledi, tGiovedi, tVenerdi,
						tSabato, tDomenica));
						
				
			}
			isConnected = true;
		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.print("ERRORE Caricamento dati");
			ErrorMessage("Non sono riuscito a collegarmi al database.");
			isConnected = false;
		}

		columnID.setCellValueFactory(new PropertyValueFactory<>("refMezzo"));
		columnLunedi.setCellValueFactory(new PropertyValueFactory<Route, String>("lunedi"));
		columnMartedi.setCellValueFactory(new PropertyValueFactory<Route, String>("martedi"));
		columnMercoledi.setCellValueFactory(new PropertyValueFactory<Route, String>("mercoledi"));
		columnGiovedi.setCellValueFactory(new PropertyValueFactory<Route, String>("giovedi"));
		columnVenerdi.setCellValueFactory(new PropertyValueFactory<Route, String>("venerdi"));
		columnSabato.setCellValueFactory(new PropertyValueFactory<Route, String>("sabato"));
		columnDomenica.setCellValueFactory(new PropertyValueFactory<Route, String>("domenica"));

		table.setItems(null);
		table.setItems(data);

	}
	
	//ALERT VARI
	public void ErrorMessage(String txt) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("ERRORE insermento dati");
		alert.setHeaderText(null);
		alert.setContentText(txt);
		alert.showAndWait();
	}
	
	//RIEMPIMENTO AUTOMATICO DELLA TABELLA
	
	public void automaticFillingDatabaseTable() {


		String query1 = "SELECT codM FROM mezzo  "
				+ "WHERE codM NOT IN (SELECT refMezzo FROM corsa WHERE settimana = " + cWeek
				+ " AND anno = " + cYear + ");";
		System.out.println(query1);

		try {
			Connection conn = dc.Connect();

			ResultSet rs1 = conn.createStatement().executeQuery(query1);

			while (rs1.next()) {

				try {

					Statement st = conn.createStatement(); 
					
					for (int i = 1; i < 8; i++) {

						String query2 = "INSERT INTO corsa VALUES (" + cWeek + ", " + i + ", null, "
								+ rs1.getInt("codM") + ", "+cYear+");";
						System.out.println(query2);
						st.executeUpdate(query2);

					}

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("ERRORE query 2");
					ErrorMessage("Non sono riuscito a collegarmi al database.");
				}

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("ERRORE query 1");
			ErrorMessage("Non sono riuscito a collegarmi al database.");
		}

		loadDataFromDBMS();

	}

	public void fillComboBoxTableCell() {
		String query = "SELECT DISTINCT nLine from tappa";
		// linee.clear();

		try {

			Connection conn = dc.Connect();
			ResultSet rs = conn.createStatement().executeQuery(query);

			while (rs.next()) {

				linee.add(rs.getString("nLine"));

			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
