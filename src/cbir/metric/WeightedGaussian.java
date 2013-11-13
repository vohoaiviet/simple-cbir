package cbir.metric;

import cbir.image.Descriptor;
import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.interfaces.Metric;

public class WeightedGaussian implements Metric{
	/**
	 * maybe change to a hashset of weight arrays for the different descriptors.
	 */
	private double weights [] = null;
	
	public WeightedGaussian(double[] weights) {
		super();
		this.weights = weights;
	}

	@Override
	public double distance(Image a, Image b, DescriptorType type){
		if(weights == null)
			if(type == DescriptorType.MERGED)
				initializeWeights(a,type);
			else
				initializeWeights(a.getDescriptor(type).getValues().length);
		return distance(a.getDescriptor(type),b.getDescriptor(type));
	}
	
	//@Override
	public double distance(Descriptor a, Descriptor b) {
		double dist = 0;
		if(weights == null)
			initializeWeights(a.getValues().length);
		
		for(int i = 0; i < weights.length; i++)
			dist += Math.pow(a.getValues()[i] - b.getValues()[i],2) * weights[i]; 
		
		dist = Math.sqrt(dist);
		return dist;
	}
	
	public void initializeWeights(int length){
		if(weights == null){
			weights = new double[length];
			for(int i = 0; i < weights.length; i++)
				weights[i] = 1.d;
		}
	}
	
	/**
	 * initializes weights for the merged descriptor in order to weight each
	 * individual descriptor instead of each individual feature. FeatureWeight =
	 * 1/(DescriptorAmount*CurrentDescriptorLength);
	 */
	public void initializeWeights(Image query, DescriptorType type) {
		this.weights = MetricUtility.initializeWeights(query, type);
	}

	public double[] getWeights() {
		return weights;
	}
	
	
}
