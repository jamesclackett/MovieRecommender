/**
 * A class to compute the similarity between two movies. Similarity is given
 * by the cosine calculated over the movies' ratings vectors.
 */

package alg.cb.similarity;

import java.util.Map;

import alg.cb.casebase.Movie;

public class RatingsCosineSimilarity implements SimilarityMetric {	
	/**
	 * constructor - creates a new object
	 */
	public RatingsCosineSimilarity() {
	}

	/**
	 * computes the similarity between two movies
	 * @param m1 - the first movie
	 * @param m2 - the second movie
	 * @return the similarity
	 */
	@Override
	public double calculateSimilarity(Movie m1, Movie m2) {		
		// Get the ratings for each movie
		Map<Integer,Double> r1 = m1.getRatings();
		Map<Integer,Double> r2 = m2.getRatings();
		
		// Return zero if either movie has no ratings
		if (r1 == null || r2 == null) 
			return 0;

		// Calculate the cosine over the ratings
		double product = 0;
		for(int id: r1.keySet()) 
			if (r2.containsKey(id))
				product += r1.get(id) * r2.get(id);
	
		double length1 = 0;
		for (int id: r1.keySet())
			length1 += Math.pow(r1.get(id), 2);
		length1 = Math.sqrt(length1);
		
		double length2 = 0;
		for (int id: r2.keySet())
			length2 += Math.pow(r2.get(id), 2);
		length2 = Math.sqrt(length2);
		
		// Return zero if division by zero occurs
		return (length1 > 0 && length2 > 0) ? product / (length1 * length2) : 0;
	}
}
