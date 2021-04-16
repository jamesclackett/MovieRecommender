package alg.cb;

import java.io.File;

import alg.cb.casebase.Casebase;
import alg.cb.evaluator.Evaluator;
import alg.cb.reader.DatasetReader;
import alg.cb.recommender.Recommender;
import alg.cb.similarity.ConfidenceSimilarity;
import alg.cb.similarity.GenomeCosineSimilarity;
import alg.cb.similarity.GenreJaccardSimilarity;
import alg.cb.similarity.GenreOverlapSimilarity;
import alg.cb.similarity.PopularitySimilarity;
import alg.cb.similarity.RatingsCosineSimilarity;
import alg.cb.similarity.SentimentSimilarity;
import alg.cb.similarity.SimilarityMetric;

public class Test_A6_Expt {
	public static void main(String[] args) {	
		// Set the paths and filenames and read in the data
		String movieFile = "dataset" + File.separator + "movies-sample.txt";
		String genomeScoresFile = "dataset" + File.separator + "genome-scores-sample.txt";
		String ratingsFile = "dataset" + File.separator + "ratings.txt";
		DatasetReader reader = new DatasetReader(movieFile, genomeScoresFile, ratingsFile);
		Casebase cb = reader.getCasebase();
		int k = 10; // the number of recommendations to be made for each target movie
		double threshold = 4; // movies with ratings >= threshold are considered liked by users
		double maxRating = 5; // the maximum rating on the scale
		
		System.out.println("k,label,relevance,coverage,rec. coverage,item space coverage,rec. popularity,rec. similarity");
		
		// Evaluate the recommender using different similarity metrics
		// Create an array of similarity metrics
		SimilarityMetric[] metrics = {
				new PopularitySimilarity(),
				new GenreOverlapSimilarity(),
				new GenreJaccardSimilarity(),
				new GenomeCosineSimilarity(),
				new RatingsCosineSimilarity(),
				new ConfidenceSimilarity(threshold)
		};

		String[] labels = {"Popularity", "Genre Overlap", "Genre Jaccard", "Genome Cosine", "Ratings Cosine", "Confidence"};
		for (int i = 0; i < metrics.length; i++) {
			// Create a Recommender object using the current similarity metric (metrics[i])
			Recommender r = new Recommender(cb, metrics[i]);			
			evaluate(r, k, labels[i]);
		}
		
		System.out.println("\n====================\n");
		
		// Evaluate SentimentSimilarity using various values of alpha
		System.out.println("k,label,relevance,coverage,rec. coverage,item space coverage,rec. popularity,rec. similarity");
		for (int i = 0; i <= 100; i+=10) {
			// Create a Recommender object using the SentimentSimilarity similarity metric
			double alpha = i / 100.0;
			SimilarityMetric m = new SentimentSimilarity(new GenreJaccardSimilarity(), alpha, maxRating);
			Recommender r = new Recommender(cb, m);			
			evaluate(r, k, "" + alpha);
		}
	}
	
	// Performs an evaluation of a recommender 
	public static void evaluate(Recommender recommender, int k, String label) {
		// Create an Evaluator object using the current recommender
		Evaluator eval = new Evaluator(recommender);
		
		// Display results 
		System.out.println(k + "," + label + "," + 
				eval.getRecommendationRelevance(k) + "," + 
				eval.getCoverage() + "," + 
				eval.getRecommendationCoverage(k) + "," +
				eval.getItemSpaceCoverage() + "," +
				eval.getRecommendationPopularity(k) + "," +
				eval.getRecommendationSimilarity(k));
	}	
}
