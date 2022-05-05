package utassystem.RicercaPercorso.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import org.w3c.dom.Document;
import com.google.gson.Gson;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import utassystem.DbConnection;
import utassystem.RicercaPercorso.risorse.Coordinate;
import utassystem.RicercaPercorso.risorse.JSBridge;

import com.github.fxrouter.FXRouter;

public class MapController {
    @FXML
    private Label busline;
    
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private WebView webview;
    
    private DbConnection dc;
    
    //private ObservableList<Coordinate> coords = FXCollections.observableArrayList();
    ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
    
    @FXML
    void goBack(ActionEvent event) throws Exception {
    	FXRouter.goTo("home");
    }
    
    // invoke js initialize() passing a json of coords
    String jsCmd() {
        Gson gson = new Gson();
        String json = gson.toJson(coords);
        System.out.println(json);
    	return "initialize(" + json + ");"; 	
    } 
    
    // create map and walk through the set of coords
    void travel() {
        webview.getEngine();
        WebEngine engine = webview.getEngine();
        engine.load(getClass().getResource("../risorse/map.html").toString());
        
        // DOM ready listener
        engine.documentProperty().addListener(new ChangeListener<Document>() {
            @Override public void changed(ObservableValue<? extends Document> observableValue, Document document, Document newDoc) {
              if (newDoc != null) {
            	engine.documentProperty().removeListener(this);
            	engine.executeScript(jsCmd());
              }
            }
          });
        
        // active js debugger
        engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                JSObject window = (JSObject) engine.executeScript("window");
                window.setMember("java", new JSBridge());
                engine.executeScript("console.log = function(message) { java.log(message); }");
           }
        });
    }

    @FXML
    void initialize() {
    	dc = new DbConnection();

    	String []linea = (String[]) FXRouter.getData();
    	
		System.out.println("Creo la mappa della line: " + linea[0]);
    	
		System.out.println("QUI: vettote: "+linea[0]+" "+linea[1]+" "+linea[2]);
		
		if(Integer.parseInt(linea[1]) == 0 && Integer.parseInt(linea[2]) == 0) {
			settingMap(linea[0]);
		} else {
			settingMap(linea);
		}
		
		
    	
        this.travel();  
        busline.setText(linea[0]);
    }
  
    public void settingMap(String []line) {
    	String max = "";
    	String min = "";
    	String ordine = "";
    	
    	
    	if(Integer.parseInt(line[1]) > Integer.parseInt(line[2])) {
    		max = line[1];
    		min = line[2];
    		ordine = "DESC";
    	} else {
    		max = line[2];
    		min = line[1];
    		ordine = "ASC";
    	}
    	
    	System.out.println("Linea: " + line[0] + "min: " + min + " max: " + max);
    	try {
			String query = "SELECT latitudine, longitudine, nomeTappa "
					+ "FROM tappa "
					+ "WHERE nLine = " + line[0] +" and nTappa <= '" + max + "' and nTappa >= '" + min + "' ORDER BY nTappa " + ordine;
			
			Connection conn = dc.Connect();
			
			ResultSet rs = conn.createStatement().executeQuery(query); 
			
			
			while (rs.next()) { 
				System.out.println(rs.getString("nomeTappa"));
				coords.add(new Coordinate(rs.getDouble("latitudine"), rs.getDouble("longitudine"), rs.getString("nomeTappa")));
				
			}

		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.print("ERRORE Caricamento dati");
		
		}
		
    	
    }
    
    public void settingMap(String line) {
    	
        
    	try {
			String query = "SELECT latitudine, longitudine, nomeTappa FROM tappa WHERE nLine = "+ line +" ORDER BY nTappa ASC";
			
			Connection conn = dc.Connect();
			
			ResultSet rs = conn.createStatement().executeQuery(query); 
			
			
			while (rs.next()) { 
				
				coords.add(new Coordinate(rs.getDouble("latitudine"), rs.getDouble("longitudine"), rs.getString("nomeTappa")));
				
			}

		} catch (SQLException e) {
			System.err.println("Errore " + e);
			System.out.print("ERRORE Caricamento dati");
		
		}
		
    	
    }
    
}
