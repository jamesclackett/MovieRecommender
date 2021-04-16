/**
 * A class to define a non-personalised, content-based recommender.
 * Recommendation candidates are ranked based on similarity to the target movie.
 */

package alg.cb.recommender;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import alg.cb.casebase.Casebase;
import alg.cb.casebase.Movie;
import alg.cb.similarity.ConfidenceSimilarity;
import alg.cb.similarity.PopularitySimilarity;
import alg.cb.similarity.SentimentSimilarity;
import alg.cb.similarity.SimilarityMetric;
import alg.cb.util.Matrix;
import alg.cb.util.ScoredObjectDsc;

public class Recommender {
	private Casebase cb; // the casebase
	private Matrix similarities; // a Matrix object to store the pairwise similarities between all movies

	/**
	 * constructor - creates a new object
	 * @param cb - the casebase
	 * @param metric - the similarity metric
	 */
	public Recommender(Casebase cb, SimilarityMetric metric) {
		this.cb = cb;

		// Calculate the pairwise similarities between all movies
		similarities = new Matrix();

		// Get all movies
		Object[] movies = cb.getMovies().values().toArray();

		// Calculate the pairwise similarities between all movies
		for (int i = 0; i < movies.length; i++)
			for (int j = i + 1; j < movies.length; j++) {
				Movie m1 = (Movie)movies[i];
				Movie m2 = (Movie)movies[j];

				if (metric instanceof ConfidenceSimilarity || 
						metric instanceof PopularitySimilarity ||
						metric instanceof SentimentSimilarity) { // similarity is not symmetric
					double sim = metric.calculateSimilarity(m1, m2);
					if(sim > 0) similarities.addValue(m1.getId(), m2.getId(), sim);

					sim = metric.calculateSimilarity(m2, m1);
					if (sim > 0) similarities.addValue(m2.getId(), m1.getId(), sim);	
				} else { // similarity is symmetric
					double sim = metric.calculateSimilarity(m1, m2);
					if(sim > 0) {
						similarities.addValue(m1.getId(), m2.getId(), sim);
						similarities.addValue(m2.getId(), m1.getId(), sim);		
					} 
				}
			}
	}

	/**
	 * @return the casebase
	 */
	public Casebase getCasebase() {
		return cb;
	}

	/**
	 * @param target - the target movie
	 * @return the ranked list of recommended movies
	 */
	public List<Movie> getRecommendations(Movie target) {
		// Store all similarities in descending order in a sorted set
		SortedSet<ScoredObjectDsc> ss = new TreeSet<>(); 

		// Get all movies - each movie is a possible recommendation candidate
		Object[] movies = cb.getMovies().values().toArray();

		// Get the similarity between the target movie and each candidate movie
		for (int i = 0; i < movies.length; i++) {
			Movie candidate = (Movie)movies[i];

			// Exclude the current candidate movie if it is the same as the target movie
			if (target.getId() != candidate.getId()) { 
				double sim = similarities.getValue(target.getId(), candidate.getId());

				// If sim is greater than zero, add the current candidate to the set
				if(sim > 0)
					ss.add(new ScoredObjectDsc(sim, candidate)); 
			}
		}

		// Sort the candidate movies by score (in descending order) and return as recommendations 
		List<Movie> recs = new ArrayList<>();

		for(Iterator<ScoredObjectDsc> it = ss.iterator(); it.hasNext(); ) {
			ScoredObjectDsc st = it.next();
			recs.add((Movie)st.getObject());
		}

		return recs;
	}
}
