package cbir.interfaces;

import cbir.image.DescriptorType;
import cbir.image.Image;
/**
 * This is the metric interface.
 * @author Chris Wendler
 */
public interface Metric {
	
	/**
	 * The distance between two images, considering descriptors of a specific type.
	 * @param a is an image.
	 * @param b is another image.
	 * @return distance is the distance between the two given images.
	 */
	public double distance(Image a, Image b, DescriptorType type);
	
}
