/*
 * Copyright (C) 2013 Justus Piater,
 * Intelligent and Interactive Systems Group,
 * University of Innsbruck, Austria.
 */
/*
 * This file is part of simple-cbir.
 *
 *  simple-cbir is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  simple-cbir is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with simple-cbir.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Diese Datei ist Teil von simple-cbir.
 *
 *  simple-cbir ist Freie Software: Sie können es unter den Bedingungen
 *  der GNU General Public License, wie von der Free Software Foundation,
 *  Version 3 der Lizenz oder (nach Ihrer Wahl) jeder späteren
 *  veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 *  simple-cbir wird in der Hoffnung, dass es nützlich sein wird, aber
 *  OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite
 *  Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
 *  Siehe die GNU General Public License für weitere Details.
 *
 *  Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 *  Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
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
	 * 
	 * @param filename
	 *            the name of the destination file.
	 * @param numbers
	 *            the numbers that should be written into the file.
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
	 * 
	 * @param filename
	 *            the name of the file containing the numbers.
	 * @return a list of read integers.
	 * @throws NumberFormatException
	 *             is thrown if a token is not parsable to an integer.
	 * @throws IOException
	 *             is thrown if the input file is not found.
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
	 * @return The scalar product.
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
	 * @return The weighted scalar product.
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
	 * @return The euclidean norm of the vector.
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
	 * @return The weighted euclidean norm of the vector.
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
	 * 
	 * @param x
	 *            is the number for which the logarithm is computed.
	 * @param base
	 *            is the base of the logarithm.
	 * @return The logarithm of x to the given base.
	 */
	public static double LogBaseX(double x, double base) {
		return Math.log(x) / Math.log(base);
	}

	/**
	 * Returns the suffix of the descriptor files of a given descriptor-type.
	 * 
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
	 * 
	 * @param values
	 *            the list of Double object that should be converted.
	 * @return the double array.
	 */
	public static double[] doubleFromDouble(List<Double> values) {
		double[] result = new double[values.size()];
		for (int i = 0; i < values.size(); i++)
			result[i] = values.get(i);
		return result;
	}

	/**
	 * Concatenates two double arrays.
	 * 
	 * @param merged
	 *            the first array.
	 * @param is
	 *            the second array.
	 * @return the concatanated array.
	 */
	public static double[] concat(double[] merged, double[] is) {
		double result[] = new double[merged.length + is.length];
		result = Arrays.copyOf(merged, merged.length + is.length);
		System.arraycopy(is, 0, result, merged.length, is.length);
		return result;
	}

}
