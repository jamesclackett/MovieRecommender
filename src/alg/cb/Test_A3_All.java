package alg.cb;

import java.io.File;
import java.util.Set;

import alg.cb.casebase.Casebase;
import alg.cb.casebase.Movie;
import alg.cb.reader.DatasetReader;
import alg.cb.similarity.ConfidenceSimilarity;
import alg.cb.similarity.GenomeCosineSimilarity;
import alg.cb.similarity.GenreJaccardSimilarity;
import alg.cb.similarity.GenreOverlapSimilarity;
import alg.cb.similarity.PopularitySimilarity;
import alg.cb.similarity.RatingsCosineSimilarity;
import alg.cb.similarity.SentimentSimilarity;
import alg.cb.similarity.SimilarityMetric;
import alg.cb.util.Histogram;
import alg.cb.util.Matrix;
import alg.cb.util.Stats;

public class Test_A3_All {
	public static void main(String[] args) {	
		// Set the paths and filenames and read in the data
		String movieFile = "dataset" + File.separator + "movies-sample.txt";
		String genomeScoresFile = "dataset" + File.separator + "genome-scores-sample.txt";
		String ratingsFile = "dataset" + File.separator + "ratings.txt";
		DatasetReader reader = new DatasetReader(movieFile, genomeScoresFile, ratingsFile);
		Casebase cb = reader.getCasebase();
		double threshold = 4; // movies with ratings >= threshold are considered liked by users
		double maxRating = 5; // the maximum rating on the scale
		
		// Create an array of similarity metrics
		SimilarityMetric[] metrics = {
				new PopularitySimilarity(),
				new GenreOverlapSimilarity(),
				new GenreJaccardSimilarity(),
				new GenomeCosineSimilarity(),
				new RatingsCosineSimilarity(),
				new ConfidenceSimilarity(threshold),
				new SentimentSimilarity(new GenreJaccardSimilarity(), 0.5, maxRating)
		};

		// Create an array of matrices to store the pairwise similarities for each similarity metric
		Matrix[] similarities = new Matrix[metrics.length];
		for (int i = 0; i < metrics.length; i++) 
			similarities[i] = calculateSimilarities(metrics[i], cb);

		// Create a histogram of pairwise similarities for each similarity metric
		String[] labels = {"Popularity", "Genre Overlap", "Genre Jaccard", "Genome Cosine", "Ratings Cosine", "Confidence", "Sentiment"};
		for (int i = 0; i < similarities.length; i++) {	
			// Add all pairwise similarities to the histogram (including pairwise similarities 
			// equal to zero).
			Histogram h = new Histogram(0, 1, 40);
			Set<Integer> movieIds = cb.getMovieIds(); // Get all movie ids
			for (int row: movieIds) // Iterate over the rows of the matrix
				for (int col: movieIds) // Iterate over the columns of the matrix
					if (row != col) // Exclude the similarity between each movie and itself 
						h.addValue(similarities[i].getValue(row, col));

			// Display the histogram for the current similarity metric
			System.out.println(labels[i]);
			displayHistogram(h);
			System.out.println("\n====================\n");
		}

		// Calculate the Pearson correlation between the pairwise similarities given 
		// by each similarity metric
		double[][] correlations = new double[similarities.length][similarities.length];
		for (int i = 0; i < correlations.length; i++) 
			for (int j = 0; j < correlations.length; j++) 
				correlations[i][j] = Stats.getCorrelation(similarities[i], similarities[j], cb.getMovieIds(), cb.getMovieIds(), true);

		// Display the correlations
		for (int i = 0; i < correlations.length; i++) {
			for (int j = 0; j < correlations[i].length; j++)
				System.out.printf("%.6f ", correlations[i][j]);
			System.out.println();
		}	

		// Create a histogram of the number of genres per movie
		Histogram hist = new Histogram(0.5, 10.5, 10);
		for (int movieId: cb.getMovieIds())
			hist.addValue(cb.getMovie(movieId).getGenres().size());

		System.out.println("\n====================\n");
		displayHistogram(hist);
	}

	// Returns a matrix containing the pairwise similarities between all movies
	public static Matrix calculateSimilarities(SimilarityMetric metric, Casebase cb) {
		// Create a matrix to store the pairwise similarities between all movies
		Matrix matrix = new Matrix();

		// Get all movies
		Object[] movies = cb.getMovies().values().toArray();

		// Calculate the pairwise similarities between all movies
		for (int i = 0; i < movies.length; i++)
			for (int j = i + 1; j < movies.length; j++) {
				Movie m1 = (Movie)movies[i];
				Movie m2 = (Movie)movies[j];

				// Similarities calculated using overlap coefficient, Jaccard index,  
				// and cosine are symmetric - i.e. sim(m1, m2) = sim(m2, m1).
				// Similarities calculated using confidence (product association 
				// recommendation approach), popularity, and sentiment are not 
				// symmetric - i.e. sim(m1, m2) != sim(m2, m1).
				if (metric instanceof ConfidenceSimilarity ||
						metric instanceof PopularitySimilarity ||
						metric instanceof SentimentSimilarity) { // similarity is not symmetric
					double sim = metric.calculateSimilarity(m1, m2);
					if(sim > 0) matrix.addValue(m1.getId(), m2.getId(), sim);

					sim = metric.calculateSimilarity(m2, m1);
					if (sim > 0) matrix.addValue(m2.getId(), m1.getId(), sim);	
				} else { // similarity is symmetric
					double sim = metric.calculateSimilarity(m1, m2);
					if (sim > 0) { // Add non-zero similarities to the matrix
						matrix.addValue(m1.getId(), m2.getId(), sim);
						matrix.addValue(m2.getId(), m1.getId(), sim);		
					}					
				}
			}

		return matrix;
	}

	// Displays a histogram
	public static void displayHistogram(Histogram h) {
		System.out.println(h.toString());

		double[] binCentres = h.getBinCentres();
		int[] binCounts = h.getBinCounts();

		System.out.println("bin centre,count");
		for (int i = 0; i < binCentres.length; i++)
			System.out.println(binCentres[i] + "," + binCounts[i]);
	}
}
