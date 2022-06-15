package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	private List<String> allCities;
	private Map<String, Business> allBusinesses;
	private List<Business> vertex;
	private List<Business> businessCity;
	private List<BusinessStars> busStars;
	private YelpDao dao;
	//grafico semplice, orientato e pesato
	private Graph<Business, DefaultWeightedEdge> grafo;
	//per la ricorsione
	public List<Business> migliore;
	
	public Model() {
		this.dao = new YelpDao();
		this.allCities = new ArrayList<>();
		this.allBusinesses = new HashMap<>();
	}
	
	public List<String> getAllCities(){
		this.allCities.addAll(this.dao.getAllCities());
		return this.allCities;
	}
	
	public void setAllBusinesses() {
		this.allBusinesses.putAll(this.dao.getAllBusiness());
	}
	public List<Business> getBusinessCity(String city){
		this.businessCity = new ArrayList<>();
		this.businessCity.addAll(this.dao.getAllBusinessCity(city));
		return this.businessCity;
	}
	
	public void creaGrafo(String selCity, Integer year) {
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		//Aggiungo i vertici
		this.vertex= new ArrayList<>();
		this.vertex.addAll(this.dao.getVertici(selCity, year, allBusinesses));
		for(Business b : this.vertex) {
			this.grafo.addVertex(b);
		}
		//aggiungo gli archi
		this.busStars = new ArrayList<>();
		this.busStars.addAll(this.dao.getArchi(selCity, year, allBusinesses));
		for(BusinessStars bs : this.busStars) {
			Graphs.addEdge(this.grafo, bs.getB1(), bs.getB2(), bs.getDiffAvgStars());
		}
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	public Set<Business> getVertici(){
		return this.grafo.vertexSet();
	}
	public boolean grafoEsiste() {
		if(this.grafo != null) {
			return true;
		}else {
			return false;
		}
	}
	public Business getMigliore() {
		double massimo = 0.0;
		Business migliore = null;
		for(Business b : grafo.vertexSet()) {
			double val = 0.0;
			for(DefaultWeightedEdge e : grafo.incomingEdgesOf(b)) {
				val += grafo.getEdgeWeight(e);
			}
			for(DefaultWeightedEdge e : grafo.outgoingEdgesOf(b)) {
				val -= grafo.getEdgeWeight(e);
			}
			if(val > massimo) {
				massimo = val;
				migliore = b;
			}
		}
		return migliore;
	}
	
	public List<Business> calcolaCamminoMigliore(Business b, double x){
		Business partenza = b;
		List<Business> parziale = new ArrayList<>();
		parziale.add(partenza);
		Business arrivo = this.getMigliore();
		this.migliore = null;
		cercaCamminoMigliore(parziale, arrivo, partenza, x);
		return migliore;
	}

	private void cercaCamminoMigliore(List<Business> parziale, Business arrivo, Business partenza, double x) {
		//caso terminale: sono arrivato a destinazione ---> controllo che sia la soluzione migliore
		Business ultimo = parziale.get(parziale.size()-1);
		if(ultimo.equals(arrivo)) {
			//ho finito, controllo che sia la soluzione migliore
			if(this.migliore == null) {
				this.migliore = new ArrayList<>(parziale);
				return;
			}else if(parziale.size() < migliore.size()) {
				this.migliore = new ArrayList<>(parziale);
				return;
			}else {
				//non vado a modificare perchè questa soluzione non è migliore
				return;
			}
		}
		//devo partire sempre dall'ultimo vertice
		for(DefaultWeightedEdge e : this.grafo.outgoingEdgesOf(ultimo)) {
			if(this.grafo.getEdgeWeight(e) >= x ) {
				Business prossimo = Graphs.getOppositeVertex(this.grafo, e, ultimo);
				if(!parziale.contains(prossimo)) {
					parziale.add(prossimo);
					cercaCamminoMigliore(parziale, arrivo, prossimo, x);
					parziale.remove(prossimo);
				}
			}
			
		}
	}
	
}
