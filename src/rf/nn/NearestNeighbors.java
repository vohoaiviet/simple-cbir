/* 
 * Copyright (C) 2013 Justus Piater,
 * Intelligent and Interactive Systems Group,
 * University of Innsbruck, Austria.
 */
package rf.nn;

import java.util.List;

import rf.Utility;
import cbir.image.DescriptorType;
import cbir.image.ImageContainer;
import cbir.interfaces.Metric;
import cbir.interfaces.RelevanceFeedback;
import cbir.interfaces.Retriever;
import cbir.interfaces.Score;
import cbir.retriever.RetrieverScoreBased;

/**
 * This class provides the framework for the NN RF approaches, you can use it
 * with different scoring functions that are defined as Score objects.
 * 
 * @author Chris Wendler
 */
public class NearestNeighbors implements RelevanceFeedback {
	/** The score based retriever which is used for the reranking. **/
	private RetrieverScoreBased retriever = null;
	/**
	 * The score object that is used to calculate the scores of the images in
	 * the database.
	 **/
	private Score score;

	/**
	 * The constructor only needs the score that is going to be used for the
	 * reranking.
	 * 
	 * @param score
	 *            the score object that contains all the information to
	 *            calculate the score that determines the reranking.
	 */
	public NearestNeighbors(Score score) {
		this.score = score;
	}

	/**
	 * Performs a relevance feedback iteration. Side effect: all the RF Methods
	 * add the positively and negatively marked images to the query Image
	 * object.
	 * 
	 * @param retriever
	 *            the retriever which is used to search.
	 * @param query
	 *            the query image.
	 * @param type
	 *            the descriptortype which was used for the query.
	 * @param positives
	 *            the images marked as positive.
	 * @param negatives
	 *            the images marked as negative.
	 * @param resultAmount
	 *            the number of desired results.
	 * @return the results after considering the user feedback.
	 */
	@Override
	public List<ImageContainer> relevanceFeedbackIteration(Retriever retriever,
			ImageContainer query, DescriptorType type, Metric metric,
			List<ImageContainer> positives, List<ImageContainer> negatives,
			int resultAmount) {
		List<ImageContainer> results;
		Utility.addImagesToList(query.getPositives(), positives);
		Utility.addImagesToList(query.getNegatives(), negatives);

		if (this.retriever == null)
			this.retriever = new RetrieverScoreBased(retriever.getDatabase(),
					score);
		if (query.getNegatives().size() > 0 && query.getPositives().size() > 0)
			results = this.retriever.search(query, type, resultAmount);
		else
			results = retriever.search(query, type, resultAmount);

		return results;
	}

}
