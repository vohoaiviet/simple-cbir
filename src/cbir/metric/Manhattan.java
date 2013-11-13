package cbir.metric;

import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.interfaces.Metric;

/**
 * Implements the manhattan metric.
 * 
 * @author Matej Stanic
 * 
 */
public class Manhattan implements Metric {

	@Override
	/**
	 * Computes the manhattan distance between two images.
	 * 
	 * @param a
	 * 			An image.
	 * @param b
	 * 			An image.
	 * @param type
	 * 			The descriptor type of the image descriptors.
	 * @returns the distance between image a and image b.
	 */
	public double distance(Image a, Image b, DescriptorType type) {
		double dist = 0;
		for (int i = 0; i < a.getDescriptor(type).getValues().length; i++)
			dist += Math.abs(a.getDescriptor(type).getValues()[i]
					- b.getDescriptor(type).getValues()[i]);
		dist = Math.sqrt(dist);
		return dist;
	}

}
