/*
 * Copyright (C) 2013 Justus Piater,
 * Intelligent and Interactive Systems Group,
 * University of Innsbruck, Austria.
 */
package cbir.metric;

import cbir.Utils;
import cbir.image.DescriptorType;
import cbir.image.ImageContainer;
import cbir.interfaces.Metric;

/**
 * Implements the cosine metric.
 * 
 * @author Chris Wendler
 * 
 */
public class Cosine implements Metric {

	@Override
	/**
	 * Computes the cosine distance between two images.
	 * 
	 * @param a
	 * 			An image.
	 * @param b
	 * 			An image.
	 * @param type
	 * 			The descriptor type of the image descriptors.
	 * @returns the distance between image a and image b.
	 */
	public double distance(ImageContainer a, ImageContainer b,
			DescriptorType type) {
		double result;
		double[] vectorA = a.getDescriptor(type).getValues();
		double[] vectorB = b.getDescriptor(type).getValues();
		double denominator = Utils.norm(vectorA) * Utils.norm(vectorB);
		if (denominator == 0)
			result = 0;
		else
			result = Utils.scalarProduct(vectorA, vectorB) / denominator;
		return 1.d / (result + 0.000001);
	}

}
