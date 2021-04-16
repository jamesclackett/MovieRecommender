package alg.cb.util;

import java.util.Map;
import java.util.Set;

public class Stats {

	/**
	 * constructor is private to prevent instantiation of the class
	 */
	private Stats() {}

	/** 
	 * @param x - an array of double values 
	 * @return the mean and standard deviation of the values in x
	 */
	public static double[] getMeanStdev(double[] x) {		
		// Calculate the mean
		double sum = 0;
		for (int i = 0; i < x.length; i++) 
			sum += x[i];
		double mean = (x.length > 0) ? sum / x.length : 0;

		// Calculate the standard deviation
		double sqSum = 0;
		for (int i = 0; i < x.length; i++) 
			sqSum += Math.pow(x[i] - mean, 2);
		double stdev = (x.length - 1 > 0) ? Math.sqrt( sqSum / (x.length - 1) ) : 0;

		// Return the mean and standard deviation
		double[] vals = {mean, stdev};
		return vals;
	}


	/** 
	 * @param x - an array of double values 
	 * @param y - an array of double values 
	 * @return the correlation coefficient between the values in x and y
	 */
	public static double getCorrelation(double x[], double y[]) throws IllegalArgumentException { 
		// Throw an exception if parameters are invalid
		if (x.length < 2 || x.length != y.length)
			throw new IllegalArgumentException("Arrays must contain at least 2 elements and must be equal in size.");

		// Declare variables
		int n = x.length;
		double sum_x = 0, sum_y = 0, sum_xy = 0; 
		double sum_xx = 0, sum_yy = 0; 

		// Iterate over the elements in each array
		for (int i = 0; i < n; i++) { 
			sum_x += x[i]; 
			sum_y += y[i]; 
			sum_xy += x[i] * y[i]; 
			sum_xx += x[i] * x[i]; 
			sum_yy += y[i] * y[i]; 
		} 

		// Calculating and return the correlation coefficient 
		double num = n * sum_xy - sum_x * sum_y;
		double denom = Math.sqrt( (n * sum_xx - sum_x * sum_x) * (n * sum_yy - sum_y * sum_y) ); 
		return (denom > 0) ? num / denom : 0; 
	} 


	/** 
	 * @param m1 - a Matrix object
	 * @param m2 - a Matrix object
	 * @param rowIds - the set of row ids
	 * @param colIds - the set of col ids
	 * @param flag - if true, exclude matrix elements where row id = col id
	 * @return the correlation coefficient between the values in m1 and m2
	 */
	public static double getCorrelation(Matrix m1, Matrix m2, Set<Integer> rowIds, Set<Integer> colIds, boolean flag) {
		// Declare variables
		int n = 0;
		double sum_x = 0, sum_y = 0, sum_xy = 0; 
		double sum_xx = 0, sum_yy = 0; 

		// Iterate over the elements in each matrix
		for (int row: rowIds) 
			for (int col: colIds) 
				if (flag && row != col || !flag) {
					double x = m1.getValue(row, col);
					double y = m2.getValue(row, col);

					sum_x += x; 
					sum_y += y; 
					sum_xy += x * y; 
					sum_xx += x * x; 
					sum_yy += y * y; 
					n++;
				} 

		// Calculating and return the correlation coefficient 
		double num = n * sum_xy - sum_x * sum_y;
		double denom = Math.sqrt( (n * sum_xx - sum_x * sum_x) * (n * sum_yy - sum_y * sum_y) ); 
		return (denom > 0) ? num / denom : 0; 
	} 


	/** 
	 * @param m1 - a HashMap object
	 * @param m2 - a HashMap object
	 * @return the correlation coefficient between the key/value pairs in m1 and m2
	 */
	public static double getCorrelation(Map<Integer,Double> m1, Map<Integer,Double> m2) throws IllegalArgumentException {
		// Throw an exception if parameters are invalid
		if (m1.size() < 2 || m1.size() != m2.size())
			throw new IllegalArgumentException("Maps must contain at least 2 entries and must be equal in size.");

		// Declare variables
		int n = m1.size();
		double sum_x = 0, sum_y = 0, sum_xy = 0; 
		double sum_xx = 0, sum_yy = 0; 

		// Iterate over the values in each hash map
		for (int key: m1.keySet()) {
			double x = m1.get(key);
			
			if (!m2.containsKey(key))
				throw new IllegalArgumentException("Maps must contain the same set of keys.");
			
			double y = m2.get(key);

			sum_x += x; 
			sum_y += y; 
			sum_xy += x * y; 
			sum_xx += x * x; 
			sum_yy += y * y; 
		} 

		// Calculating and return the correlation coefficient 
		double num = n * sum_xy - sum_x * sum_y;
		double denom = Math.sqrt( (n * sum_xx - sum_x * sum_x) * (n * sum_yy - sum_y * sum_y) ); 
		return (denom > 0) ? num / denom : 0; 
	} 
}
