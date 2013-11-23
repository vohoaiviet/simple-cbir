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
package cbir.score;

import java.util.List;

import cbir.image.DescriptorType;
import cbir.image.ImageContainer;
import cbir.interfaces.Metric;
import cbir.interfaces.Score;
import cbir.retriever.ComparatorDistanceBased;

/**
 * Provides the NN score stabilization using a random variable that determines
 * how reliable the NN score is.
 * 
 * @author Chris Wendler
 */
public class NNregularizedScore implements Score {
	Metric norm;

	/**
	 * Initializes the norm and database fields.
	 * 
	 * @param metric
	 *            the preferred metric for all computations.
	 */
	public NNregularizedScore(Metric metric) {
		super();
		this.norm = metric;
	}

	/**
	 * Calculates a score for the given image.
	 * 
	 * @param query
	 *            the query vector containing all positively and negatively
	 *            marked images.
	 * @param image
	 *            the image for which the score is computed.
	 * @param type
	 *            the type of descriptor which is considered in the score
	 *            computation.
	 * @return the score of the image.
	 */
	@Override
	public double score(ImageContainer query, final ImageContainer image,
			final DescriptorType type) {
		List<ImageContainer> positives = query.getPositives();
		List<ImageContainer> negatives = query.getNegatives();
		ImageContainer nearestPositive, nearestNegative;
		double dN, dR;
		nearestPositive = cbir.retriever.Utility.findNearestNeighbors(
				positives, 1, new ComparatorDistanceBased(image, norm, type))
				.get(0);
		nearestNegative = cbir.retriever.Utility.findNearestNeighbors(
				negatives, 1, new ComparatorDistanceBased(image, norm, type))
				.get(0);
		dN = norm.distance(image, nearestNegative, type);
		dR = norm.distance(image, nearestPositive, type);
		return (1 - Math.min(dR, dN)) * (dN / (dN + dR));
	}
}
