package cbir.score;

import java.util.List;

import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.interfaces.Metric;
import cbir.interfaces.Score;
import cbir.retriever.ComparatorDistanceBased;


/**
 * Provides the NN score stabilization using a random variable that determines how reliable the NN score is. 
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
	public double score(Image query, final Image image,
			final DescriptorType type) {
		List<Image> positives = query.getPositives();
		List<Image> negatives = query.getNegatives();
		Image nearestPositive, nearestNegative;
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
