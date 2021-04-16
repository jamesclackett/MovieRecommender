/**
 * This class stores movie objects.
 */

package alg.cb.casebase;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import alg.cb.casebase.Movie;

public class Casebase {
	// A hashmap to store movies; each key is a movie id 
	// and each value is a Movie object
	private Map<Integer,Movie> cb; 
	
	/**
	 * constructor - creates a new Casebase object
	 */
	public Casebase() {
		cb = new HashMap<>();
	}
	
	/**
	 * adds a movie to the casebase
	 * @param id - the id of the movie
	 * @param m - the Movie object
	 */
	public void addMovie(int id, Movie m) {
		cb.put(id, m);
	}
	
	/**
	 * @param id - the id of the movie 
	 * @return the Movie object corresponding to the specified id
	 */
	public Movie getMovie(int id) {
		return cb.get(id);
	}
	
	/**
	 * @return all movies in the casebase
	 */
	public Map<Integer,Movie> getMovies() {
		return cb;
	}
	
	/**
	 * @return the ids of all movies in the casebase
	 */
	public Set<Integer> getMovieIds() {
		return cb.keySet();
	}
	
	/**
	 * @return the number of movies in the casebase
	 */
	public int getNumberMovies() {
		return cb.size();
	}
}
