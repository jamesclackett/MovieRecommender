package alg.cbp;

import java.io.File;

import alg.cb.casebase.Casebase;
import alg.cb.reader.DatasetReader;
import alg.cb.similarity.ConfidenceSimilarity;
import alg.cb.similarity.GenomeCosineSimilarity;
import alg.cb.similarity.GenreJaccardSimilarity;
import alg.cb.similarity.SimilarityMetric;
import alg.cb.util.Matrix;
import alg.cbp.evaluator.PEvaluator;
import alg.cbp.recommender.MaxPRecommender;
import alg.cbp.recommender.MeanPRecommender;
import alg.cbp.recommender.PRecommender;

public class Test {
	public static void main(String[] args) {	
		// Set the paths and filenames and read in the data
		String movieFile = "dataset" + File.separator + "movies-sample.txt";
		String genomeScoresFile = "dataset" + File.separator + "genome-scores-sample.txt";
		String trainRatingsFile = "dataset" + File.separator + "ratings.txt";
		String testRatingsFile = "dataset" + File.separator + "test.txt";
		DatasetReader reader = new DatasetReader(movieFile, genomeScoresFile, trainRatingsFile, testRatingsFile);
		Casebase cb = reader.getCasebase();
		Matrix trainRatings = reader.getTrainRatings(); // a matrix to store the training ratings for each user
		Matrix testRatings = reader.getTestRatings(); // a matrix to store the test ratings for each user
		double threshold = 4; // movies with ratings >= threshold are considered liked by users

		// Configure the recommender
		SimilarityMetric s = new GenreJaccardSimilarity();
		PRecommender r = new MeanPRecommender(cb, s);

		// Evaluate the recommender
		String label = "Mean Rec. Genre Jaccard";
		evaluate(r, threshold, trainRatings, testRatings, label);	
	}
	
	// Performs an evaluation of a recommender 
	public static void evaluate(PRecommender recommender, double threshold, Matrix trainRatings, Matrix testRatings, String label) {
		// Create a PEvaluator object using the current recommender
		PEvaluator eval = new PEvaluator(recommender, threshold, trainRatings, testRatings);

		System.out.println(label);
		System.out.println("k,precision,recall,f1,coverage");
		
		// Evaluate performance at different values of k
		for (int k = 5; k <= 50; k+=5) {
			double[] values = eval.getPRF1(k);
			double coverage = eval.getCoverage();
			System.out.println(k + "," + values[0] + "," + values[1] + "," + values[2] + "," + coverage);
		}
	}
}	

