package cbir.interfaces;

import java.util.List;

import cbir.image.DescriptorType;
import cbir.image.Image;

public interface Retriever {
	/**
	 * perform a search for all descriptors...
	 * 
	 * @param query
	 * @param metric
	 * @param resultAmount
	 * @return the top resultAmount results
	 */
	public List<Image> search(final Image query,
			final DescriptorType type, int resultAmount);

	public void printResultListHTML(List<Image> results, DescriptorType type,
			String filename);
	
	public Image getImageByName(String name);
	
	public List<Image> getDatabase();
}
