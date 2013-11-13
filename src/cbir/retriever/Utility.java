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

public class Utility {
	
	public static List<Image> findNearestNeighbors(List<Image> database, int amount, Comparator<Image> comparator) {
		List<Image> results = new LinkedList<Image>();
		TreeSet<Image> tree = new TreeSet<Image>(comparator);
		
		for (Image curr : database) {
			if (tree.size() <= amount) {
				//System.out.println(tree.size() +" adding "+ curr.getFilename());
				tree.add(curr);
				continue;
			}
			else if (comparator.compare(tree.last(),curr) > 0) {
				//System.out.println(tree.size() +" removing "+ tree.last().getFilename());
				tree.remove(tree.last());
				//System.out.println(tree.size() +" adding "+ curr.getFilename());
				tree.add(curr);
			}
		}

		for (int i = 0; i < amount; i++)
			results.add(tree.pollFirst());

		return results;
	}
	
	public static String toLinuxPath(String windowspath){
		String [] buffer = windowspath.split("\\\\");
		//System.out.println(buffer.length);
		String result = "";
		for(int i=0;i<buffer.length;i++){
			result += buffer[i];
			if(i<(buffer.length-1))
				result += "/";
		}
		//System.out.println(result);
		return result;
	}
	
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
					//out.write(i + " <img src=\"" + curr.getFilename() + "\" width=\"150px\" height=\"150px\"/>\n");
				i++;
			}
			out.write("</div><br clear=both>"+System.getProperty("line.separator"));
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Image getImageByName(List<Image> database, String name) {
		for (Image curr : database)
			if (curr.getFilename().equals(name))
				return curr;
		return null;
	}

}
