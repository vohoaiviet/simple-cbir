package cbir;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import cbir.image.ImageContainer;

/**
 * An util class for labelling.
 * 
 * @author Stanic Matej
 * 
 */
public class LabelUtils {

	/**
	 * Automatic labelling of images corresponding to their sub-folders.
	 * 
	 * @param database
	 *            database to be labelled
	 */
	public static void labelDatabase(List<ImageContainer> database) {
		Iterator<ImageContainer> it = database.iterator();
		while (it.hasNext()) {
			ImageContainer temp = it.next();
			String[] filename = temp.getFilename().split(Pattern.quote("\\"));
			// get last sub-folder
			String label = filename[filename.length - 2];
			temp.setLabel(label);
		}
	}
}
