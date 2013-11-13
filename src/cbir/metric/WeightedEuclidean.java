package cbir.metric;

import cbir.image.Descriptor;
import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.interfaces.Metric;

/**
 * Implements the weighted euclidean metric.
 * 
 * @author Chris Wendler
 * 
 */
public class WeightedEuclidean implements Metric {
	/** The weights used. */
	private double weights[] = null;

	
	/**
	 * Constructor.
	 */
	public WeightedEuclidean() {
		super();
	}
	/**
	 * Constructor.
	 */
	public WeightedEuclidean(double[] weights) {
		super();
		this.weights = weights;
	}

	@Override
	/**
	 * Computes the weighted euclidean distance between two images.
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
			if (type == DescriptorType.MERGED)
				initializeWeights(a, type);
			else
				initializeWeights(a.getDescriptor(type).getValues().length);
		return distance(a.getDescriptor(type), b.getDescriptor(type));
	}

	/**
	 * Computes the weighted euclidean distance between two descriptors.
	 * 
	 * @param a
	 *            A descriptor.
	 * @param b
	 *            A descriptor.
	 * @returns the distance between image a and image b.
	 */
	public double distance(Descriptor a, Descriptor b) {
		double dist = 0;
		if (weights == null)
			initializeWeights(a.getValues().length);

		for (int i = 0; i < weights.length; i++)
			dist += Math.pow(a.getValues()[i] - b.getValues()[i], 2)
					* weights[i];

		dist = Math.sqrt(dist);
		return dist;
	}

	/**
	 * Initializes the weights with 1s.
	 * 
	 * @param length
	 *            The length of the weight array.
	 */
	public void initializeWeights(int length) {
		if (weights == null) {
			weights = new double[length];
			for (int i = 0; i < weights.length; i++)
				weights[i] = 1.d;
		}
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

	public double[] getWeights() {
		return weights;
	}

}
