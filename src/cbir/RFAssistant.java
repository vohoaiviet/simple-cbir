package cbir;

import java.util.Iterator;
import java.util.List;

import cbir.image.Image;
/**
 * This class provides a RFAssistant that is supposed to help the poor guy who has to evaluate
 * different relevance feedback mechanisms by hand. The RFAssistant maintains lists of positve images and
 * negative images for a specific query that are already known and corrects the user if he
 * is tired and does inconsistent marking. 
 * 
 * @author Chris Wendler
 * 
 */
public class RFAssistant {
	/** The list of known positive images for one specific query image. **/
	public List<String> positives;
	/** The list of known negative images for one specific query image. **/
	public List<String> negatives;
	
	/**
	 * The constructor.
	 * @param positives is the list of known positive images for a query image.
	 * @param negatives is the list of known negative images for a query image.
	 */
	public RFAssistant(List<String> positives, List<String> negatives){
		this.positives = positives;
		this.negatives = negatives;
	}
	
	
	/**
	 * Revises the new positive/negative labeled images form the user, when there are inconsistencies compared to the already
	 * known lists these inconsistencies get eliminated.
	 * @param positives
	 * @param negatives
	 */
	public void revisePositivesAndNegatives(List<Image> positives, List<Image> negatives){
		Iterator<Image> it = positives.iterator();
		int falsePositives = 0, falseNegatives = 0;
		while(it.hasNext()){
			Image curr = it.next();
			if(this.negatives.contains(curr.getFilename())){
				falsePositives++;
				negatives.add(curr);
				it.remove();
			}
		}
		it = negatives.iterator();
		while(it.hasNext()){
			Image curr = it.next();
			if(this.positives.contains(curr.getFilename())){
				falseNegatives++;
				positives.add(curr);
				it.remove();
			}
		}
		System.out.println("corrected "+(falsePositives+falseNegatives)+" mistakes: fp="+falsePositives+" fn="+falseNegatives);
	}

	public List<String> getPositives() {
		return positives;
	}

	public List<String> getNegatives() {
		return negatives;
	}
}
