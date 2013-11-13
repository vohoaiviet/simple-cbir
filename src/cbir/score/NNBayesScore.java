package cbir.score;


import java.util.List;

import cbir.image.DescriptorType;
import cbir.image.Image;


/**
 * Provides the NN score stabilization using a Bayesian Query Shifting term. 
 * 
 * @author Chris Wendler
 */
public class NNBayesScore implements cbir.interfaces.Score{	
	/** The score object that is used to calculate the BQS term of the combined score.**/
	private BayesScore bayesScore;
	/** The score object that is used to calculate the NN term of the combined score.**/
	private NNScore nnScore;
	
	/**
	 * The constructor needs the both sub-scores.
	 * @param bayesScore a BayesScore object.
	 * @param nnScore a NNScore object
	 */
	public NNBayesScore(BayesScore bayesScore, NNScore nnScore){
		super();
		this.bayesScore = bayesScore;
		this.nnScore = nnScore;
	}
	/**
	 * Calculates a score for the given image.
	 * @param query the query vector containing all positively and negatively marked images.
	 * @param image the image for which the score is computed.
	 * @param type the type of descriptor which is considered in the score computation.
	 * @return the score of the image.
	 */
	@Override
	public double score(final Image query, final Image image, final DescriptorType type){
		double relevanceNN = nnScore.score(query, image, type);
		double relevanceBQS = bayesScore.score(query, image, type);
		
		List<Image> positives = query.getPositives();
		List<Image> negatives = query.getNegatives();		
		double n,k;
		n = negatives.size();
		k = positives.size()+negatives.size();
		
		
		return ((n/k)/(1.+n/k))*relevanceBQS + (1./(n/k + 1.))*relevanceNN;
	}

}
