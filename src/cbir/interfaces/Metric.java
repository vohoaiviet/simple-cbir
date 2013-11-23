/*
 * Copyright (C) 2013 Justus Piater,
 * Intelligent and Interactive Systems Group,
 * University of Innsbruck, Austria.
 */
package cbir.interfaces;

import cbir.image.DescriptorType;
import cbir.image.ImageContainer;

/**
 * This is the metric interface.
 * 
 * @author Chris Wendler
 */
public interface Metric {

	/**
	 * The distance between two images, considering descriptors of a specific
	 * type.
	 * 
	 * @param a
	 *            is an image.
	 * @param b
	 *            is another image.
	 * @return distance is the distance between the two given images.
	 */
	public double distance(ImageContainer a, ImageContainer b,
			DescriptorType type);

}
