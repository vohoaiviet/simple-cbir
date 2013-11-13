package cbir.metric;

import cbir.Utils;
import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.interfaces.Metric;

public class WeightedCosine implements Metric {
	private double[] weights;

	@Override
	public double distance(Image a, Image b, DescriptorType type) {
		if(weights == null)
			initializeWeights(a, type);
		double result;
		double[] vectorA = a.getDescriptor(type).getValues();
		double[] vectorB = b.getDescriptor(type).getValues();

		double enumerator = Utils.scalarProduct(vectorA, vectorB, weights);
		double denominator = Utils.norm(vectorA, weights) * Utils.norm(vectorB, weights);

		if (denominator == 0)
			result = 0;
		else
			result = enumerator / denominator;
		//to convert similarity into distance
		//return 1.d/(result+0.000001);
		//change to 1-result
		/**
		 * use this only in combination with feature vectors that only contain positive values
		 */
		return 1-result;
	}

	/**
	 * initializes weights for the merged descriptor in order to weight each
	 * individual descriptor instead of each individual feature. FeatureWeight =
	 * 1/(DescriptorAmount*CurrentDescriptorLength);
	 */
	public void initializeWeights(Image query, DescriptorType type) {
		this.weights = MetricUtility.initializeWeights(query, type);
	}

}
