/**
 * An interface to calculate the similarity between two movies.
 */

package alg.cb.similarity;

import alg.cb.casebase.Movie;

public interface SimilarityMetric {
	/**
	 * computes the similarity between two movies
	 * @param m1 - the first movie
	 * @param m2 - the second movie
	 * @return the similarity
	 */
	public abstract double calculateSimilarity(Movie m1, Movie m2);
}
