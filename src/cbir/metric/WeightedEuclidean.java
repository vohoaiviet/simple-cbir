/*
 * Copyright (C) 2013 Justus Piater,
 * Intelligent and Interactive Systems Group,
 * University of Innsbruck, Austria.
 */
/*
 * This file is part of simple-cbir.
 *
 *  simple-cbir is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  simple-cbir is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with simple-cbir.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Diese Datei ist Teil von simple-cbir.
 *
 *  simple-cbir ist Freie Software: Sie können es unter den Bedingungen
 *  der GNU General Public License, wie von der Free Software Foundation,
 *  Version 3 der Lizenz oder (nach Ihrer Wahl) jeder späteren
 *  veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 *  simple-cbir wird in der Hoffnung, dass es nützlich sein wird, aber
 *  OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite
 *  Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
 *  Siehe die GNU General Public License für weitere Details.
 *
 *  Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 *  Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package cbir.metric;

import cbir.image.Descriptor;
import cbir.image.DescriptorType;
import cbir.image.ImageContainer;
import cbir.interfaces.Metric;

/**
 * Implements the weighted euclidean metric.
 * 
 * @author Chris Wendler
 * 
 */
public class WeightedEuclidean implements Metric {
	/** The weights used. */
	private double weights[] = null;

	/**
	 * Constructor.
	 */
	public WeightedEuclidean() {
		super();
	}

	/**
	 * Constructor.
	 */
	public WeightedEuclidean(double[] weights) {
		super();
		this.weights = weights;
	}

	@Override
	/**
	 * Computes the weighted euclidean distance between two images.
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
		if (weights == null)
			if (type == DescriptorType.MERGED)
				initializeWeights(a, type);
			else
				initializeWeights(a.getDescriptor(type).getValues().length);
		return distance(a.getDescriptor(type), b.getDescriptor(type));
	}

	/**
	 * Computes the weighted euclidean distance between two descriptors.
	 * 
	 * @param a
	 *            A descriptor.
	 * @param b
	 *            A descriptor.
	 * @return the distance between image a and image b.
	 */
	public double distance(Descriptor a, Descriptor b) {
		double dist = 0;
		if (weights == null)
			initializeWeights(a.getValues().length);

		for (int i = 0; i < weights.length; i++)
			dist += Math.pow(a.getValues()[i] - b.getValues()[i], 2)
					* weights[i];

		dist = Math.sqrt(dist);
		return dist;
	}

	/**
	 * Initializes the weights with 1s.
	 * 
	 * @param length
	 *            The length of the weight array.
	 */
	public void initializeWeights(int length) {
		if (weights == null) {
			weights = new double[length];
			for (int i = 0; i < weights.length; i++)
				weights[i] = 1.d;
		}
	}

	/**
	 * Initializes weights for the merged descriptor in order to weight each
	 * individual descriptor instead of each individual feature. FeatureWeight =
	 * 1/(DescriptorAmount*CurrentDescriptorLength)
	 * 
	 * @param query
	 *            the query image
	 * @param type
	 *            The descriptor type used.
	 */
	public void initializeWeights(ImageContainer query, DescriptorType type) {
		this.weights = MetricUtility.initializeWeights(query, type);
	}

	public double[] getWeights() {
		return weights;
	}

}
