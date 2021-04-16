/**
 * A class to evaluate a personalised recommender.
 */

package alg.cbp.evaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.jdi.Type;

import alg.cb.casebase.Casebase;
import alg.cb.casebase.Movie;
import alg.cb.util.Matrix;
import alg.cbp.recommender.PRecommender;

public class PEvaluator {
	private double threshold; // Movies with ratings >= threshold are considered liked by users
	private Map<Integer,List<Movie>> recommendations; // Stores the recommendations made for each user
	private Matrix testRatings;  // Stores the test ratings for each user

	/**
	 * constructor - creates a new Evaluator object
	 * @param recommender - the recommender
	 * @param threshold - movies with ratings >= threshold are considered liked by users
	 * @param trainRatings - a matrix which stores user training ratings
	 * @param testRatings - a matrix which stores user test ratings
	 */
	public PEvaluator(PRecommender recommender, double threshold, Matrix trainRatings, Matrix testRatings) {
		this.threshold = threshold;
		this.testRatings = testRatings;
		recommendations = new HashMap<>();

		// Get the recommendations for each user
		for (int userId: trainRatings.getRowIds()) {
			// Create a hash set to hold the target movies for the current user. The 
			// target movies are those movies in the training set which the user has 
			// liked (movies with ratings >= threshold are considered liked).
			// *** write this code here ***
			
			HashSet<Movie> targetMovies = new HashSet<>();
			Casebase cb = recommender.getCasebase();
			Set<Integer> moviesRated = trainRatings.getColIds(userId);
			
			for (int movieId: moviesRated) {
				if (trainRatings.getValue(userId, movieId) >= threshold) targetMovies.add(cb.getMovie(movieId));
			}
			
			// Get the recommendations for the current user based on the target movies
			// and add the recommendations to the map.
			// *** write this code here ***
		
			List<Movie> recs = recommender.getRecommendations(targetMovies);
			recommendations.put(userId, recs);
		}
	}

	/**
	 * @return the coverage which is given by the percentage of  
	 * users for which at least one recommendation can be made
	 */
	public double getCoverage() {
		// Implement this method
		// calculate how many users that 1+ recommendations can be made for then divide
		// by the num of total users to get percentage
		int counter = 0;
		
		for (int userId: recommendations.keySet()) {
			if (recommendations.get(userId).size() >= 1) {
				counter++;
			}
		}
		return (counter > 0) ? recommendations.keySet().size() / counter : 0;
	}

	/**
	 * @param k - the number of recommendations to consider
	 * @return the average precision, recall, and F1 over all users for which at least one
	 * recommendation can be made
	 */
	public double[] getPRF1(int k) {
		// Implement this method
		//calculate precision, recall and f1
		double precisionCounter = 0;
		double totalRecall = 0;
		double f1Counter = 0;
		
		for (int userId: recommendations.keySet()) {
			Set<Integer> r = new HashSet<>();
			Set<Integer> t = new HashSet<>();
			List<Movie> recs = recommendations.get(userId);
			if (recs.size() < 1) continue;
			for (int i = 0; i < k; i++) {
				r.add(recs.get(i).getId());
			}
			
			Set<Integer> moviesRated = testRatings.getColIds(userId);
			for (int movieId: moviesRated) {
				if (testRatings.getValue(userId, movieId) >= threshold) t.add(movieId);
			}
			
			Set<Integer> rIntersectT = new HashSet<>();
			
			if (r.size() < t.size()) {
				for (int i: r) {
					if (t.contains(i)) rIntersectT.add(i);
				}
			}
			else {
				for (int i: t) {
					if (r.contains(i)) rIntersectT.add(i);
				}
			}
			double precision;
			if (k > 0) {
				precision = rIntersectT.size() / (k * 1.0);
			}
			else {
				precision = 0;
			}
			
			double recall;
			if (t.size() > 0) {
				recall = rIntersectT.size() / (t.size() * 1.0);
			}
			else {
				recall = 0;
			}
			
			double f1;
			if (precision == 0 && recall == 0) f1 = 0;
			else {
				f1 = 2 * (precision*1.0) * (recall *1.0) / (precision + recall) *1.0;
			}
			
			precisionCounter += precision;
			totalRecall += recall;
			f1Counter += f1;
		}
		
		
		double[] values = new double[3];	
		values[0] = precisionCounter / recommendations.keySet().size();
		values[1] = totalRecall / recommendations.keySet().size();
		values[2] = f1Counter / recommendations.keySet().size();
		return values;
	}
}
