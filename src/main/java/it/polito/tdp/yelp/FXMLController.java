/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.yelp;

import java.net.URL;
import java.util.ResourceBundle;

import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnLocaleMigliore"
    private Button btnLocaleMigliore; // Value injected by FXMLLoader

    @FXML // fx:id="btnPercorso"
    private Button btnPercorso; // Value injected by FXMLLoader

    @FXML // fx:id="cmbCitta"
    private ComboBox<String> cmbCitta; // Value injected by FXMLLoader

    @FXML // fx:id="txtX"
    private TextField txtX; // Value injected by FXMLLoader

    @FXML // fx:id="cmbAnno"
    private ComboBox<Integer> cmbAnno; // Value injected by FXMLLoader

    @FXML // fx:id="cmbLocale"
    private ComboBox<Business> cmbLocale; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCalcolaPercorso(ActionEvent event) {
    	this.txtResult.clear();
    	Business b = this.cmbLocale.getValue();
    	if(b == null) {
    		this.txtResult.setText("Devi selezionare un locale di partenza");
    		return;
    	}
    	double x;
    	try {
    		x = Double.parseDouble(this.txtX.getText());
    		if( x < 0 || x > 1) {
    			this.txtResult.setText("Devi inserire un valore compreso fra 0 e 1 (valore decimale con '.' come separatore");
    			return;
    		}
    	}catch(NumberFormatException e) {
    		this.txtResult.setText("Devi inserire un valore numerico");
    		return;
    	}
    	//controllo che il grafo esista
    	if(!this.model.grafoEsiste()) {
    		this.txtResult.setText("Devi prima creare il grafo");
    		return;
    	}
    	//se sono qui posso avviare la ricorsione
    	this.txtResult.appendText("CAMMINO SEMPLICE:\n");
    	for(Business ss : this.model.calcolaCamminoMigliore(b, x)) {
    		this.txtResult.appendText(""+ss.getBusinessName()+"\n");
    	}    	
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	this.txtResult.clear();
    	//prendi i dati in input
    	String city = this.cmbCitta.getValue();
    	if(city == null) {
    		this.txtResult.setText("Devi scegliere una città!");
    		return;
    	}
    	Integer year = this.cmbAnno.getValue();
    	if(year == null) {
    		this.txtResult.setText("Devi selezionare un anno!");
    		return;
    	}
    	//Se sono qui vuol dire che va tutto bene
    	this.model.setAllBusinesses();
    	//creiamo il grafo
    	this.model.creaGrafo(city, year);
    	//recuperiamo numero di vertici e archi
    	this.txtResult.appendText("# VERTICI: "+this.model.nVertici()+"\n");
    	this.txtResult.appendText("# ARCHI: "+this.model.nArchi()+"\n");
    	this.cmbLocale.getItems().clear();
    	this.cmbLocale.getItems().addAll(this.model.getVertici());
    }

    @FXML
    void doLocaleMigliore(ActionEvent event) {
    	this.txtResult.clear();
    	//controllo che il grafo esista
    	if(!this.model.grafoEsiste()) {
    		this.txtResult.setText("Devi prima creare il grafo");
    		return;
    	}
    	//se sono qui, grafo creato, posso proseguire
    	Business migliore = this.model.getMigliore();
    	this.txtResult.setText("LOCALE MIGLIORE: "+migliore.getBusinessName()+".");
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnLocaleMigliore != null : "fx:id=\"btnLocaleMigliore\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnPercorso != null : "fx:id=\"btnPercorso\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbCitta != null : "fx:id=\"cmbCitta\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtX != null : "fx:id=\"txtX\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbAnno != null : "fx:id=\"cmbAnno\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbLocale != null : "fx:id=\"cmbLocale\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
    }
    
    public void setModel(Model model) {
    	this.model = model;
    	this.cmbCitta.getItems().clear();
    	this.cmbCitta.getItems().addAll(this.model.getAllCities());
    	this.cmbAnno.getItems().clear();
    	for(int i = 2005; i <= 2013; i++) {
    		this.cmbAnno.getItems().add(i);
    	}
    }
}
