package cbir.reader;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cbir.image.Descriptor;
import cbir.image.DescriptorType;
import cbir.image.ImageContainer;

/**
 * A class which reads the name of an image and its descriptors from a given XML
 * file extracted by the tool img(rummager).
 * 
 * @author Matej Stanic
 * 
 */
public class XMLReader {
	/** The image list that will be returned. */
	private final List<ImageContainer> imageList = new LinkedList<ImageContainer>();

	/**
	 * Parses image information from a given XML file and returns a list of
	 * images with their descriptors (only MPEG_EHD and CEDD extracted by
	 * img(rummager)).
	 * 
	 * @param file
	 *            The given img(rummager) XML file.
	 * @returns a list of images with their descriptors.
	 * @throws DocumentException
	 */
	public List<ImageContainer> parseXMLFile(File file) throws DocumentException {

		// Read XML file
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);

		// Get first Element
		Element rootElement = document.getRootElement();

		// Iterate + get image paths + descriptors

		// outer loop (Info, Data)
		for (@SuppressWarnings("rawtypes")
		Iterator iter = rootElement.elementIterator(); iter.hasNext();) {
			Element element = (Element) iter.next();

			// inner loop (Image Data)
			for (@SuppressWarnings("rawtypes")
			Iterator innerIter = element.elementIterator(); innerIter.hasNext();) {
				Element innerElement = (Element) innerIter.next();

				// data loop (Filename, EHD, RGB)
				for (@SuppressWarnings("rawtypes")
				Iterator dataIter = innerElement.elementIterator(); dataIter
						.hasNext();) {

					// get Filename
					Element dataElement = (Element) dataIter.next();
					String filename = new String(dataElement.getText());
					List<Descriptor> descriptors = new LinkedList<Descriptor>();
					while (dataIter.hasNext()) {
						// get descriptors
						dataElement = (Element) dataIter.next();
						if (dataElement.getName().equals("EHD")
								|| dataElement.getName().equals("CEDD"))
							descriptors.add(readDescriptor(dataElement));
					}

					// construct Image and save in list
					ImageContainer image = new ImageContainer(filename,
							descriptors.toArray(new Descriptor[descriptors
									.size()]));
					imageList.add(image);
				}
			}
		}
		return imageList;

	}

	public List<ImageContainer> getImageList() {
		return imageList;
	}

	public Descriptor readDescriptor(Element dataElement) {
		String descriptorType = dataElement.getName();
		Descriptor descriptor = null;

		// EHD
		if (descriptorType.equals("EHD")) {

			char[] ehdChars = new char[80];
			dataElement.getTextTrim().getChars(0, 80, ehdChars, 0);
			double[] ehdValues = new double[ehdChars.length];
			for (int i = 0; i < ehdChars.length; i++)
				ehdValues[i] = Integer
						.parseInt(Character.toString(ehdChars[i]));
			descriptor = new Descriptor(DescriptorType.MPEG_EHD, ehdValues, 9.d);

			// CEDD
		} else if (descriptorType.equals("CEDD")) {
			char[] ceddChars = new char[144];
			dataElement.getTextTrim().getChars(0, 144, ceddChars, 0);
			double[] ceddValues = new double[ceddChars.length];
			for (int i = 0; i < ceddChars.length; i++)
				ceddValues[i] = Integer.parseInt(Character
						.toString(ceddChars[i]));
			descriptor = new Descriptor(DescriptorType.CEDD, ceddValues, 9.d);
		}

		return descriptor;

	}

}
