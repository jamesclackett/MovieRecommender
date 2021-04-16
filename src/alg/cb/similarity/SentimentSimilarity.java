/**
 * A class to compute the similarity between two movies. Overall similarity is 
 * given by a linear combination of similarity (calculated by the metric
 * passed to the constructor) and sentiment (given by the mean rating of m2).
 */

package alg.cb.similarity;

import alg.cb.casebase.Movie;

public class SentimentSimilarity implements SimilarityMetric {	
	private SimilarityMetric metric; // the metric used to calculate similarity
	private double alpha; // a value in the interval [0, 1] which controls the
						  // relative influence of similarity and sentiment
	private double maxRating; // the maximum rating on the scale
	
	/**
	 * constructor - creates a new object
	 * @param metric - the metric used to calculate the similarity between movies
	 * @param alpha - a value in the interval [0, 1] which controls the relative influence of similarity and sentiment
	 * @param maxRating - the maximum rating on the scale
	 */
	public SentimentSimilarity(SimilarityMetric metric, double alpha, double maxRating) {
		this.metric = metric;
		this.alpha = alpha;
		this.maxRating = maxRating;
	}

	/**
	 * computes the similarity between two movies
	 * @param m1 - the first movie
	 * @param m2 - the second movie
	 * @return the similarity
	 */
	@Override
	public double calculateSimilarity(Movie m1, Movie m2) {	
		// Calculate the similarity according to the specified metric
		double sim = metric.calculateSimilarity(m1, m2);
		
		// Get the sentiment of movie m2 which is given by the mean rating of m2,
		// normalised to [0, 1]
		double sent = m2.getMeanRating() / maxRating;
		
		// Return the overall similarity. The parameter alpha controls the relative 
		// influence of similarity and sentiment on overall similarity. Return zero 
		// if either similarity or sentiment is zero.
		return (sim > 0 && sent > 0) ? alpha * sim + (1 - alpha) * sent : 0;
	}
}
