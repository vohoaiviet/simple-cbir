package rf.mars;

import java.util.List;

import rf.Utility;
import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.interfaces.Metric;
import cbir.interfaces.RelevanceFeedback;
import cbir.interfaces.Retriever;
import cbir.metric.WeightedGaussian;
import cbir.retriever.RetrieverDistanceBased;

/**
 * Provides the reweighting rf functionality.
 * 
 * @author Chris Wendler
 */

public class MarsGaussian implements RelevanceFeedback {
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
		return new RetrieverDistanceBased(retriever.getDatabase(),new WeightedGaussian(reweightFeatures(query, query.getPositives(), type))).search(query,
				type, resultAmount);
	}

}
