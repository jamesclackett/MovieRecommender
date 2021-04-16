/**
 * A class to define a personalised recommender.
 * Each recommendation candidate is ranked by the maximum of its similarity 
 * to the target movies.
 */

package alg.cbp.recommender;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import alg.cb.casebase.Casebase;
import alg.cb.casebase.Movie;
import alg.cb.similarity.SimilarityMetric;
import alg.cb.util.ScoredObjectDsc;

public class MaxPRecommender extends PRecommender {
	/**
	 * constructor - creates a new object
	 * @param cb - the casebase
	 * @param metric - the similarity metric
	 */
	public MaxPRecommender(Casebase cb, SimilarityMetric metric) {
		super(cb, metric);
	}

	/**
	 * @param targetMovies - the target movies (e.g. the movies which are liked in a user's profile)
	 * @return the ranked list of recommended movies - each recommendation candidate is ranked by the 
	 * maximum of its similarity to the target movies
	 */
	public List<Movie> getRecommendations(Set<Movie> targetMovies) {
		// Store all similarities in descending order in a sorted set
		SortedSet<ScoredObjectDsc> ss = new TreeSet<>(); 

		// Get all movies - each movie is a possible recommendation candidate
		Object[] movies = getCasebase().getMovies().values().toArray();

		// Get the similarity between the target movies and each candidate movie
		for (int i = 0; i < movies.length; i++) {
			Movie candidate = (Movie)movies[i];

			// Exclude the current candidate movie if it is the same as one of the target movies
			if (!targetMovies.contains(candidate)) {
				double sim = calculateMaxSimilarity(targetMovies, candidate);

				// If sim is greater than zero, add the current candidate to the set
				if (sim > 0)
					ss.add(new ScoredObjectDsc(sim, candidate)); 
			}
		}

		// Sort the candidate movies by score (in descending order) and return as recommendations 
		List<Movie> recs = new ArrayList<>();

		for (Iterator<ScoredObjectDsc> it = ss.iterator(); it.hasNext(); ) {
			ScoredObjectDsc st = it.next();
			recs.add((Movie)st.getObject());
		}

		return recs;
	}
	
	/**
	 * @param targetMovies - the target movies (e.g. the movies which are liked in a user's profile)
	 * @param candidate - the candidate movie 
	 * @return the maximum similarity between the target movies and the candidate
	 */
	private double calculateMaxSimilarity(Set<Movie> targetMovies, Movie candidate) {
		double max = 0;
		
		for (Movie target: targetMovies) {
			double sim = getSimilarity(target, candidate);
			if (sim > max)
				max = sim;
		}
		
		return max;
	}
}
