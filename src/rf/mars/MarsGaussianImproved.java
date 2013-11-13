package rf.mars;

import java.util.List;

import rf.Utility;
import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.interfaces.Metric;
import cbir.interfaces.RelevanceFeedback;
import cbir.interfaces.Retriever;
import cbir.metric.WeightedEuclidean;
import cbir.retriever.RetrieverDistanceBased;

/**
 * This was an attempt to improve the reweighting approach which did not improve the results.
 * Instead of redoing the whole weight vector every iteration this approach uses a weighted sum of
 * the weight vectors where the weight of each weight vector is 1 divided by the current iteration number.
 *	
 * @author Chris Wendler
 * 
 */

public class MarsGaussianImproved implements RelevanceFeedback {
	private double[] lastweights;
	private int iteration = 1;


	/**
	 * calculates the new weight matrix for a given set of positive examples
	 * 
	 * @param query
	 *            the query image
	 * @param positives
	 *            the results marked as positive examples
	 * @param type
	 *            the type of the descriptor which was used for the search
	 * @return the weight matrix
	 */
	public double[] reweightFeatures(Image query, List<Image> positives,
			DescriptorType type) {
		double[] means = Utility.calculateMeans(positives, type);
		double[] deviations = Utility.calculateDeviations(positives, type,
				means);
		// inverse derivations == new weights
		for (int i = 0; i < deviations.length; i++)
			if (deviations[i] != 0)
				deviations[i] = 1.d / deviations[i];
			else
				deviations[i] = 1;

		return deviations;
	}

	/**
	 * Performs a relevance feedback iteration. Side effect:
	 * all the RF Methods add the positively and negatively marked images to the query Image object.
	 * @param retriever the retriever which is used to search.
	 * @param query the query image.
	 * @param type the descriptortype which was used for the query.
	 * @param positives the images marked as positive.
	 * @param negatives the images marked as negative.
	 * @param resultAmount the number of desired results.
	 * @return the results after considering the user feedback.
	 */
	@Override
	public List<Image> relevanceFeedbackIteration(Retriever retriever,
			Image query, DescriptorType type, Metric metric, List<Image> positives,
			List<Image> negatives, int resultAmount) {
		Utility.addImagesToList(query.getPositives(), positives);
		Utility.addImagesToList(query.getNegatives(), negatives);
		
		double [] weights = reweightFeatures(query, query.getPositives(), type);
		
		if(lastweights != null){

			for(int i = 0; i<weights.length; i++){
				weights[i] =  (1-(1/((double)iteration))) * lastweights[i] + 1/((double)iteration) * weights[i];
			}
		}
		lastweights = weights;
		iteration++;
		
		return new RetrieverDistanceBased(retriever.getDatabase(),new WeightedEuclidean(weights)).search(query,
				type, resultAmount);
	}

}
