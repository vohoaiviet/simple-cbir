package cbir.score;

import java.util.List;

import cbir.image.DescriptorType;
import cbir.image.ImageContainer;
import cbir.interfaces.Metric;
import cbir.retriever.ComparatorDistanceBased;
/**
 * Provides the simple NN score computation without any optimization.
 * 
 * @author Chris Wendler
 */
public class NNScore implements cbir.interfaces.Score {	
	/** The used distance metric in the score computation. **/
	Metric norm;
	
	/**
	 * Initializes the norm and database fields.
	 * 
	 * @param metric
	 *            the preferred metric for all computations.
	 */
	public NNScore(Metric metric){
		super();
		this.norm = metric;
	}
	
	/**
	 * Calculates a score for the given image.
	 * @param query the query vector containing all positively and negatively marked images.
	 * @param image the image for which the score is computed.
	 * @param type the type of descriptor which is considered in the score computation.
	 * @return the score of the image.
	 */
	@Override
	public double score(ImageContainer query, final ImageContainer image, final DescriptorType type){
		List<ImageContainer> positives = query.getPositives();
		List<ImageContainer> negatives = query.getNegatives();		
		ImageContainer nearestPositive, nearestNegative;
		double dN, dR;
		
		nearestPositive = cbir.retriever.Utility.findNearestNeighbors(positives, 1, new ComparatorDistanceBased(image,norm,type)).get(0);
		nearestNegative = cbir.retriever.Utility.findNearestNeighbors(negatives, 1, new ComparatorDistanceBased(image,norm,type)).get(0);
		dN = norm.distance(image, nearestNegative, type);
		dR = norm.distance(image, nearestPositive, type);
		return dN/(dN+dR);
	}

}
