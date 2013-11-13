package cbir.metric;

import cbir.image.DescriptorType;
import cbir.image.ImageContainer;
import cbir.interfaces.Metric;

/**
 * Implements the gaussian metric.
 * 
 * @author Chris Wendler
 * 
 */
public class Euclidean implements Metric {

	@Override
	/**
	 * Computes the euclidean distance between two images.
	 * 
	 * @param a
	 * 			An image.
	 * @param b
	 * 			An image.
	 * @param type
	 * 			The descriptor type of the image descriptors.
	 * @returns the distance between image a and image b.
	 */
	public double distance(ImageContainer a, ImageContainer b, DescriptorType type) {
		double dist = 0;
		for (int i = 0; i < a.getDescriptor(type).getValues().length; i++)
			dist += Math.pow(a.getDescriptor(type).getValues()[i]
					- b.getDescriptor(type).getValues()[i], 2);
		dist = Math.sqrt(dist);
		return dist;
	}

}
