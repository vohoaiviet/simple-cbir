package cbir.retriever;

import ind.kdtree.KDTree;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.interfaces.Metric;
import cbir.interfaces.Retriever;

/**
 * Class that realizes a query.
 * 
 * @author chris
 * 
 */
public class RetrieverDistanceBased implements Retriever{
	private List<Image> database;
	private Metric metric;
	private final HashMap<DescriptorType, KDTree> trees;

	/**
	 * @param database
	 * @param types indicate for which descriptors, index structures should get formed
	 */
	public RetrieverDistanceBased(List<Image> database, Metric metric, DescriptorType... types) {
		this.database = database;
		this.metric = metric;
		this.trees = new HashMap<DescriptorType, KDTree>();
		for (DescriptorType type : types) {
			trees.put(type,
					new KDTree(database, database.get(0).getDescriptor(type)
							.getValues().length, type));
		}
	}

	public List<Image> findNearestNeighbors(final Image image,
			final DescriptorType type, int amount) {
		return Utility.findNearestNeighbors(database,amount,new ComparatorDistanceBased(image,metric,type));
	}

	/**
	 * perform a search for all descriptors...
	 * 
	 * @param query
	 * @param metric
	 * @param resultAmount
	 * @return the top resultAmount results
	 */
	@Override
	public List<Image> search(final Image query, 
			final DescriptorType type, int resultAmount) {
		// use index structure if possible
		if (trees.containsKey(type))
			return trees.get(type).nearestNeighborSearch(resultAmount, query,
					metric, type);
		// otherwise just use sorting to make the ranking
//		 Collections.sort(database, new Comparator<Image>() {
//		 @Override
//		 public int compare(Image a, Image b) {
//		 if (metric.distance(a, query, type) < metric.distance(b, query,
//		 type))
//		 return -1;
//		 if (metric.distance(a, query, type) > metric.distance(b, query,
//		 type))
//		 return 1;
//		 return 0;
//		 }
//		 });	
//		 return database.subList(0, resultAmount);
  		return findNearestNeighbors(query, type, resultAmount);
	}

	public void printResultList(List<Image> results, DescriptorType type) {
		System.out.println("<h1> " + results.get(0).getFilename() + " " + type
				+ " </h1>");
		int i = 0;
		for (Image curr : results) {
			System.out
					.println(i + " <img src=\"" + curr.getFilename() + "\" width=\"150px\" height=\"150px\"/>");
			i++;
		}
	}

	@Override
	public void printResultListHTML(List<Image> results, DescriptorType type,
			String filename) {
		Utility.printResultListHTML(results, type, filename);
	}

	@Override
	public Image getImageByName(String name) {
		return Utility.getImageByName(database, name);
	}

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
