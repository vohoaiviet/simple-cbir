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

import java.util.List;

import cbir.image.DescriptorType;
import cbir.image.ImageContainer;

/**
 * Provides a function which initializes weights for the merged descriptor. Used
 * for weighted cosine and weighted gaussian.
 * 
 * @author Chris Wendler
 * 
 */
public class MetricUtility {

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
	public static double[] initializeWeights(ImageContainer query,
			DescriptorType type) {
		double[] weights = new double[query.getDescriptor(type).getValues().length];
		if (type == DescriptorType.MERGED) {
			List<DescriptorType> types = query.getOrder();
			int start = 0;
			for (DescriptorType currType : types) {
				int length = query.getDescriptor(currType).getValues().length;
				for (int i = start; i < (start + length); i++)
					weights[i] = (1.d / length)
							* ((double) weights.length / types.size());
				start += length;
			}
		} else
			for (int i = 0; i < weights.length; i++)
				weights[i] = 1;

		return weights;
	}

}
