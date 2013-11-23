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

import java.util.Comparator;
import java.util.List;

import rf.bayesian.Bayesian;
import cbir.image.DescriptorType;
import cbir.image.ImageContainer;
import cbir.interfaces.Metric;

/**
 * Provides the score computation using the Bayesian Query Shifting approach.
 * 
 * @author Chris Wendler
 */
public class BayesScore implements cbir.interfaces.Score {
	/** The image database. **/
	private List<ImageContainer> database;
	/** The used distance metric in the score computation. **/
	private Metric norm;
	/** The Query Image after applying Bayesian Query Shifting. **/
	private volatile ImageContainer BQS;
	/**
	 * The Image in the database with the maximum distance to the BQS query
	 * vector.
	 **/
	private volatile ImageContainer BQSmax;
	/**
	 * A variable used to synchronize the usage of this scoring function in case
	 * many threads compute the score at the same time.
	 **/
	private volatile int k = 0;

	/**
	 * Initializes the norm and database fields.
	 * 
	 * @param metric
	 *            the preferred metric for all computations.
	 * @param database
	 *            the list of images that is used as the database.
	 */
	public BayesScore(Metric metric, List<ImageContainer> database) {
		super();
		this.norm = metric;
		this.database = database;
	}

	/**
	 * Initializes all needed variables for the score computation.
	 * 
	 * @param query
	 * @param type
	 */
	public void init(final ImageContainer query, final DescriptorType type) {
		BQS = new Bayesian().shiftQuery(query, type);
		BQSmax = cbir.retriever.Utility.findNearestNeighbors(database, 1,
				new Comparator<ImageContainer>() {
					@Override
					public int compare(ImageContainer a, ImageContainer b) {
						double distA = norm.distance(a, query, type);
						double distB = norm.distance(b, query, type);
						if (distA > distB)
							return -1;
						if (distA < distB)
							return 1;
						return 0;
					}
				}).get(0);
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
		int knew = query.getPositives().size() + query.getNegatives().size();
		synchronized (this) {
			if (k != knew) {
				k = knew;
				init(query, type);
			}
		}
		double dBQS, dBQSmax, relevanceBQS;

		dBQS = norm.distance(BQS, image, type);
		dBQSmax = norm.distance(BQSmax, image, type);
		relevanceBQS = (1 - Math.pow(Math.E, (1 - (dBQS / dBQSmax))))
				/ (1 - Math.E);

		return relevanceBQS;
	}

}
