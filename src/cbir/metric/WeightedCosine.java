package cbir.metric;

import cbir.Utils;
import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.interfaces.Metric;

/**
 * Implements the weighted cosine metric.
 * 
 * @author Chris Wendler
 * 
 */
public class WeightedCosine implements Metric {
	/** The weights used. */
	private double[] weights;

	@Override
	/**
	 * Computes the weighted cosine distance between two images.
	 * 
	 * @param a
	 * 			An image.
	 * @param b
	 * 			An image.
	 * @param type
	 * 			The descriptor type of the image descriptors.
	 * @returns the distance between image a and image b.
	 */
	public double distance(Image a, Image b, DescriptorType type) {
		if (weights == null)
			initializeWeights(a, type);
		double result;
		double[] vectorA = a.getDescriptor(type).getValues();
		double[] vectorB = b.getDescriptor(type).getValues();

		double enumerator = Utils.scalarProduct(vectorA, vectorB, weights);
		double denominator = Utils.norm(vectorA, weights)
				* Utils.norm(vectorB, weights);

		if (denominator == 0)
			result = 0;
		else
			result = enumerator / denominator;
		// use this only in combination with feature vectors that only contain
		// positive values

		return 1 - result;
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
	public void initializeWeights(Image query, DescriptorType type) {
		this.weights = MetricUtility.initializeWeights(query, type);
	}

}
