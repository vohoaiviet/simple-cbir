/*
 * Copyright (C) 2013 Justus Piater,
 * Intelligent and Interactive Systems Group,
 * University of Innsbruck, Austria.
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
