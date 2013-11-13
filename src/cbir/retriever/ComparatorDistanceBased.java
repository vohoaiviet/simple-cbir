package cbir.retriever;

import java.util.Comparator;

import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.interfaces.Metric;
/**
 * This comparator uses distances to a given query image 
 * computed by a given distance metric to compare two images.
 * 
 * @author Chris Wendler
 *
 */
public class ComparatorDistanceBased implements Comparator<Image> {
	/** Relative to this image all distances get computed. **/
	private Image image;
	/** The metric that is used to compute a distance. **/
	private Metric metric;
	/** The discriptortype of interest.**/
	private DescriptorType type;

	/**
	 * The constructor needs the query image, a metric and the descriptortype of interest.
	 * @param image the image to which all other images get compared.
	 * @param metric the metric is used to calculate distancs.
	 * @param type is the descriptortype of interest.
	 */
	public ComparatorDistanceBased(Image image, Metric metric, DescriptorType type) {
		this.image = image;
		this.metric = metric;
		this.type = type;
	}

	@Override
	public int compare(Image a, Image b) {
		double distA = metric.distance(a, image, type);
		double distB = metric.distance(b, image, type);
		if (distA < distB)
			return -1;
		if (distA > distB)
			return 1;
		return 0;
	}
}
