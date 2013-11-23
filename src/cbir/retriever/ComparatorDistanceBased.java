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
package cbir.retriever;

import java.util.Comparator;

import cbir.image.DescriptorType;
import cbir.image.ImageContainer;
import cbir.interfaces.Metric;

/**
 * This comparator uses distances to a given query image computed by a given
 * distance metric to compare two images.
 * 
 * @author Chris Wendler
 * 
 */
public class ComparatorDistanceBased implements Comparator<ImageContainer> {
	/** Relative to this image all distances get computed. **/
	private ImageContainer image;
	/** The metric that is used to compute a distance. **/
	private Metric metric;
	/** The discriptortype of interest. **/
	private DescriptorType type;

	/**
	 * The constructor needs the query image, a metric and the descriptortype of
	 * interest.
	 * 
	 * @param image
	 *            the image to which all other images get compared.
	 * @param metric
	 *            the metric is used to calculate distancs.
	 * @param type
	 *            is the descriptortype of interest.
	 */
	public ComparatorDistanceBased(ImageContainer image, Metric metric,
			DescriptorType type) {
		this.image = image;
		this.metric = metric;
		this.type = type;
	}

	@Override
	public int compare(ImageContainer a, ImageContainer b) {
		double distA = metric.distance(a, image, type);
		double distB = metric.distance(b, image, type);
		if (distA < distB)
			return -1;
		if (distA > distB)
			return 1;
		return 0;
	}
}
