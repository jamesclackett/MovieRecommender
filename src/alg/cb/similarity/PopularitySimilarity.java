/**
 * A class to compute the similarity between two movies. Similarity is given
 * by the popularity of movie m2.
 */

package alg.cb.similarity;

import java.util.Map;

import alg.cb.casebase.Movie;

public class PopularitySimilarity implements SimilarityMetric {	
	/**
	 * constructor - creates a new object
	 */
	public PopularitySimilarity() {
	}

	/**
	 * computes the similarity between two movies
	 * @param m1 - the first movie
	 * @param m2 - the second movie
	 * @return the similarity
	 */
	@Override
	public double calculateSimilarity(Movie m1, Movie m2) {		
		// Get the ratings for movie m2
		Map<Integer,Double> r2 = m2.getRatings();

		// Return the popularity of movie m2 or zero if it has no ratings
		return (r2 != null) ? r2.size() : 0;		
	}
}
