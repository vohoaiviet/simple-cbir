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
package rf.mars;

import java.util.List;

import rf.Utility;
import cbir.image.DescriptorType;
import cbir.image.ImageContainer;
import cbir.interfaces.Metric;
import cbir.interfaces.RelevanceFeedback;
import cbir.interfaces.Retriever;
import cbir.metric.WeightedEuclidean;
import cbir.retriever.RetrieverDistanceBased;

/**
 * Provides the reweighting rf functionality.
 * 
 * @author Chris Wendler
 */

public class MarsGaussian implements RelevanceFeedback {
	/**
	 * calculates the new weight matrix for a given set of positive examples
	 * 
	 * @param query
	 *            the query image
	 * @param positives
	 *            the results marked as positive examples
	 * @param type
	 *            the type of the descriptor which was used for the search
	 * @return the weight matrix
	 */
	public double[] reweightFeatures(ImageContainer query,
			List<ImageContainer> positives, DescriptorType type) {
		double[] means = Utility.calculateMeans(positives, type);
		double[] deviations = Utility.calculateDeviations(positives, type,
				means);
		// inverse derivations == new weights
		for (int i = 0; i < deviations.length; i++)
			if (deviations[i] != 0)
				deviations[i] = 1.d / deviations[i];
			else
				deviations[i] = 1;

		return deviations;
	}

	/**
	 * Performs a relevance feedback iteration. Side effect: all the RF Methods
	 * add the positively and negatively marked images to the query Image
	 * object.
	 * 
	 * @param retriever
	 *            the retriever which is used to search.
	 * @param query
	 *            the query image.
	 * @param type
	 *            the descriptortype which was used for the query.
	 * @param positives
	 *            the images marked as positive.
	 * @param negatives
	 *            the images marked as negative.
	 * @param resultAmount
	 *            the number of desired results.
	 * @return the results after considering the user feedback.
	 */
	@Override
	public List<ImageContainer> relevanceFeedbackIteration(Retriever retriever,
			ImageContainer query, DescriptorType type, Metric metric,
			List<ImageContainer> positives, List<ImageContainer> negatives,
			int resultAmount) {
		Utility.addImagesToList(query.getPositives(), positives);
		Utility.addImagesToList(query.getNegatives(), negatives);
		return new RetrieverDistanceBased(retriever.getDatabase(),
				new WeightedEuclidean(reweightFeatures(query,
						query.getPositives(), type))).search(query, type,
				resultAmount);
	}

}
