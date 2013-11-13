package rf.mars;

import java.util.List;

import rf.Utility;
import cbir.image.Descriptor;
import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.interfaces.Metric;
import cbir.interfaces.RelevanceFeedback;
import cbir.interfaces.Retriever;

/**
 * this class implements the tfxidf relevance feedback approach from Mars
 */

public class MarsIDF implements RelevanceFeedback {
	private final List<Image> database;
	private final DescriptorType descriptorOfInterest;
	private double[] means;
	private double[] deviation;
	// 30,50,20 worked pretty well
	// wikipedia: 1, 0.8, 0.1
	private double weightQuery = 50.d / 100.d,
			weightPositives = 25.d / 100.d, weightNegatives = 25.d / 100.d;

	/**
	 * prepares the descriptors of type descriptor in the given database for
	 * relevance feedback
	 * 
	 * @param database
	 * @param descriptor
	 * @param k 
	 * @param j 
	 * @param i 
	 */
	public MarsIDF(List<Image> database, DescriptorType descriptor, double a, double b, double c) {
		this.database = database;
		this.descriptorOfInterest = descriptor;
		weightQuery = a;
		weightPositives = b;
		weightNegatives = c;
	}


	/**
	 * normalizes the entries of a given descriptor
	 * 
	 * @param descriptor
	 */
	public void normalize(DescriptorType descriptor) {
		calculateMeans();
		calculateDeviations();
		Utility.normalizeDescriptorsPositive(database, descriptor, means,
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
	 * calculates the deviation for every feature of a feature vector among all
	 * images in the database
	 */
	public void calculateDeviations() {
		deviation = Utility.calculateDeviations(database,
				descriptorOfInterest, means);
	}

	/**
	 * implements the query vector translation from the mars paper (rocchio
	 * algorithm)
	 * 
	 * @param query
	 *            the query image
	 * @param positives
	 *            the list of results which was marked positive
	 * @param negatives
	 *            all results that were not marked
	 * @param type
	 *            the type of the descriptor which was used for search
	 * @return the image containing the moved query vector
	 */
	public Image learnQueryVector(Image query, List<Image> positives,
			List<Image> negatives, DescriptorType type) {
		int length = query.getDescriptor(type).getValues().length;
		double[] movedQuery = query.getDescriptor(type).getValues();
		double[] meanPositives;
		double[] meanNegatives;
		if(positives.size()>0)
			meanPositives = Utility.calculateMeans(positives, type);
		else
			meanPositives = new double[length];
		
		if(negatives.size()>0)
			meanNegatives = Utility.calculateMeans(negatives, type);
		else
			meanNegatives = new double[length];

		for (int i = 0; i < length; i++) {
			movedQuery[i] = weightQuery * movedQuery[i] + weightPositives
					* meanPositives[i] - weightNegatives * meanNegatives[i];
		}
		Descriptor result = query.getDescriptor(type);
		result.setValues(movedQuery);
		return query;
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
		
		
		// move query vector:
		//System.out.println("old query vector: "+Arrays.toString(query.getDescriptor(type).getValues()));
		query = learnQueryVector(query, query.getPositives(), query.getNegatives(), type);
		//System.out.println("new query vector: "+Arrays.toString(query.getDescriptor(type).getValues()));
		List<Image> results = retriever.search(query,
				type, resultAmount);

		return results;
	}

}
