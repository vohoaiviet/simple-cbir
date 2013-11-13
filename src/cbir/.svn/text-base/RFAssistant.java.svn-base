package cbir;

import java.util.Iterator;
import java.util.List;

import cbir.image.Image;

public class RFAssistant {
	public List<String> positives;
	public List<String> negatives;
	
	public RFAssistant(List<String> positives, List<String> negatives){
		this.positives = positives;
		this.negatives = negatives;
	}
	
	/*
	 * revise the new positive/negative labeled images form the user, when there are misslabels swap between the lists
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
