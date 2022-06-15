package it.polito.tdp.yelp.model;

public class BusinessStars {
	
	private Business b1;
	private Business b2;
	private double diffAvgStars;
	public BusinessStars(Business b1, Business b2, double diffAvgStars) {
		super();
		this.b1 = b1;
		this.b2 = b2;
		this.diffAvgStars = diffAvgStars;
	}
	public Business getB1() {
		return b1;
	}
	public void setB1(Business b1) {
		this.b1 = b1;
	}
	public Business getB2() {
		return b2;
	}
	public void setB2(Business b2) {
		this.b2 = b2;
	}
	public double getDiffAvgStars() {
		return diffAvgStars;
	}
	public void setDiffAvgStars(double diffAvgStars) {
		this.diffAvgStars = diffAvgStars;
	}
	
}
