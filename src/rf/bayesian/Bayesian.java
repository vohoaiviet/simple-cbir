package rf.bayesian;

import java.util.List;

import rf.Utility;
import cbir.image.Descriptor;
import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.interfaces.Metric;
import cbir.interfaces.RelevanceFeedback;
import cbir.interfaces.Retriever;

public class Bayesian implements RelevanceFeedback {
	private double[] expectationRelevant, expectationIrrelevant;
	private double scatterWithin, scatterBetween;
	private double sigmaSquare;
	
	public Bayesian(){
		super();
	}
	

	private double calculateScatterWithin(List<Image> positives,
			List<Image> negatives, double[] expectationsRelevant,
			double[] expectationsIrrelevant, DescriptorType type) {
		int NN = negatives.size();
		int NR = positives.size();
		int N = NN + NR;
		int length = expectationsRelevant.length;
		double result = 0;
		double deviationRelevant = 0;
		double deviationIrrelevant = 0;
		
		for (Image curr : positives) {
			for (int i = 0; i < length; i++) {
				deviationRelevant += Math.pow(curr.getDescriptor(type)
						.getValues()[i] - expectationRelevant[i], 2);
			}
		}
		if (NR > 1)
			deviationRelevant /= (NR - 1);
		else
			deviationRelevant = 0;

		for (Image curr : negatives) {
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
	
	private double calculateAverageVariance(List<Image> positives,
			List<Image> negatives, double[] expectationsRelevant,
			double[] expectationsIrrelevant, DescriptorType type) {
		double result = 0;
		int length = expectationsRelevant.length;
		int NN = negatives.size();
		int NR = positives.size();
		int N = NN + NR;
		for(Image curr: positives){
			for(int i = 0; i<length; i++)
				result += Math.pow(curr.getDescriptor(type).getValues()[i]-expectationsRelevant[i],2);
		}
		for(Image curr: negatives){
			for(int i = 0; i<length; i++)
				result += Math.pow(curr.getDescriptor(type).getValues()[i]-expectationsIrrelevant[i],2);
		}
		result /= N;
		return result;
		
	}
	
	public Image shiftQuery(Image query,
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
		
		sigmaSquare = scatterBetween * scatterWithin;
		//sigmaSquare = calculateAverageVariance(positives, negatives, expectationRelevant, expectationIrrelevant, type);
		
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
		//System.out.println("old query vector: "+Arrays.toString(query.getDescriptor(type).getValues()));
		Descriptor descriptor = query.getDescriptor(type);
		descriptor.setValues(shiftedQuery);
		//System.out.println("new query vector: "+Arrays.toString(query.getDescriptor(type).getValues()));
		return query;
	}


	/**
	 * positives and negatives must have at least a length of 2 otherwise the
	 * sigma computation wont work
	 */
	@Override
	public List<Image> relevanceFeedbackIteration(Retriever retriever,
			Image query, DescriptorType type, Metric metric,
			List<Image> positives, List<Image> negatives, int resultAmount) {
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
