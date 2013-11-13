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
 * initializes an environment for relevance feedback... maybe make everything
 * static as a utility class
 * 
 * @author Chris
 * 
 */

public class MarsGaussianImproved implements RelevanceFeedback {
	private final List<Image> database;
	private final DescriptorType descriptorOfInterest;
	private double[] lastweights;
	private double[] means;
	private double[] deviation;
	private int iteration = 1;

	/**
	 * prepares the descriptors of type descriptor in the given database for
	 * relevance feedback
	 * 
	 * @param database
	 * @param descriptor
	 */
	public MarsGaussianImproved(List<Image> database, DescriptorType descriptor) {
		this.database = database;
		this.descriptorOfInterest = descriptor;
	}

	/**
	 * normalizes the feature vectors which are used for the relevance feedback
	 */
	public void normalizeDescriptors() {
		Utility.normalizeDescriptors(database, descriptorOfInterest, means,
				deviation);
	}

	/**
	 * calculates the mean for every feature of a feature vector among all
	 * images in the database
	 */
	public void calculateMeans() {
		means = Utility.calculateMeans(database, descriptorOfInterest);
	}

	/**
	 * calculates the deviations for every feature of a feature vector among all
	 * images in the database
	 */
	public void calculateDeivations() {
		deviation = Utility.calculateDeviations(database,
				descriptorOfInterest, means);
	}

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
	 * performs a relevancefeedback iteration
	 * 
	 * @param retriever
	 *            the retriever which is used to search
	 * @param query
	 *            the query image
	 * @param type
	 *            the descriptortype which was used for the query
	 * @param positives
	 *            the images marked positive
	 * @param negatives
	 *            the images marked negative
	 * @param resultAmount
	 *            the number of desired results
	 * @return the results after considering the user feedback
	 */
	@Override
	public List<Image> relevanceFeedbackIteration(Retriever retriever,
			Image query, DescriptorType type, Metric metric, List<Image> positives,
			List<Image> negatives, int resultAmount) {
		Utility.addImagesToList(query.getPositives(), positives);
		Utility.addImagesToList(query.getNegatives(), negatives);
		
		double [] weights = reweightFeatures(query, query.getPositives(), type);
		
		if(lastweights != null){
			System.out.println(iteration);
//			System.out.println("Last weights: "+Arrays.toString(lastweights));
//			System.out.println("Current weights: "+Arrays.toString(weights));
			for(int i = 0; i<weights.length; i++){
				weights[i] =  (1-(1/((double)iteration))) * lastweights[i] + 1/((double)iteration) * weights[i];
			}
//			System.out.println("combined weights: "+Arrays.toString(weights));
		}
		lastweights = weights;
		iteration++;
		
		return new RetrieverDistanceBased(retriever.getDatabase(),new WeightedGaussian(weights)).search(query,
				type, resultAmount);
	}

}
