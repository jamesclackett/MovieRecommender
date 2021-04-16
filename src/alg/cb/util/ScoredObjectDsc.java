/**
 * Used as a way to sort a set of objects, each of which is associated 
 * with a score.
 * Note: sorts in DESCENDING order.
 */

package alg.cb.util;

public class ScoredObjectDsc implements Comparable<ScoredObjectDsc> {
	private double score; // the score associated with the object
	private Object object; // the object to be sorted

	/**
	 * constructor - creates a new object
	 * @param s - the score associated with o
	 * @param o - an object
	 */
	public ScoredObjectDsc(double s, Object o) {
		score = s;
		object = o;
	}

	/**
	 * @return the score
	 */
	public double getScore() {
		return score;
	}

	/**
	 * @return the object
	 */
	public Object getObject() {
		return object;
	}

	/**
	 * Sorts in DESCENDING order
	 * @param o - the object compared to this object
	 * @return -1 if this object is greater than o and 1 otherwise
	 */
	@Override
	public int compareTo(ScoredObjectDsc o) {
		return (score > o.getScore()) ? -1 : 1;
	}

	/**
	 * @return a string representation of the object
	 */
	@Override
	public String toString() {
		return "(" + score + ", " + object.toString() + ")";
	}
}
