/**
 * A class to run a personalised recommender.
 */

package alg.cbp;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import alg.cb.casebase.Casebase;
import alg.cb.casebase.Movie;
import alg.cb.reader.DatasetReader;
import alg.cb.similarity.ConfidenceSimilarity;
import alg.cb.similarity.GenomeCosineSimilarity;
import alg.cb.similarity.GenreJaccardSimilarity;
import alg.cb.similarity.GenreOverlapSimilarity;
import alg.cb.similarity.SimilarityMetric;
import alg.cbp.recommender.MaxPRecommender;
import alg.cbp.recommender.MeanPRecommender;
import alg.cbp.recommender.PRecommender;

public class TestPRecommender {
	public static void main(String[] args) {	
		// Set the paths and filenames and read in the data
		String movieFile = "dataset" + File.separator + "movies-sample.txt";
		String genomeScoresFile = "dataset" + File.separator + "genome-scores-sample.txt";
		String ratingsFile = "dataset" + File.separator + "ratings.txt";
		DatasetReader reader = new DatasetReader(movieFile, genomeScoresFile, ratingsFile);
		Casebase cb = reader.getCasebase();
		double threshold = 4; // movies with ratings >= threshold are considered liked by users
		double maxRating = 5; // the maximum rating on the scale
		

		// Configure the content-based recommendation algorithm - set the similarity metric and recommender
		SimilarityMetric metric = new GenreJaccardSimilarity();
		PRecommender recommender = new MeanPRecommender(cb, metric);


		// Create an instance of the Scanner class
		Scanner input = new Scanner(System.in);

		// In each iteration of this loop, random target movies are
		// selected and recommendations based on these movies are displayed
		do {
			// Randomly choose t target movies and display these movies
			int t = 2;
			Set<Movie> targets = getRandomMovies(cb, t);
			System.out.println("\nTarget movies:");
			for (Movie m: targets)
				System.out.println("- " + m.getTitle());

			// Display the top-k recommendations based on the target movies
			List<Movie> recs = recommender.getRecommendations(targets);
			int k = 3; 

			if (recs.size() > 0) {
				System.out.println("\nRecommended movies:");
				for (int i = 0; i < recs.size() && i < k; i++)
					System.out.println("- " + recs.get(i).getTitle());
			} else
				System.out.println("\nNo recommended movies");

			// Prompt the user to enter 'q' or 'Q' to quit or to go again
			System.out.print("\nEnter 'q' to quit or any other character to go again> ");
			if (input.nextLine().toLowerCase().charAt(0) == 'q')
				break; // The user wishes to quit
		} while (true);

		// Display a final message
		System.out.println("\nGoodbye");

		// Close the Scanner
		input.close();
	}

	// Returns a randomly selected set of target movies
	public static Set<Movie> getRandomMovies(Casebase cb, int t) {
		// Get all movies
		Object[] movies = cb.getMovies().values().toArray();

		// Randomly select t *different* indices from the array and add the corresponding
		// movies to a hash set.
		Set<Movie> targetMovies = new HashSet<>();
		while (targetMovies.size() < t) {
			int index = (int)(Math.random() * movies.length);
			Movie m = (Movie)movies[index];
			targetMovies.add(m); // Note that duplicate elements are not added to a hash set
		}
			
		return targetMovies;
	}
}
