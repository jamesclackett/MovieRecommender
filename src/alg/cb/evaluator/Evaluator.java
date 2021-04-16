/**
 * A class to evaluate a non-personalised recommender.
 */

package alg.cb.evaluator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import alg.cb.casebase.Casebase;
import alg.cb.casebase.Movie;
import alg.cb.recommender.Recommender;
import alg.cb.util.Stats;


public class Evaluator {	
	private Casebase cb; // stores movie objects
	private Map<Integer,List<Movie>> recommendations; // stores the recommendations made for each movie id
	private int maxPopularity; // the number of times the most popular movie has been rated by users
	
	/**
	 * constructor - creates a new Evaluator object
	 * @param recommender - the recommender
	 */
	public Evaluator(Recommender recommender) {
		// Initialise the casebase
		cb = recommender.getCasebase();

		// Get the recommendations for each movie and store in the hash map
		recommendations = new HashMap<>();

		for (int movieId: cb.getMovieIds()) {
			Movie m = cb.getMovie(movieId);
			List<Movie> recs = recommender.getRecommendations(m);
			recommendations.put(movieId, recs);
		}

		// Get the maximum movie popularity 
		maxPopularity = 0;

		for (int movieId: cb.getMovieIds()) {
			Movie m = cb.getMovie(movieId);
			int popularity = m.getRatings().size();
			if (popularity > maxPopularity)
				maxPopularity = popularity;
		}
	}

	/**
	 * @return the coverage which is given by the percentage of  
	 * target movies for which at least one recommendation can be made
	 */
	public double getCoverage() {
		// Stores the number of target movies for which at least one recommendation can be made
		int numMovies = 0;

		// Iterate over each movie - if the number of recommendations made for that movie
		// is greater than zero increment count by one
		for (int movieId: recommendations.keySet())
			if (recommendations.get(movieId).size() > 0)
				numMovies++;

		// Return the coverage
		return (recommendations.size() > 0) ? numMovies * 1.0 / recommendations.size() : 0;
	}

	/**
	 * @param k - the number of recommendations to be considered
	 * @return the percentage of movies in the dataset which appear at 
	 * least once in the top-k recommendations made over all target movies
	 */
	public double getRecommendationCoverage(int k) {
		// Stores the ids of all movies which appear at least once in the top-k 
		// recommendations made over all target movies 
		Set<Integer> allRecs = new HashSet<>();

		// Iterate over each movie, get the recommendations made for that movie and add
		// these recommendations to the hashset
		for (int movieId: recommendations.keySet()) {
			List<Movie> recs = recommendations.get(movieId);
			for (int i = 0; i < recs.size() && i < k; i++)
				allRecs.add(recs.get(i).getId());
		}

		// Return the recommendation coverage
		return (recommendations.size() > 0) ? allRecs.size() * 1.0 / recommendations.size() : 0;
	}

	/**
	 * For a given target movie, the percentage of movies in the system which are 
	 * capable of being recommended (i.e. those movies which have a similarity 
	 * greater than zero to the target movie) is calculated.
	 * 
	 * @return the mean over all target movies for which at least one
	 * recommendation can be made.
	 */
	public double getItemSpaceCoverage() {
		double meanCoverage = 0;
		int numMovies = 0;

		// Iterate over each movie and get the recommendations made for that movie
		for (int movieId: recommendations.keySet()) {
			List<Movie> recs = recommendations.get(movieId);
			if (recs.size() > 0) {
				meanCoverage += (recommendations.size() - 1 > 0) ? recs.size() * 1.0 / (recommendations.size() - 1) : 0;
				numMovies++;
			}
		}

		// Return the mean item space coverage
		return (numMovies > 0) ? meanCoverage / numMovies : 0;
	}

	/**
	 * The relevance of a recommended movie is given by the mean of the 
	 * ratings the movie received in the dataset.
	 * 
	 * For a given target movie, the relevance of the top-k recommendations 
	 * made is given by the mean of the relevance of the recommended movies.
	 * 
	 * @param k - the number of recommendations to be considered
	 * @return the mean over all target movies for which at least one 
	 * recommendation can be made.
	 */
	public double getRecommendationRelevance(int k) {
		double meanRelevance = 0;
		int numMovies = 0;

		// Iterate over each movie and get the recommendations made for that movie
		for (int movieId: recommendations.keySet()) {
			double relevance = 0;
			int count = 0;

			// Get the recommendations for the target movie
			List<Movie> recs = recommendations.get(movieId);
			for (int i = 0; i < recs.size() && i < k; i++) {
				Movie movie = recs.get(i);
				relevance += movie.getMeanRating();
				count++;
			}

			// Update the mean relevance
			if (count > 0) {
				meanRelevance += relevance / count;
				numMovies++;
			}
		}

		// Return the mean relevance
		return (numMovies > 0) ? meanRelevance / numMovies : 0;
	}

	/**
	 * The popularity of a recommended movie is given by the number of times 
	 * the movie has been rated by users divided by maxPopularity.
	 * 
	 * For a given target movie, the popularity of the top-k recommendations 
	 * made is given by the mean of the popularity of the recommended movies. 
	 * 
	 * @param k - the number of recommendations to be considered
	 * @return the mean over all target movies for which at least one 
	 * recommendation can be made.
	 */
	public double getRecommendationPopularity(int k) {
		double meanPopularity = 0;
		int numMovies = 0;

		// Iterate over each movie and get the recommendations made for that movie
		for (int movieId: recommendations.keySet()) {
			double popularity = 0;
			int count = 0;

			// Get the recommendations for the target movie
			List<Movie> recs = recommendations.get(movieId);
			for (int i = 0; i < recs.size() && i < k; i++) {
				Movie movie = recs.get(i);
				popularity += movie.getRatings().size() * 1.0 / maxPopularity;
				count++;
			}

			// Update the mean popularity
			if (count > 0) {
				meanPopularity += popularity / count;
				numMovies++;
			}
		}

		// Return the mean popularity
		return (numMovies > 0) ? meanPopularity / numMovies : 0;
	}
	
	/**
	 * The similarity of a recommended movie is given by the Pearson's 
	 * correlation coefficient, calculated over the genome tag relevance 
	 * scores vectors, between it and the target movie.
	 * 
	 * For a given target movie, the similarity of the top-k recommendations 
	 * made is given by the mean of the similarity of the recommended movies. 
	 * 
	 * @param k - the number of recommendations to be considered
	 * @return the mean over all target movies for which at least one 
	 * recommendation can be made.
	 */
	public double getRecommendationSimilarity(int k) {
		double meanSimilarity = 0;
		int numMovies = 0;

		// Iterate over each movie and get the recommendations made for that movie
		for (int movieId: recommendations.keySet()) {
			double similarity = 0;
			int count = 0;

			// Get the recommendations for the target movie
			List<Movie> recs = recommendations.get(movieId);
			for (int i = 0; i < recs.size() && i < k; i++) {
				Movie movie = recs.get(i);
				
				// Calculate the similarity between the target movie and the current recommendation.
				// Here, the similarity between movies is given by Pearson's correlation coefficient
				// calculated over the genome tag relevance scores
				similarity += Stats.getCorrelation(cb.getMovie(movieId).getGenomeScores(), movie.getGenomeScores());
				count++;
			}

			// Update the mean similarity
			if (count > 0) {
				meanSimilarity += similarity / count;
				numMovies++;
			}
		}

		// Return the mean similarity
		return (numMovies > 0) ? meanSimilarity / numMovies : 0;
	}
}
