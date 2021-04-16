/**
 * A class to represent a matrix.
 * 
 * Uses a hash map to store matrix elements. 
 * Each key is a row id and each value is a hash map that stores the 
 * column ids (as keys) and values (as values) for that row.
 */

package alg.cb.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Matrix {
	private Map<Integer,Map<Integer,Double>> matrix; // the data structure used to store matrix elements

	/**
	 * constructor - creates a new Matrix object
	 */
	public Matrix() {
		matrix = new HashMap<>();
	}

	/**
	 * adds a value to the matrix
	 * @param row - the row id
	 * @param col - the column id
	 * @param value - the value to be added
	 */
	public void addValue(int row, int col, double value) {
		Map<Integer,Double> map = matrix.containsKey(row) ? matrix.get(row) : new HashMap<>();
		map.put(col, value);
		matrix.put(row, map);
	}

	/**
	 * @param row - the row id
	 * @param col - the column id
	 * @return the value corresponding to (row, col) or 0 if the element is not present in the matrix
	 */
	public double getValue(int row, int col) {
		if (matrix.containsKey(row) && matrix.get(row).containsKey(col))
			return matrix.get(row).get(col);
		else
			return 0;
	}
	
	/**
	 * @return the the row ids
	 */
	public Set<Integer> getRowIds() {
		return matrix.keySet();
	}
	
	/**
	 * @param row - the row id
	 * @return the specified row or null if the row is not present in the matrix
	 */
	public Map<Integer,Double> getRow(int row) {
		return matrix.get(row);
	}
	
	/**
	 * @param row - the row id
	 * @return the column ids in the specified row or an empty hash set if the row is not present in the matrix
	 */
	public Set<Integer> getColIds(int row) {
		if (matrix.containsKey(row))
			return matrix.get(row).keySet();
		else
			return new HashSet<>();
	}
	
	/**
	 * @param row - the row id
	 * @return the mean of the values in the specified row or 0 if the row is not present in the matrix
	 */
	public double getRowMean(int row) {
		double sum = 0;

		Set<Integer> colIds = getColIds(row);
		for (int col: colIds)
			sum += getValue(row, col);
		
		return (colIds.size() > 0) ? sum / colIds.size() : 0;
	}
}
