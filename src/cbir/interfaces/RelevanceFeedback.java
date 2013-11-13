package cbir.interfaces;

import java.util.List;

import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.retriever.RetrieverDistanceBased;

public interface RelevanceFeedback {
	/**
	 * performs a relevancefeedback iteration
	 * @param retriever the retriever which is used to search
	 * @param query the query image
	 * @param type the descriptortype which was used for the query
	 * @param positives the images marked positive
	 * @param negatives the images marked negative
	 * @param resultAmount the number of desired results
	 * @return the results after considering the user feedback
	 */
	public List<Image> relevanceFeedbackIteration(Retriever retriever, Image query, DescriptorType type, Metric metric, List<Image> positives, List<Image> negatives, int resultAmount); 

}
