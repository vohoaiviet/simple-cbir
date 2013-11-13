package cbir.retriever;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import cbir.image.DescriptorType;
import cbir.image.Image;

/**
 * This utility class implements all shared functions of the distance based and the score based retrievers.
 * @author Chris Wendler
 */

public class Utility {
	
	/**
	 * Finds the "amount" nearest neighbors of the given image.
	 * @param image the query image.
	 * @param type the descriptor type of interest.
	 * @param amount the desired amount of nearest neighbors.
	 * @param comparator the comparator which is used to compare images.
	 * @return The list of the nearest neighbors.
	 */
	public static List<Image> findNearestNeighbors(List<Image> database, int amount, Comparator<Image> comparator) {
		List<Image> results = new LinkedList<Image>();
		TreeSet<Image> tree = new TreeSet<Image>(comparator);
		for (Image curr : database) {
			if (tree.size() <= amount) {
				tree.add(curr);
				continue;
			}
			else if (comparator.compare(tree.last(),curr) > 0) {
				tree.remove(tree.last());
				tree.add(curr);
			}
		}
		for (int i = 0; i < amount; i++)
			results.add(tree.pollFirst());
		return results;
	}
	
	/**
	 * Convertes a path containing windows file separators to a path containing linux file separators.
	 * @param windowspath the path of a file in a windows operating system.
	 * @return the path of the file in a linux operating system.
	 */
	public static String toLinuxPath(String windowspath){
		String [] buffer = windowspath.split("\\\\");
		String result = "";
		for(int i=0;i<buffer.length;i++){
			result += buffer[i];
			if(i<(buffer.length-1))
				result += "/";
		}
		return result;
	}
	
	/**
	 * Prints a list of images to a given file in html format.
	 * @param results the list of images to be printed.
	 * @param type the descriptortype is also printed.
	 * @param filename the filename of the target file.
	 */
	public static void printResultListHTML(List<Image> results, DescriptorType type,
			String filename) {
		try {
			File file = new File(filename);
			FileWriter out = new FileWriter(file, true);

			out.write("<h1> " + results.get(0).getFilename() + " " + type
					+ " </h1>\n");
			int i = 0;
			out.write("<div>"+System.getProperty("line.separator"));
			for (Image curr : results) {
				if(curr != null){
					out.write("	<div style=\"float: left; padding-left: 5px; color: white; width:150px; height:150px; background:url("+toLinuxPath(curr.getFilename())+") no-repeat; background-size:100%;\"><b style=\"background-color: black;\">"+i+"</b></div>");
					out.write(System.getProperty("line.separator"));
				}
				i++;
			}
			out.write("</div><br clear=both>"+System.getProperty("line.separator"));
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Queries for an image with a specific name.
	 * @param database the database in which the lookup is performed.
	 * @param filename of the image.
	 * @return The image object with the given filename or null if not found.
	 */
	public static Image getImageByName(List<Image> database, String name) {
		for (Image curr : database)
			if (curr.getFilename().equals(name))
				return curr;
		return null;
	}

}
