package rf.nn;

import java.util.List;

import rf.Utility;
import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.interfaces.Metric;
import cbir.interfaces.RelevanceFeedback;
import cbir.interfaces.Retriever;
import cbir.interfaces.Score;
import cbir.retriever.RetrieverScoreBased;

public class NearestNeighbors implements RelevanceFeedback {
	private RetrieverScoreBased retriever = null;
	private Score score;
	public NearestNeighbors(Score score){
		this.score = score;
	}
	
	@Override
	public List<Image> relevanceFeedbackIteration(Retriever retriever,
			Image query, DescriptorType type, Metric metric,
			List<Image> positives, List<Image> negatives, int resultAmount) {	
		List<Image> results;
		Utility.addImagesToList(query.getPositives(), positives);
		Utility.addImagesToList(query.getNegatives(), negatives);
		

		if(this.retriever == null)
			this.retriever = new RetrieverScoreBased(retriever.getDatabase(), score);
		if(query.getNegatives().size()>0 && query.getPositives().size()>0)
			results = this.retriever.search(query, type, resultAmount);
		else
			results = retriever.search(query, type, resultAmount);
		 
		return results;
	}
	


}
