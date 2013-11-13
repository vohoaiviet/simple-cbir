package cbir.metric;

import cbir.Utils;
import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.interfaces.Metric;

public class Cosine implements Metric {

	@Override
	/**
	 * For cosine similarities resulting in a value of 0, the documents do not 
	 * share any attributes (or words) because the angle between the objects is 
	 * 90 degrees. 
	 */
	public double distance(Image a, Image b, DescriptorType type) {
		double result;
		double[] vectorA = a.getDescriptor(type).getValues();
		double[] vectorB = b.getDescriptor(type).getValues();
		double denominator = Utils.norm(vectorA) * Utils.norm(vectorB);
		if (denominator == 0)
			result = 0;
		else
			result = Utils.scalarProduct(vectorA, vectorB) / denominator;
		return 1.d / (result + 0.000001);
	}

}
