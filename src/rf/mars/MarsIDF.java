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
import cbir.image.Descriptor;
import cbir.image.DescriptorType;
import cbir.image.ImageContainer;
import cbir.interfaces.Metric;
import cbir.interfaces.RelevanceFeedback;
import cbir.interfaces.Retriever;

/**
 * Provides the functionality of the rocchio query shifting approach.
 * 
 * @author Chris Wendler
 */

public class MarsIDF implements RelevanceFeedback {
	/**
	 * These are the weights of the rocchio equation, some values that worked
	 * well are: 1, 0.75, 0.15
	 */
	private double weightQuery = 100.d / 100.d, weightPositives = 75.d / 100.d,
			weightNegatives = 15.d / 100.d;

	/**
	 * When you not initiate the weights the default values 1, 0.76, 0.15 are
	 * used.
	 */
	public MarsIDF() {
		super();
	}

	/**
	 * The constructor needs the weights for the rocchio equation.
	 * 
	 * @param a
	 *            is the weight that is applied to the old query vector (e.g.
	 *            1.0).
	 * @param b
	 *            is the weight that is applied to the mean of the positively
	 *            marked images (e.g. 0.75).
	 * @param c
	 *            is the weight that is applied to the mean of the negatively
	 *            marked images (e.g. 0.15).
	 */
	public MarsIDF(double a, double b, double c) {
		super();
		weightQuery = a;
		weightPositives = b;
		weightNegatives = c;
	}

	/**
	 * implements the query vector translation from the mars paper (rocchio
	 * algorithm)
	 * 
	 * @param query
	 *            the query image
	 * @param positives
	 *            the list of results which was marked positive
	 * @param negatives
	 *            all results that were not marked
	 * @param type
	 *            the type of the descriptor which was used for search
	 * @return the image containing the moved query vector
	 */
	public ImageContainer learnQueryVector(ImageContainer query,
			List<ImageContainer> positives, List<ImageContainer> negatives,
			DescriptorType type) {
		int length = query.getDescriptor(type).getValues().length;
		double[] movedQuery = query.getDescriptor(type).getValues();
		double[] meanPositives;
		double[] meanNegatives;
		if (positives.size() > 0)
			meanPositives = Utility.calculateMeans(positives, type);
		else
			meanPositives = new double[length];

		if (negatives.size() > 0)
			meanNegatives = Utility.calculateMeans(negatives, type);
		else
			meanNegatives = new double[length];

		for (int i = 0; i < length; i++) {
			movedQuery[i] = weightQuery * movedQuery[i] + weightPositives
					* meanPositives[i] - weightNegatives * meanNegatives[i];
		}
		Descriptor result = query.getDescriptor(type);
		result.setValues(movedQuery);
		return query;
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

		query = learnQueryVector(query, query.getPositives(),
				query.getNegatives(), type);
		List<ImageContainer> results = retriever.search(query, type,
				resultAmount);

		return results;
	}

}
