/**
 * A class to read in and store movie data.
 */

package alg.cb.reader;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import alg.cb.casebase.Casebase;
import alg.cb.casebase.Movie;
import alg.cb.util.Matrix;


public class DatasetReader {
	private Casebase cb; // Stores Movie objects
	private Matrix trainRatings;
	private Matrix testRatings;

	/** 
	 * constructor - creates a new DatasetReader object
	 * @param movieFile - the path and filename of the file containing movie metadata
	 * @param genomeScoresFile - the path and filename of the file containing the movie genome scores
	 * @param ratingsFile - the path and filename of the file containing the user-item ratings
	 */
	public DatasetReader(String movieFile, String genomeScoresFile, String ratingsFile) {
		readCasebase(movieFile, genomeScoresFile, ratingsFile);
	}
	
	public DatasetReader(String movieFile, String genomeScoresFile, String trainRatingsFile, String testRatingsFile ) {
		readCasebase(movieFile, genomeScoresFile, trainRatingsFile);
		trainRatings = readUserRatings(trainRatingsFile);
		testRatings = readUserRatings(testRatingsFile);
	}
	
	private Matrix readUserRatings(String ratingsFile) {
		Matrix matrix = new Matrix(); 
		File file = new File(ratingsFile);

		Scanner input = null;
		
		try {
			// Create a Scanner for the file
			input = new Scanner(file);

			while (input.hasNext()) { // Returns true if the scanner has more data to be read
				String line = input.nextLine();

				StringTokenizer st = new StringTokenizer(line, ",");
				if(st.countTokens() != 3) {
					System.out.println("Error reading from file \"" + ratingsFile + "\"");
					System.exit(1);
				}

				int userId = Integer.valueOf(st.nextToken());
				int movieId = Integer.valueOf(st.nextToken());
				double rating = Double.valueOf(st.nextToken());

				// Add the user id and rating for the current movie id to the matrix
				matrix.addValue(userId, movieId, rating);
			}
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			// Close the Scanner
			if (input != null)
				input.close();
		}
		
		return matrix;
	}
	
	public Matrix getTrainRatings() {
		return trainRatings;
	}
	
	public Matrix getTestRatings() {
		return testRatings;
	}

	/**
	 * @return the casebase
	 */
	public Casebase getCasebase() {
		return cb;
	}

	/** 
	 * Creates the casebase
	 * @param movieFile - the path and filename of the file containing movie metadata
	 * @param genomeScoresFile - the path and filename of the file containing the movie genome scores
	 * @param ratingsFile - the path and filename of the file containing the user-item ratings
	 */
	private void readCasebase(String movieFile, String genomeScoresFile, String ratingsFile) {
		// Read the genome scores and the ratings for each movie
		Matrix genomeScores = readGenomeScores(genomeScoresFile);
		Matrix movieRatings = readMovieRatings(ratingsFile);
		
		// Initialise the Casebase data field
		cb = new Casebase();

		// Create a File object
		File file = new File(movieFile);

		// Declare a Scanner reference variable
		Scanner input = null;

		try {
			// Create a Scanner for the file
			input = new Scanner(file);

			while (input.hasNext()) { // Returns true if the scanner has more data to be read
				String line = input.nextLine();

				// Parse the movie information
				int firstIndex = line.indexOf(",");
				int lastIndex = line.lastIndexOf(",");

				int movieId = Integer.valueOf(line.substring(0, firstIndex));

				// Parse the title and year
				String titleStr = line.substring(firstIndex + 1, lastIndex);
				if (titleStr.charAt(0) == '\"') // If present, remove quotation marks
					titleStr = titleStr.substring(1, titleStr.length() - 1);
				String title = titleStr.substring(0, titleStr.lastIndexOf('(')).trim(); // Remove the year

				int year = Integer.valueOf(titleStr.substring(titleStr.lastIndexOf('(') + 1, titleStr.lastIndexOf(')')));

				// Parse the genres
				String genreStr = line.substring(lastIndex + 1);
				StringTokenizer st = new StringTokenizer(genreStr, "|");
				int ntokens = st.countTokens();
				if(ntokens < 1) {
					System.out.println("Error reading from file \"" + movieFile + "\"");
					System.exit(1);
				}

				Set<String> genres = new HashSet<String>();
				for (int i = 0; i < ntokens; i++) {
					String genre = st.nextToken().toLowerCase().trim();
					if(!genre.equals("imax")) // Exclude the generic genre imax
						genres.add(genre);
				}
				
				// Create a new Movie object and add it to the casebase
				Movie movie = new Movie(movieId, title, year, genres, genomeScores.getRow(movieId), movieRatings.getRow(movieId));
				cb.addMovie(movieId, movie);
			}
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			// Close the Scanner
			if (input != null)
				input.close();
		}
	}

	/**  
	 * Read the genome scores for each movie
	 * @param genomeScoresFile - the path and filename of the file containing the movie genome scores
	 * @return a Matrix object that stores the genome scores for each movie
	 */
	private Matrix readGenomeScores(String genomeScoresFile) {
		// Create a Matrix object to store the genome scores for each movie
		Matrix matrix = new Matrix(); 

		// Create a File object
		File file = new File(genomeScoresFile);

		// Declare a Scanner reference variable
		Scanner input = null;

		try {
			// Create a Scanner for the file
			input = new Scanner(file);

			while (input.hasNext()) { // Returns true if the scanner has more data to be read
				String line = input.nextLine();

				// Parse the current line
				StringTokenizer st = new StringTokenizer(line, ",");
				int ntokens = st.countTokens();
				if(ntokens != 3) {
					System.out.println("Error reading from file \"" + genomeScoresFile + "\"");
					System.exit(1);
				}

				int movieId = Integer.valueOf(st.nextToken());
				int tagId = Integer.valueOf(st.nextToken());
				double score = Double.valueOf(st.nextToken());

				// Add the tag id and score for the current movie id to the matrix
				matrix.addValue(movieId, tagId, score);
			}
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			// Close the Scanner
			if (input != null)
				input.close();
		}
		
		return matrix;
	}

	/** 
	 * Read the ratings for each movie
	 * @param ratingsFile - the path and filename of the file containing the user-item ratings
	 * @return a Matrix object that stores the ratings for each movie
	 */
	private Matrix readMovieRatings(String ratingsFile) {
		// Create a Matrix object to store the ratings for each movie
		Matrix matrix = new Matrix(); 

		// Create a File object
		File file = new File(ratingsFile);

		// Declare a Scanner reference variable
		Scanner input = null;

		try {
			// Create a Scanner for the file
			input = new Scanner(file);

			while (input.hasNext()) { // Returns true if the scanner has more data to be read
				String line = input.nextLine();

				StringTokenizer st = new StringTokenizer(line, ",");
				if(st.countTokens() != 3) {
					System.out.println("Error reading from file \"" + ratingsFile + "\"");
					System.exit(1);
				}

				int userId = Integer.valueOf(st.nextToken());
				int movieId = Integer.valueOf(st.nextToken());
				double rating = Double.valueOf(st.nextToken());

				// Add the user id and rating for the current movie id to the matrix
				matrix.addValue(movieId, userId, rating);
			}
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			// Close the Scanner
			if (input != null)
				input.close();
		}
		
		return matrix;
	}
}
