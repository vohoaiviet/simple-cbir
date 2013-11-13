package cbir.metric;

import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.interfaces.Metric;

public class Gaussian implements Metric {

	@Override
	public double distance(Image a, Image b, DescriptorType type) {
		double dist = 0;
		for(int i = 0; i < a.getDescriptor(type).getValues().length; i++)
			dist += Math.pow(a.getDescriptor(type).getValues()[i] - b.getDescriptor(type).getValues()[i],2);
		dist = Math.sqrt(dist);
		return dist;
	}

}
