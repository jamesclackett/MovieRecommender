package alg.cb;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import alg.cb.casebase.Casebase;
import alg.cb.casebase.Movie;
import alg.cb.reader.DatasetReader;
import alg.cb.recommender.Recommender;
import alg.cb.similarity.ConfidenceSimilarity;
import alg.cb.similarity.GenomeCosineSimilarity;
import alg.cb.similarity.GenreJaccardSimilarity;
import alg.cb.similarity.GenreOverlapSimilarity;
import alg.cb.similarity.PopularitySimilarity;
import alg.cb.similarity.RatingsCosineSimilarity;
import alg.cb.similarity.SimilarityMetric;

public class TestRecommender {
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
		SimilarityMetric metric = new GenreOverlapSimilarity();
		Recommender recommender = new Recommender(cb, metric);
		
		
		// Create an instance of the Scanner class
		Scanner input = new Scanner(System.in);

		// In each iteration of this loop, a random target movie is
		// selected and recommendations are displayed for this movie
		do {
			// Randomly choose a target movie and display this movie
			Movie target = getRandomMovie(cb);
			System.out.println("\nTarget movie:");
			System.out.println("- " + target.getTitle());

			// Display the top-k recommendations based on the target movie
			List<Movie> recs = recommender.getRecommendations(target);
			int k = 3; 

			if (recs.size() > 0) {
				System.out.println("\nRecommended movies:");
				for (int i = 0; i < recs.size() && i < k; i++)
					System.out.println("- " + recs.get(i).getTitle());
			} else
				System.out.println("\nNo recommended movies");

			// Prompt the user to enter 'y' or 'Y' to go again
			System.out.print("\nEnter 'q' to quit or any other character to go again> ");
			if (input.nextLine().toLowerCase().charAt(0) == 'q')
				break; // The user wishes to quit
		} while (true);

		// Display a final message
		System.out.println("\nGoodbye");

		// Close the Scanner
		input.close();
	}

	// Returns a randomly selected movie
	public static Movie getRandomMovie(Casebase cb) {
		// Get all movies
		Object[] movies = cb.getMovies().values().toArray();

		// Randomly select a movie from the the array
		int index = (int)(Math.random() * movies.length);
		return (Movie)movies[index];
	}
}
