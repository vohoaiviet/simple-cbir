package cbir.interfaces;

import cbir.image.DescriptorType;
import cbir.image.ImageContainer;
/**
 * This is the Score interface. 
 * @author Chris Wendler
 *
 */
public interface Score {
	/**
	 * Calculates a score for the given image.
	 * @param query the query vector containing all positively and negatively marked images.
	 * @param image the image for which the score is computed.
	 * @param type the type of descriptor which is considered in the score computation.
	 * @return the score of the image.
	 */
	public double score(ImageContainer query, final ImageContainer image, final DescriptorType type);
}
