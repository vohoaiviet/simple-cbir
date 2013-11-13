package cbir.image;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import cbir.Utils;

/**
 * A class which implements an image (jpeg...). An image has a filename, a Color
 * Histogram descriptor and a MPEG-EHD descriptor.
 * 
 * @author Stanic Matej
 * 
 */
public class Image implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3920208288841347100L;
	private final String filename;
	private HashMap<DescriptorType, Descriptor> descriptors;
	private HashMap<String, Double> scores;
	private List<Image> positives = new LinkedList<Image>(),
			negatives = new LinkedList<Image>();
	private String label = null;

	/**
	 * a list to remember the order in which the descriptors have been merged to
	 * be able to compute the weights.
	 */
	private List<DescriptorType> order = new LinkedList<DescriptorType>();

	public Image(String filename, Descriptor... histograms) {
		super();
		this.filename = filename;
		descriptors = new HashMap<DescriptorType, Descriptor>();
		scores = new HashMap<String,Double>();
		for (int i = 0; i < histograms.length; i++)
			addDescriptor(histograms[i]);
	}

	/**
	 * always add descriptors using this function to garantuee that the merged
	 * descriptor is correct
	 * 
	 * @param descriptor
	 */
	public void addDescriptor(Descriptor descriptor) {
		if (descriptors.size() > 0) {
			double[] merged = null;
			// double maxValue = descriptor.getMaxValue();
			merged = Utils.concat(descriptors.get(DescriptorType.MERGED)
					.getValues(), descriptor.getValues());
			descriptors.put(DescriptorType.MERGED, new Descriptor(
					DescriptorType.MERGED, merged, 1));
		} else
			descriptors.put(DescriptorType.MERGED, descriptor);

		descriptors.put(descriptor.getType(), descriptor);
		order.add(descriptor.getType());
	}

	public String getFilename() {
		return filename;
	}

	public HashMap<DescriptorType, Descriptor> getDescriptors() {
		return descriptors;
	}

	public void setDescriptors(HashMap<DescriptorType, Descriptor> descriptors) {
		this.descriptors = descriptors;
	}

	public Descriptor getDescriptor(DescriptorType type) {
		return descriptors.get(type);
	}

	public Descriptor getColorHisto() {
		return descriptors.get(DescriptorType.COLOR_HISTO);
	}

	public Descriptor getEdgeHisto() {
		return descriptors.get(DescriptorType.MPEG_EHD);
	}

	public List<DescriptorType> getOrder() {
		return order;
	}

	public void setOrder(List<DescriptorType> order) {
		this.order = order;
	}

	public List<Image> getPositives() {
		return positives;
	}

	public void setPositives(List<Image> positives) {
		this.positives = positives;
	}

	public List<Image> getNegatives() {
		return negatives;
	}

	public void setNegatives(List<Image> negatives) {
		this.negatives = negatives;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Image deepCopy() throws IOException, ClassNotFoundException {
		// ObjectOutputStream erzeugen
		ByteArrayOutputStream bufOutStream = new ByteArrayOutputStream();
		ObjectOutputStream outStream = new ObjectOutputStream(bufOutStream);
		// Objekt im byte-Array speichern
		outStream.writeObject(this);
		outStream.close();
		// Pufferinhalt abrufen
		byte[] buffer = bufOutStream.toByteArray();
		// ObjectInputStream erzeugen
		ByteArrayInputStream bufInStream = new ByteArrayInputStream(buffer);
		ObjectInputStream inStream = new ObjectInputStream(bufInStream);
		// Objekt wieder auslesen
		Image deepCopy = (Image) inStream.readObject();
		return deepCopy;
	}
	
	public Double getScore(String query) {
		return scores.get(query);
	}
	
	public void putScore(String query, Double score) {
		scores.put(query, score);
	}
	
	

}
