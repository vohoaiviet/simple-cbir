package cbir.score;

import java.util.List;

import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.interfaces.Metric;
import cbir.interfaces.Score;
import cbir.retriever.ComparatorDistanceBased;

public class NNregularizedScore implements Score{
Metric norm;
	
	public NNregularizedScore(Metric metric){
		super();
		this.norm = metric;
	}
	
	@Override
	public double score(Image query, final Image image, final DescriptorType type){
		List<Image> positives = query.getPositives();
		List<Image> negatives = query.getNegatives();		
		Image nearestPositive, nearestNegative;
		double dN, dR;
		nearestPositive = cbir.retriever.Utility.findNearestNeighbors(positives, 1, new ComparatorDistanceBased(image,norm,type)).get(0);
		nearestNegative = cbir.retriever.Utility.findNearestNeighbors(negatives, 1, new ComparatorDistanceBased(image,norm,type)).get(0);
		dN = norm.distance(image, nearestNegative, type);
		dR = norm.distance(image, nearestPositive, type);
		return (1-Math.min(dR, dN))*(dN/(dN+dR));
	}
}
