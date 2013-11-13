package cbir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
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
	 * Creates a RFAssistant object from a given file containing the known marked images.
	 * @param filename the filename of the file containing the filenames of the marked images.
	 * @return a RF assistant object is returned.
	 * @throws IOException if the file is not found.
	 */
	public static RFAssistant createRFAssistant(String filename)
			throws IOException {
		LinkedList<String> positives = new LinkedList<String>();
		LinkedList<String> negatives = new LinkedList<String>();
		File file = new File(filename);
		if (file.exists()) {
			boolean readPositives = false;
			boolean readNegatives = false;
			System.out.println(filename + " exists");
			InputStream fileStream = new FileInputStream(file);
			Reader decoder = new InputStreamReader(fileStream);
			BufferedReader reader = new BufferedReader(decoder);
			String line;
			/* parse positives and negatives */
			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split(System
						.getProperty("line.separator"));
				System.out.println(Arrays.toString(tokens));
				for (String token : tokens) {
					System.out.println(readPositives + "+-" + readNegatives);
					if (token.equals("positives")) {
						System.out.println("read: positives");
						readPositives = true;
						readNegatives = false;
						continue;
					}
					if (token.equals("negatives")) {
						System.out.println("read: negatives");
						readPositives = false;
						readNegatives = true;
						continue;
					}
					if (readPositives) {
						positives.add(token);
						System.out.println("read: " + token);
						continue;
					}
					if (readNegatives) {
						negatives.add(token);
						System.out.println("read: " + token);
						continue;
					}
				}
			}
			reader.close();
		}
		return new RFAssistant(positives, negatives);
	}

	/**
	 * Prints all marked images from a given query to a given outputfile.
	 * @param filename the name of the outputfile.
	 * @param query the query image of which the marked positives/negatives should be stored.
	 * @throws IOException is thrown if there is a problem with creating the file.
	 */
	public static void printQueryHits(String filename, Image query)
			throws IOException {
		List<String> positives = new LinkedList<String>();
		List<String> negatives = new LinkedList<String>();
		File file = new File(filename);
		if (file.exists()) {
			RFAssistant assi = createRFAssistant(filename);
			positives = assi.getPositives();
			negatives = assi.getNegatives();
		}
		for (Image curr : query.getPositives())
			if (!positives.contains(curr.getFilename()))
				positives.add(curr.getFilename());
		for (Image curr : query.getNegatives())
			if (!negatives.contains(curr.getFilename()))
				negatives.add(curr.getFilename());

		FileWriter out = new FileWriter(file, false);
		out.write("positives" + System.getProperty("line.separator"));

		for (String curr : positives)
			out.write(curr + System.getProperty("line.separator"));
		out.write("negatives" + System.getProperty("line.separator"));
		for (String curr : negatives)
			out.write(curr + System.getProperty("line.separator"));
		out.close();
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
