package cbir;

/**
 * A class which provides utility functions.
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
import cbir.image.Image;

public class Utils {

	/**
	 * 
	 * @param filename
	 * @param numbers
	 */
	public static void printIntListToFile(String filename, List<Integer> numbers) {
		String message = "" + numbers.get(0);
		for (int i = 1; i < numbers.size(); i++) {
			message += " " + numbers.get(i);
		}
		printToFile(filename, message);
	}

	/**
	 * 
	 * @param filename
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
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
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static RFAssistant createRFAssistant(String filename)
			throws IOException {
		LinkedList<String> positives = new LinkedList<String>();
		LinkedList<String> negatives = new LinkedList<String>();
		File file = new File(filename);
		if (file.exists()) {
			boolean readPositives = false;
			boolean readNegatives = false;
			System.out.println(filename + " exists");
			InputStream fileStream = new FileInputStream(file);
			Reader decoder = new InputStreamReader(fileStream);
			BufferedReader reader = new BufferedReader(decoder);
			String line;
			/* parse positives and negatives */
			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split(System
						.getProperty("line.separator"));
				System.out.println(Arrays.toString(tokens));
				for (String token : tokens) {
					System.out.println(readPositives + "+-" + readNegatives);
					if (token.equals("positives")) {
						System.out.println("read: positives");
						readPositives = true;
						readNegatives = false;
						continue;
					}
					if (token.equals("negatives")) {
						System.out.println("read: negatives");
						readPositives = false;
						readNegatives = true;
						continue;
					}
					if (readPositives) {
						positives.add(token);
						System.out.println("read: " + token);
						continue;
					}
					if (readNegatives) {
						negatives.add(token);
						System.out.println("read: " + token);
						continue;
					}
				}
			}
			reader.close();
		}
		return new RFAssistant(positives, negatives);
	}

	/**
	 * 
	 * @param filename
	 * @param query
	 * @throws IOException
	 */
	public static void printQueryHits(String filename, Image query)
			throws IOException {
		List<String> positives = new LinkedList<String>();
		List<String> negatives = new LinkedList<String>();
		File file = new File(filename);
		if (file.exists()) {
			RFAssistant assi = createRFAssistant(filename);
			positives = assi.getPositives();
			negatives = assi.getNegatives();
		}
		for (Image curr : query.getPositives())
			if (!positives.contains(curr.getFilename()))
				positives.add(curr.getFilename());
		for (Image curr : query.getNegatives())
			if (!negatives.contains(curr.getFilename()))
				negatives.add(curr.getFilename());

		FileWriter out = new FileWriter(file, false);
		out.write("positives" + System.getProperty("line.separator"));

		for (String curr : positives)
			out.write(curr + System.getProperty("line.separator"));
		out.write("negatives" + System.getProperty("line.separator"));
		for (String curr : negatives)
			out.write(curr + System.getProperty("line.separator"));
		out.close();
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
			// TODO Auto-generated catch block
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

	public static double LogBaseX(double x, double base) {
		return Math.log(x) / Math.log(base);
	}

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
	 *  
	 */
	public static double[] doubleFromDouble(List<Double> values) {
		double[] result = new double[values.size()];
		for (int i = 0; i < values.size(); i++)
			result[i] = values.get(i);
		return result;
	}

	public static double[] concat(double[] merged, double[] is) {
		double result[] = new double[merged.length + is.length];
		result = Arrays.copyOf(merged, merged.length + is.length);
		System.arraycopy(is, 0, result, merged.length, is.length);
		return result;
	}

	public static double[] normalize(double[] histogram, double histoMax) {
		double[] result = Arrays.copyOf(histogram, histogram.length);
		double factor = 1.d / histoMax;
		for (int i = 0; i < histogram.length; i++)
			result[i] *= factor;
		return result;
	}

	public static void main(String[] args) {
		double[] arr = { 1, 2, 3, 1, 4 };
		System.out.println(Arrays.toString(concat(new double[1], arr)));
	}
}
