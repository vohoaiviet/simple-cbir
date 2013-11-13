package cbir.interfaces;

import java.util.List;

import cbir.image.DescriptorType;
import cbir.image.ImageContainer;
/**
 * This is the Relevance Feedback Method interface.
 * @author Chris Wendler
 *
 */
public interface RelevanceFeedback {
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
	public List<ImageContainer> relevanceFeedbackIteration(Retriever retriever, ImageContainer query, DescriptorType type, Metric metric, List<ImageContainer> positives, List<ImageContainer> negatives, int resultAmount); 

}
