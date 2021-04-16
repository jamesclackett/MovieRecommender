package alg.cb.util;

import alg.cb.util.Matrix;

public class Histogram {
	private double min; // the minimum of the range covered by the histogram bins
	private double max; // the maximum value of the range covered by the histogram bins
	private int numBins; // the number of bins the histogram has
	private int numUnderflow; // the number of values less than min (these values are not added to the histogram)
	private int numOverflow; // the number of values greater than max (these values are not added to the histogram)
	private int numValues; // the number of values in the range added to the histogram
	private int[] hist; // an array to hold the number of values in each bin
    
	/**
     * @param min - the minimum of the range covered by the histogram bins
     * @param max - the maximum value of the range covered by the histogram bins
     * @param numBins - the number of bins the histogram has. The range specified 
     * by min and max will be divided into this many bins.
     */
	public Histogram(double min, double max, int numBins) {
		this.min = min;
		this.max = max;
		this.numBins = numBins;
		hist = new int[numBins];
		numUnderflow = 0;
		numOverflow = 0;
		numValues = 0;
	}

	/**
     * @param min - the minimum of the range covered by the histogram bins
     * @param max - the maximum value of the range covered by the histogram bins
     * @param numBins - the number of bins the histogram has. The range specified 
     * by min and max will be divided into this many bins.
     * @param m - constructs a histogram using the data in m
     */
	public Histogram(double min, double max, int numBins, Matrix m) {
		this(min, max, numBins);
		
		// Add each value in the matrix to the histogram
		for (int rowId: m.getRowIds())
			for (int colId: m.getColIds(rowId))
				addValue(m.getValue(rowId, colId));
	}
	
	/**  
	 * @return the minimum value of the range covered by the histogram bins
	 */	
	public double getMin() {
		return min;
	}

	/**  
	 * @return the maximum value of the range covered by the histogram bins
	 */
	public double getMax() {
		return max;
	}

	/**  
	 * @return the number of bins in the histogram
	 */
	public int getNumBins() {
		return numBins;
	}

	/**  
	 * @return the number of values less than min (these values are not added to the histogram)
	 */
	public int getNumUnderflow() {
		return numUnderflow;
	}

	/**  
	 * @return the number of values greater than max (these values are not added to the histogram)
	 */
	public int getNumOverflow() {
		return numOverflow;
	}

	/**  
	 * @return the number of values in the histogram
	 */
	public int getNumValues() {
		return numValues;
	}

	/**  
	 * @return the number of values in each bin
	 */
	public int[] getBinCounts() {
		return hist;
	}
	
	/**  
	 * @return the bin centres
	 */
	public double[] getBinCentres() {
		double[] binCentres = new double[numBins];
		
		double binWidth = (max - min)/ numBins;
		for (int i = 0; i < numBins; i++)
			binCentres[i] = min + (i + 0.5) * binWidth;
		
		return binCentres;
	}
	
	/**
	 * @param v - the value to add to the histogram
	 */
	public void addValue(double v) {
		if (v < min) numUnderflow++;
		else if (v > max) numOverflow++;
		else {
			hist[getBinIndex(v)]++;
			numValues++;
		}
	}

	/** 
	 * The mean and standard deviation is calculated from the bin centres,
	 * weighted by their content. Over/underflows are excluded. 
	 * @return the mean and standard deviation
	 */
	public double[] getMeanStdev(){
		double binWidth = (max - min)/ numBins;
		
		// Calculate the mean
		double sum = 0;
		for (int i = 0; i < numBins; i++) {
			double binCentre = min + (i + 0.5) * binWidth;
			sum += hist[i] * binCentre;
		}
		double mean = (numValues > 0) ? sum / numValues : 0;
		
		// Calculate the standard deviation
		double sqSum = 0;
		for (int i = 0; i < numBins; i++) {
			double binCentre = min + (i + 0.5) * binWidth;
			sqSum += hist[i] * Math.pow(binCentre - mean, 2);
		}		
		double stdev = (numValues - 1 > 0) ? Math.sqrt( sqSum / (numValues - 1) ) : 0;
		
		// Return the mean and standard deviation
		double[] vals = {mean, stdev};
		return vals;
	}

	/**
	 * @return a string representation of this object
	 */
	@Override
	public String toString() {		
		StringBuilder sb = new StringBuilder();
		
		sb.append("min: " + min);
		sb.append(", max: " + max);
		sb.append(", #bins: " + numBins + "\n");
		
		sb.append("#values: " + numValues);
		sb.append(", #underflow: " + numUnderflow);
		sb.append(", #overflow: " + numOverflow + "\n");
		
		double[] stats = getMeanStdev();
		sb.append("mean: " + stats[0]);
		sb.append(", std. dev.: " + stats[1] + "\n");
		
		return sb.toString();
	}
	
	/**
	 * @param v - the value to add to the histogram
	 * @return the index of the bin in which v falls
	 */
	private int getBinIndex(double v) {
		int index = -1;
		double binWidth = (max - min) / numBins;
		for (int i = 0; i < numBins; i++) {
			double highEdge = min + (i + 1) * binWidth;
			if (v <= highEdge) {
				index = i;
				break;
			}
		}
		return index;
	}
}
