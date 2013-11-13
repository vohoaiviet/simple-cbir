package cbir.retriever;

import ind.kdtree.KDTree;

import java.util.HashMap;
import java.util.List;

import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.interfaces.Metric;
import cbir.interfaces.Retriever;

/**
 * A Retriever that uses a distance function for the ranking.
 * @author Chris Wendler
 * 
 */
public class RetrieverDistanceBased implements Retriever{
	/** The image database defined by a list of images. **/
	private List<Image> database;
	/** The distance function that is used to compare the images in the database. **/
	private Metric metric;
	/** This hashmap contains the indexstructures for their corresponding descriptortypes. **/
	private final HashMap<DescriptorType, KDTree> trees;

	/**
	 * @param database is a list of images describing an image database.
	 * @param metric determines which distance function is used.
	 * @param types denote for which descriptortypes index structures should be generated.
	 */
	public RetrieverDistanceBased(List<Image> database, Metric metric, DescriptorType ... types) {
		this.database = database;
		this.metric = metric;
		this.trees = new HashMap<DescriptorType, KDTree>();
		for (DescriptorType type : types) {
			trees.put(type,
					new KDTree(database, database.get(0).getDescriptor(type)
							.getValues().length, type));
		}
	}

	/**
	 * Finds the "amount" nearest neighbors of the given image.
	 * @param image the query image.
	 * @param type the descriptor type of interest.
	 * @param amount the desired amount of nearest neighbors.
	 * @return The list of the nearest neighbors.
	 */
	public List<Image> findNearestNeighbors(final Image image,
			final DescriptorType type, int amount) {
		return Utility.findNearestNeighbors(database,amount,new ComparatorDistanceBased(image,metric,type));
	}

	/**
	 * Performs a search for the given query image and returns the "resultAmount" best results.
	 * @param query the query image that is used.
	 * @param resultAmount the desired amount of results.
	 * @return the best "resultAmount" results in a list.
	 */
	@Override
	public List<Image> search(final Image query, 
			final DescriptorType type, int resultAmount) {
		if (trees.containsKey(type))
			return trees.get(type).nearestNeighborSearch(resultAmount, query,
					metric, type);

  		return findNearestNeighbors(query, type, resultAmount);
	}

	/**
	 * Prints a list of images to a given file in html format.
	 * @param results the list of images to be printed.
	 * @param type the descriptortype is also printed.
	 * @param filename the filename of the target file.
	 */
	@Override
	public void printResultListHTML(List<Image> results, DescriptorType type,
			String filename) {
		Utility.printResultListHTML(results, type, filename);
	}

	/**
	 * Queries for an image with a specific name.
	 * @param filename of the image.
	 * @return The image object with the given filename or null if not found.
	 */
	@Override
	public Image getImageByName(String name) {
		return Utility.getImageByName(database, name);
	}

	/**
	 * A getter for the database.
	 * @return a list of Images that describe the database.
	 */
	@Override
	public List<Image> getDatabase() {
		return database;
	}

	public void setDatabase(List<Image> database) {
		this.database = database;
	}

	public void setMetric(Metric metric) {
		this.metric = metric;
	}

}
