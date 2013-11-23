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

/**
 * Provides the NN score stabilization using a Bayesian Query Shifting term.
 * 
 * @author Chris Wendler
 */
public class NNBayesScore implements cbir.interfaces.Score {
	/**
	 * The score object that is used to calculate the BQS term of the combined
	 * score.
	 **/
	private BayesScore bayesScore;
	/**
	 * The score object that is used to calculate the NN term of the combined
	 * score.
	 **/
	private NNScore nnScore;

	/**
	 * The constructor needs the both sub-scores.
	 * 
	 * @param bayesScore
	 *            a BayesScore object.
	 * @param nnScore
	 *            a NNScore object
	 */
	public NNBayesScore(BayesScore bayesScore, NNScore nnScore) {
		super();
		this.bayesScore = bayesScore;
		this.nnScore = nnScore;
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
	public double score(final ImageContainer query, final ImageContainer image,
			final DescriptorType type) {
		double relevanceNN = nnScore.score(query, image, type);
		double relevanceBQS = bayesScore.score(query, image, type);

		List<ImageContainer> positives = query.getPositives();
		List<ImageContainer> negatives = query.getNegatives();
		double n, k;
		n = negatives.size();
		k = positives.size() + negatives.size();

		return ((n / k) / (1. + n / k)) * relevanceBQS + (1. / (n / k + 1.))
				* relevanceNN;
	}

}
