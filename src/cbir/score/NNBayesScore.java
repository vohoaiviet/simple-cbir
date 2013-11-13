package cbir.score;


import java.util.List;

import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.interfaces.Metric;

public class NNBayesScore implements cbir.interfaces.Score{	
	private Metric norm;
	private BayesScore bayesScore;
	private NNScore nnScore;
	
	public NNBayesScore(Metric metric, BayesScore bayesScore, NNScore nnScore){
		super();
		this.norm = metric;
		this.bayesScore = bayesScore;
		this.nnScore = nnScore;
	}
	
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
