package it.polito.tdp.yelp.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.BusinessStars;
import it.polito.tdp.yelp.model.Review;
import it.polito.tdp.yelp.model.User;

public class YelpDao {

	public Map<String, Business> getAllBusiness(){
		String sql = "SELECT * FROM Business";
		Map<String, Business> result = new HashMap<>();
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				Business business = new Business(res.getString("business_id"), 
						res.getString("full_address"),
						res.getString("active"),
						res.getString("categories"),
						res.getString("city"),
						res.getInt("review_count"),
						res.getString("business_name"),
						res.getString("neighborhoods"),
						res.getDouble("latitude"),
						res.getDouble("longitude"),
						res.getString("state"),
						res.getDouble("stars"));
				result.put(res.getString("business_id"), business);
				//result.add(business);
			}
			res.close();
			st.close();
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Business> getAllBusinessCity(String city){
		String sql = "SELECT * FROM Business WHERE city = ?";
		List<Business> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, city);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				Business business = new Business(res.getString("business_id"), 
						res.getString("full_address"),
						res.getString("active"),
						res.getString("categories"),
						res.getString("city"),
						res.getInt("review_count"),
						res.getString("business_name"),
						res.getString("neighborhoods"),
						res.getDouble("latitude"),
						res.getDouble("longitude"),
						res.getString("state"),
						res.getDouble("stars"));
				result.add(business);
			}
			res.close();
			st.close();
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Review> getAllReviews(){
		String sql = "SELECT * FROM Reviews";
		List<Review> result = new ArrayList<Review>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Review review = new Review(res.getString("review_id"), 
						res.getString("business_id"),
						res.getString("user_id"),
						res.getDouble("stars"),
						res.getDate("review_date").toLocalDate(),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("review_text"));
				result.add(review);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<User> getAllUsers(){
		String sql = "SELECT * FROM Users";
		List<User> result = new ArrayList<User>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				User user = new User(res.getString("user_id"),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("name"),
						res.getDouble("average_stars"),
						res.getInt("review_count"));
				
				result.add(user);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> getAllCities(){
		String sql = "SELECT DISTINCT city "
				+ "FROM business";
		List<String> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				result.add(res.getString("city"));
			}
			res.close();
			st.close();
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Business> getVertici(String city, Integer year, Map<String, Business> idMap){
		String sql = "SELECT b.business_id AS bId "
				+ "FROM business b, reviews r "
				+ "WHERE b.business_id=r.business_id AND b.city = ? AND YEAR(r.review_date) = ? ";
		List<Business> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, city);
			st.setInt(2, year);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				String bId = res.getString("bId");
				Business b = idMap.get(bId);
				if(b != null) {
					//vuol dire che ho trovato corrispondenza
					result.add(b);
				}
			}
			res.close();
			st.close();
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<BusinessStars> getArchi(String city, Integer year, Map<String, Business> idMap){
		String sql = "SELECT b1.business_id AS b1, b2.business_id AS b2, AVG(r2.stars)-AVG(r1.stars) AS differenza "
				+ "FROM business b1, reviews r1, business b2, reviews r2 "
				+ "WHERE b1.business_id=r1.business_id AND b2.business_id=r2.business_id AND b1.city=? AND b1.city = b2.city AND YEAR(r1.review_date) = ? AND YEAR(r1.review_date) = YEAR(r2.review_date) AND "
				+ "b1.business_id<> b2.business_id "
				+ "GROUP BY b1.business_id, b2.business_id "
				+ "HAVING differenza>0";
		List<BusinessStars> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, city);
			st.setInt(2, year);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				String bId1 = res.getString("b1");
				String bId2 = res.getString("b2");
				Business b1 = idMap.get(bId1);
				Business b2 = idMap.get(bId2);
				if(b1 != null && b2!=null) {
					//vuol dire che ho trovato corrispondenza
					result.add(new BusinessStars(b1,b2,res.getDouble("differenza")));
				}
			}
			res.close();
			st.close();
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
