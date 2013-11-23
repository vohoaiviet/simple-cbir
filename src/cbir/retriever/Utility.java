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
 *  simple-cbir ist Freie Software: Sie k�nnen es unter den Bedingungen
 *  der GNU General Public License, wie von der Free Software Foundation,
 *  Version 3 der Lizenz oder (nach Ihrer Wahl) jeder sp�teren
 *  ver�ffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 *  simple-cbir wird in der Hoffnung, dass es n�tzlich sein wird, aber
 *  OHNE JEDE GEW�HELEISTUNG, bereitgestellt; sogar ohne die implizite
 *  Gew�hrleistung der MARKTF�HIGKEIT oder EIGNUNG F�R EINEN BESTIMMTEN ZWECK.
 *  Siehe die GNU General Public License f�r weitere Details.
 *
 *  Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 *  Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package cbir.retriever;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import cbir.image.DescriptorType;
import cbir.image.ImageContainer;

/**
 * This utility class implements all shared functions of the distance based and
 * the score based retrievers.
 * 
 * @author Chris Wendler
 */

public class Utility {

	/**
	 * Finds the "amount" nearest neighbors of the given image (defined in the
	 * comparator).
	 * 
	 * @param database
	 *            the list of images that represents the database.
	 * @param amount
	 *            the desired amount of nearest neighbors.
	 * @param comparator
	 *            the comparator which is used to compare images.
	 * @return The list of the nearest neighbors.
	 */
	public static List<ImageContainer> findNearestNeighbors(
			List<ImageContainer> database, int amount,
			Comparator<ImageContainer> comparator) {
		List<ImageContainer> results = new LinkedList<ImageContainer>();
		TreeSet<ImageContainer> tree = new TreeSet<ImageContainer>(comparator);
		for (ImageContainer curr : database) {
			if (tree.size() <= amount) {
				tree.add(curr);
				continue;
			} else if (comparator.compare(tree.last(), curr) > 0) {
				tree.remove(tree.last());
				tree.add(curr);
			}
		}
		for (int i = 0; i < amount; i++)
			results.add(tree.pollFirst());
		return results;
	}

	/**
	 * Convertes a path containing windows file separators to a path containing
	 * linux file separators.
	 * 
	 * @param windowspath
	 *            the path of a file in a windows operating system.
	 * @return the path of the file in a linux operating system.
	 */
	public static String toLinuxPath(String windowspath) {
		String[] buffer = windowspath.split("\\\\");
		String result = "";
		for (int i = 0; i < buffer.length; i++) {
			result += buffer[i];
			if (i < (buffer.length - 1))
				result += "/";
		}
		return result;
	}

	/**
	 * Prints a list of images to a given file in html format.
	 * 
	 * @param results
	 *            the list of images to be printed.
	 * @param type
	 *            the descriptortype is also printed.
	 * @param filename
	 *            the filename of the target file.
	 */
	public static void printResultListHTML(List<ImageContainer> results,
			DescriptorType type, String filename) {
		try {
			File file = new File(filename);
			FileWriter out = new FileWriter(file, true);

			out.write("<h1> " + results.get(0).getFilename() + " " + type
					+ " </h1>\n");
			int i = 0;
			out.write("<div>" + System.getProperty("line.separator"));
			for (ImageContainer curr : results) {
				if (curr != null) {
					out.write("	<div style=\"float: left; padding-left: 5px; color: white; width:150px; height:150px; background:url("
							+ toLinuxPath(curr.getFilename())
							+ ") no-repeat; background-size:100%;\"><b style=\"background-color: black;\">"
							+ i + "</b></div>");
					out.write(System.getProperty("line.separator"));
				}
				i++;
			}
			out.write("</div><br clear=both>"
					+ System.getProperty("line.separator"));
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Queries for an image with a specific name.
	 * 
	 * @param database
	 *            the database in which the lookup is performed.
	 * @param name
	 *            is the filename of the image.
	 * @return The image object with the given filename or null if not found.
	 */
	public static ImageContainer getImageByName(List<ImageContainer> database,
			String name) {
		for (ImageContainer curr : database)
			if (curr.getFilename().equals(name))
				return curr;
		return null;
	}

}
