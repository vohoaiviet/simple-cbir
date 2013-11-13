package cbir.metric;

import java.util.List;

import cbir.image.DescriptorType;
import cbir.image.Image;

public class MetricUtility {
	/**
	 * initializes weights for the merged descriptor in order to weight each
	 * individual descriptor instead of each individual feature. FeatureWeight =
	 * 1/(DescriptorAmount*CurrentDescriptorLength);
	 */
	public static double [] initializeWeights(Image query, DescriptorType type) {
		double[] weights = new double[query.getDescriptor(type).getValues().length];
		if (type == DescriptorType.MERGED) {
			List<DescriptorType> types = query.getOrder();
			int start = 0;
			for (DescriptorType currType : types) {
				int length = query.getDescriptor(currType).getValues().length;
				for (int i = start; i < (start + length); i++)
					weights[i] = (1.d / length) * ((double)weights.length / types.size());
				start += length;
			}
		} else
			for (int i = 0; i < weights.length; i++)
				weights[i] = 1;

		return weights;
	}

}
