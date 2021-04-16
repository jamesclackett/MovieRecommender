/**
 * A class to compute the similarity between two movies. Similarity is given 
 * by the overlap calculated over the movies' genres. If there are no 
 * common genres between the movies, a similarity value of zero is returned.
 */

package alg.cb.similarity;

import java.util.Set;

import alg.cb.casebase.Movie;

public class GenreOverlapSimilarity implements SimilarityMetric {	
	/**
	 * constructor - creates a new object
	 */
	public GenreOverlapSimilarity() {
	}

	/**
	 * computes the similarity between two movies
	 * @param m1 - the first movie
	 * @param m2 - the second movie
	 * @return the similarity
	 */
	@Override
	public double calculateSimilarity(Movie m1, Movie m2) {		
		// Get the genres for each movie
		Set<String> s1 = m1.getGenres();
		Set<String> s2 = m2.getGenres();
		
		// Calulate the overlap over the genres
		int intersection = 0;
		
		for(String str: s1)
			if(s2.contains(str))
				intersection++;
		
		int min = Math.min(s1.size(), s2.size());	
		
		// Return zero if division by zero occurs
		return (min > 0) ? intersection * 1.0 / min : 0;
	}
}
