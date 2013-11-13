package cbir;

/**
 * A class which provides utility functions for classes in the cbir package.
 * Most of the methods provided are used to read and write external files.
 * 
 * @author Chris Wendler
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import cbir.image.DescriptorType;

public class Utils {

	/**
	 * Prints a list of integers to a given outputfile.
	 * @param filename the name of the destination file.
	 * @param numbers the numbers that should be written into the file.
	 */
	public static void printIntListToFile(String filename, List<Integer> numbers) {
		String message = "" + numbers.get(0);
		for (int i = 1; i < numbers.size(); i++) {
			message += " " + numbers.get(i);
		}
		printToFile(filename, message);
	}

	/**
	 * Reads a list of integers separated by spaces.
	 * @param filename the name of the file containing the numbers.
	 * @return a list of read integers.
	 * @throws NumberFormatException is thrown if a token is not parsable to an integer.
	 * @throws IOException is thrown if the input file is not found.
	 */
	public static List<Integer> readIntListFromFile(String filename)
			throws NumberFormatException, IOException {
		LinkedList<Integer> values = new LinkedList<Integer>();
		InputStream fileStream = new FileInputStream(new File(filename));
		Reader decoder = new InputStreamReader(fileStream);
		BufferedReader reader = new BufferedReader(decoder);
		String line;
		while ((line = reader.readLine()) != null) {
			String[] tokens = line.split("\\s+");
			for (String token : tokens) {
				values.add(Integer.parseInt(token));
			}
		}
		reader.close();
		return values;
	}


	/**
	 * Writes a message into a file.
	 * 
	 * @param filename
	 *            The path of the file.
	 * @param message
	 *            The message to be written.
	 */
	public static void printToFile(String filename, String message) {
		File file = new File(filename);
		FileWriter out;
		try {
			out = new FileWriter(file, true);
			out.write(message);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Calculates the scalar product of a and b.
	 * 
	 * @param a
	 *            A double array.
	 * @param b
	 *            A double array.
	 * @returns The scalar product.
	 */
	public static double scalarProduct(double[] a, double[] b) {
		double result = 0;
		for (int i = 0; i < a.length; i++)
			result += a[i] * b[i];
		return result;
	}

	/**
	 * Calculates the weighted scalar product of a and b.
	 * 
	 * @param a
	 *            A double array.
	 * @param b
	 *            A double array.
	 * @param weights
	 *            A weight array.
	 * @returns The weighted scalar product.
	 */
	public static double scalarProduct(double[] a, double[] b, double[] weights) {
		double result = 0;
		for (int i = 0; i < a.length; i++)
			result += weights[i] * a[i] * b[i];
		return result;
	}

	/**
	 * Calculates the euclidean norm of a vector.
	 * 
	 * @param a
	 *            A vector.
	 * @returns The euclidean norm of the vector.
	 */
	public static double norm(double[] a) {
		double result = 0;
		for (int i = 0; i < a.length; i++)
			result += Math.pow(a[i], 2);
		result = Math.sqrt(result);
		return result;
	}

	/**
	 * Calculates the weighted euclidean norm of a vector.
	 * 
	 * @param a
	 *            A vector.
	 * @param weights
	 *            A weight array.
	 * @returns The weighted euclidean norm of the vector.
	 */
	public static double norm(double[] a, double[] weights) {
		double result = 0;
		for (int i = 0; i < a.length; i++)
			result += weights[i] * Math.pow(a[i], 2);
		result = Math.sqrt(result);
		return result;
	}

	/**
	 * Calculates the logarithm of x to the base "base".
	 * @param x is the number for which the logarithm is computed.
	 * @param base is the base of the logarithm.
	 * @return The logarithm of x to the given base.
	 */
	public static double LogBaseX(double x, double base) {
		return Math.log(x) / Math.log(base);
	}

	/**
	 * Returns the suffix of the descriptor files of a given descriptor-type.
	 * @param type
	 * @return a string containing the suffix.
	 */
	public static String getSuffix(DescriptorType type) {
		String result = null;
		switch (type) {
		default:
			result = ".color.histo.gz";
			break;
		}
		return result;
	}

	/**
	 * Convert a list of Double objects to an array of double values.
	 * @param the list of Double object that should be converted.
	 */
	public static double[] doubleFromDouble(List<Double> values) {
		double[] result = new double[values.size()];
		for (int i = 0; i < values.size(); i++)
			result[i] = values.get(i);
		return result;
	}

	/**
	 * Concatenates two double arrays.
	 * @param merged the first array.
	 * @param is the second array.
	 * @return the concatanated array.
	 */
	public static double[] concat(double[] merged, double[] is) {
		double result[] = new double[merged.length + is.length];
		result = Arrays.copyOf(merged, merged.length + is.length);
		System.arraycopy(is, 0, result, merged.length, is.length);
		return result;
	}

}
