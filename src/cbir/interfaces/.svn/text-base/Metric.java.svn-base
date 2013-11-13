package cbir.interfaces;

import cbir.image.DescriptorType;
import cbir.image.Image;

public interface Metric {
	/**
	 * The distance between two descriptors
	 * @param a
	 * @param b
	 * @return distance
	 */
	//public double distance(Descriptor a, Descriptor b);
	
	/**
	 * The distance between two images, considering descriptors of a specific type.
	 * Note: this method has some can have some additional functionality since there is more information.
	 * @param a
	 * @param b
	 * @return distance
	 */
	public double distance(Image a, Image b, DescriptorType type);
	
}
