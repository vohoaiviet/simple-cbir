package cbir.retriever;

import java.util.Comparator;
import java.util.List;

import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.interfaces.Retriever;
import cbir.interfaces.Score;

/**
 * A Retriever that uses a score function for the ranking instead of a distance function.
 * NOTE: The score based retriever is not compatible with indexing, since our index structure
 * did not improve the results.
 * @author Chris Wendler
 * 
 */

public class RetrieverScoreBased implements Retriever {
	/** The image database defined by a list of images. **/
	private List<Image> database;
	/** The score function that is used to compare the images in the database. **/
	private Score score;

	/**
	 * @param database is a list of images describing an image database.
	 * @param score determines which scoring function is used.
	 */
	public RetrieverScoreBased(List<Image> database, Score score) {
		this.database = database;
		this.score = score;
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
		return Utility.findNearestNeighbors(database, amount,
				new Comparator<Image>() {
					@Override
					public int compare(Image a, Image b) {
						double scoreA = score.score(image, a, type);
						double scoreB = score.score(image, b, type);
						if (scoreA > scoreB)
							return -1;
						if (scoreA < scoreB)
							return 1;
						return 0;
					}
		});
	}

	/**
	 * Performs a search for the given query image and returns the "resultAmount" best results.
	 * @param query the query image that is used.
	 * @param resultAmount the desired amount of results.
	 * @return the best "resultAmount" results in a list.
	 */
	@Override
	public List<Image> search(final Image query, final DescriptorType type,
			int resultAmount) {
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
}
