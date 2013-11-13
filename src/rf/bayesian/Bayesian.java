package rf.bayesian;

import java.util.List;
import rf.Utility;
import cbir.image.Descriptor;
import cbir.image.DescriptorType;
import cbir.image.ImageContainer;
import cbir.interfaces.Metric;
import cbir.interfaces.RelevanceFeedback;
import cbir.interfaces.Retriever;

/**
 * This class implements the Bayesian Query Shifting.
 * 
 * @author Chris Wendler
 */
public class Bayesian implements RelevanceFeedback {
	
	/** The expectated descriptor vector of the relevant images. **/
	private double[] expectationRelevant;
	/** The expectated descriptor vector of the irrelevant images. **/
	private double [] expectationIrrelevant;
	/** Scatter within is the averaged deviation from the two expectation vectors of the image classes. **/
	private double scatterWithin;
	/** Scatter between is the distance between the expectation vectors.**/
	private double scatterBetween;
	/** The approximation of the variance. **/
	private double sigmaSquare;
	/** The expectated descriptor vector of the relevant images. **/
	private boolean useScatterSigma = true;
	
	/**
	 * If you use the default constructor the approximation chosen for sigma is
	 * the product of scatterWithin and scatterBetween.
	 */
	public Bayesian(){
		super();
	}
	
	/**
	 * The boolean flag of the constructor denotes which approximation is used for sigma.
	 * @param useScatterSigma if this is true sigmaSquare equals the product of scatterBetween and scatterWithin,
	 * else sigmaSquare is the average Variance of the positives and negatives. 
	 */
	public Bayesian(boolean useScatterSigma){
		super();
		this.useScatterSigma = useScatterSigma;
	}
	
	/**
	 * Calculates the scatter within, which is necessary for the approximation of the Variance.
	 * @param positives is the list of positively marked images.
	 * @param negatives is the list of negatively marked images.
	 * @param expectationsRelevant is the expectation value of the relevant images.
	 * @param expectationsIrrelevant is the expectation value of the irrelevant images.
	 * @param type is the descriptor type which is considered for the calculation.
	 * @return the calculated value for scatter within.
	 */
	private double calculateScatterWithin(List<ImageContainer> positives,
			List<ImageContainer> negatives, double[] expectationsRelevant,
			double[] expectationsIrrelevant, DescriptorType type) {
		int NN = negatives.size();
		int NR = positives.size();
		int N = NN + NR;
		int length = expectationsRelevant.length;
		double result = 0;
		double deviationRelevant = 0;
		double deviationIrrelevant = 0;
		
		for (ImageContainer curr : positives) {
			for (int i = 0; i < length; i++) {
				deviationRelevant += Math.pow(curr.getDescriptor(type)
						.getValues()[i] - expectationRelevant[i], 2);
			}
		}
		if (NR > 1)
			deviationRelevant /= (NR - 1);
		else
			deviationRelevant = 0;

		for (ImageContainer curr : negatives) {
			for (int i = 0; i < length; i++) {
				deviationIrrelevant += Math.pow(curr.getDescriptor(type)
						.getValues()[i] - expectationIrrelevant[i], 2);
			}
		}
		if (NN > 1)
			deviationIrrelevant /= (NN - 1);
		else
			deviationIrrelevant = 0;

		result = ((double) NR / (double) N) * deviationRelevant
				+ ((double) NN / (double) N) * deviationIrrelevant;
		result = Math.sqrt(result);

		return result;
	}

	/**
	 * Calculates the value scatter between which is used to approximate the variance.
	 * @param expectationsRelevant is the expectation value of the relevant images.
	 * @param expectationsIrrelevant  is the expectation value of the irrelevant images.
	 * @return the calculated value for scatter between.
	 */
	private double calculateScatterBetween(double[] expectationsRelevant,
			double[] expectationsIrrelevant) {
		double result = 0;
		for (int i = 0; i < expectationsRelevant.length; i++) {
			result += Math.pow(expectationsRelevant[i]
					- expectationsIrrelevant[i], 2);

		}
		result = Math.sqrt(result);
		return result;
	}
	
	/**
	 * Calculates a different approximation for the variance by adding up the variance 
	 * of the relevant images with the variance of the irrelevant images.
	 * @param positives is the list of positively marked images.
	 * @param negatives is the list of negatively marked images.
	 * @param expectationsRelevant is the expectation value of the relevant images.
	 * @param expectationsIrrelevant is the expectation value of the irrelevant images.
	 * @param type is the descriptor type which is considered for the calculation.
	 * @return an approximation for the variance.
	 */
	private double calculateAverageVariance(List<ImageContainer> positives,
			List<ImageContainer> negatives, double[] expectationsRelevant,
			double[] expectationsIrrelevant, DescriptorType type) {
		double result = 0;
		int length = expectationsRelevant.length;
		int NN = negatives.size();
		int NR = positives.size();
		int N = NN + NR;
		for(ImageContainer curr: positives){
			for(int i = 0; i<length; i++)
				result += Math.pow(curr.getDescriptor(type).getValues()[i]-expectationsRelevant[i],2);
		}
		for(ImageContainer curr: negatives){
			for(int i = 0; i<length; i++)
				result += Math.pow(curr.getDescriptor(type).getValues()[i]-expectationsIrrelevant[i],2);
		}
		result /= N;
		return result;
		
	}
	
	/**
	 * Performs the query shifting.
	 * @param query the old query image.
	 * @param type the type of descriptor which has to be considered.
	 * @return The new (shifted) query image is returned.
	 */
	public ImageContainer shiftQuery(ImageContainer query,
			DescriptorType type) {
		int NN = query.getNegatives().size();
		int NR = query.getPositives().size();
		if(NR > 0)
			expectationRelevant = Utility.calculateMeans(query.getPositives(), type);
		else
			expectationRelevant = new double[query.getDescriptor(type).getValues().length];
		
		if(NN > 0)
			expectationIrrelevant = Utility.calculateMeans(query.getNegatives(), type);
		else
			expectationIrrelevant = new double[query.getDescriptor(type).getValues().length];
		
		scatterBetween = calculateScatterBetween(expectationRelevant,
				expectationIrrelevant);
		scatterWithin = calculateScatterWithin(query.getPositives(), query.getNegatives(),
				expectationRelevant, expectationIrrelevant, type);
		
		if(useScatterSigma)
			sigmaSquare = scatterBetween * scatterWithin;
		else
			sigmaSquare = calculateAverageVariance(query.getPositives(), query.getNegatives(), expectationRelevant, expectationIrrelevant, type);
		
		int length = query.getDescriptor(type).getValues().length;
		double[] shiftedQuery = new double[length];
		double normSquare = 0;
		for (int j = 0; j < length; j++)
			normSquare += Math.pow(expectationRelevant[j]
					- expectationIrrelevant[j], 2);
		
		for (int i = 0; i < length; i++) {
			shiftedQuery[i] = expectationRelevant[i];
			shiftedQuery[i] += (sigmaSquare / normSquare)
					* (1 - ((NR - NN) / Math.max(NR, NN)))
					* (expectationRelevant[i] - expectationIrrelevant[i]);
		}
		Descriptor descriptor = query.getDescriptor(type);
		descriptor.setValues(shiftedQuery);
		return query;
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
	public List<ImageContainer> relevanceFeedbackIteration(Retriever retriever,
			ImageContainer query, DescriptorType type, Metric metric,
			List<ImageContainer> positives, List<ImageContainer> negatives, int resultAmount) {
		Utility.addImagesToList(query.getPositives(), positives);
		Utility.addImagesToList(query.getNegatives(), negatives);
		int NN = query.getNegatives().size();
		int NR = query.getPositives().size();
		if((NR == 0 && NN == 0) || (NR == resultAmount))
			return retriever.search(query, type, resultAmount);
		
		query = shiftQuery(query, type);

		return retriever.search(query, type, resultAmount);
	}


	
	
}
