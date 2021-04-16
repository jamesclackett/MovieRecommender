/**
 * A class to represent a movie.
 */

package alg.cb.casebase;

import java.util.Map;
import java.util.Set;

public class Movie {
	private int id; // the movie id
	private String title; // the movie title
	private int year; // the movie year of release
	private Set<String> genres; // the movie genres
	private Map<Integer,Double> genomeScores; // the movie genome scores
	private Map<Integer,Double> ratings; // the movie ratings
		
	/**
	 * constructor - creates a new Movie object
	 * @param id - the movie id
	 * @param title - the movie title
	 * @param year - the movie year of release
	 * @param genres - the movie genres
	 * @param genomeScores - the movie genome scores
	 * @param ratings - the movie ratings
	 */
	public Movie(int id, String title, int year, Set<String> genres, Map<Integer,Double> genomeScores, Map<Integer,Double> ratings) {
		this.id = id;
		this.title = title;
		this.year = year;
		this.genres = genres;
		this.genomeScores = genomeScores;
		this.ratings = ratings;
	}
	
	/**
	 * @return the movie id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @return the movie title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * @return the movie year of release
	 */
	public int getYear() {
		return year;
	}	
	
	/**
	 * @return the movie genres
	 */
	public Set<String> getGenres() {
		return genres;
	}
	
	/**
	 * @return the movie genome scores
	 */
	public Map<Integer,Double> getGenomeScores() {
		return genomeScores;
	}
	
	/**
	 * @return the movie ratings
	 */
	public Map<Integer,Double> getRatings() {
		return ratings;
	}
	
	/**
	 * @return the mean movie rating
	 */
	public double getMeanRating() {
		double sum = 0;
		
		Set<Integer> userIds = ratings.keySet();
		for (int id: userIds)
			sum += ratings.get(id);
		
		return (userIds.size() > 0) ? sum / userIds.size() : 0;
	}
	
	
	/**
	 * @return the hash code value for this Movie object
	 */
	@Override
	public int hashCode() {
		// Since each Movie object has a unique id, id is a suitable 
		// hash code value to return
		return id; 
	}

	/**
	 * Indicates whether some other object is "equal to" this one. Here, 
	 * Movie objects are compared based on the value of the data field id.
	 * @param obj - the object with which to compare
	 * return true if this object is equal to obj and false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Movie))
			return false;
		Movie other = (Movie) obj;
		if (id != other.id)
			return false;
		return true;
	}

	/**
	 * @return a string representation of the Movie object
	 */
	@Override
	public String toString() {
		return id + ", " + title + ", " + year + ", " + genres.toString();
	}
}
