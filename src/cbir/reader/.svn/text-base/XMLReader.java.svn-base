package cbir.reader;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cbir.image.Descriptor;
import cbir.image.DescriptorType;
import cbir.image.Image;

/**
 * A class which reads the name of an image and its descriptors from a given XML
 * file.
 * 
 * @author Stanic Matej
 * 
 */
public class XMLReader {
	private final List<Image> imageList = new LinkedList<Image>();

	/**
	 * Parses image information from a given XML file and returns a list of
	 * images with their descriptors (MPEG_EHD & Color Histogram)
	 * 
	 * @param file
	 *            - the given XML file
	 * @returns a list of images with their descriptors
	 * @throws DocumentException
	 */
	public List<Image> parseXMLFile(File file) throws DocumentException {

		// Read XML file
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);

		// Get first Element
		Element rootElement = document.getRootElement();

		// Iterate + get image paths + descriptors

		// outer loop (Info, Data)
		for (Iterator iter = rootElement.elementIterator(); iter.hasNext();) {
			Element element = (Element) iter.next();

			// inner loop (Image Data)
			for (Iterator innerIter = element.elementIterator(); innerIter
					.hasNext();) {
				Element innerElement = (Element) innerIter.next();

				// data loop (Filename, EHD, RGB)
				for (Iterator dataIter = innerElement.elementIterator(); dataIter
						.hasNext();) {

					// get Filename
					Element dataElement = (Element) dataIter.next();
					String filename = new String(dataElement.getText());
					List<Descriptor> descriptors = new LinkedList<Descriptor>();
					while (dataIter.hasNext()) {
						// get descriptors
						dataElement = (Element) dataIter.next();
						if (dataElement.getName().equals("EHD"))
							descriptors.add(readDescriptor(dataElement));
					}

					// construct Image and save in list
					Image image = new Image(filename,
							descriptors.toArray(new Descriptor[descriptors
									.size()]));
					imageList.add(image);
				}
			}
		}
		return imageList;

	}

	public List<Image> getImageList() {
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

			// Color Histo
		} else if (descriptorType.equals("RGB")) {

			StringTokenizer ColTokenizer = new StringTokenizer(
					dataElement.getTextTrim(), "%");
			int colCount = ColTokenizer.countTokens();
			double[] colValues = new double[colCount];

			for (int i = 0; i < colCount; i++)
				colValues[i] = Integer.parseInt(ColTokenizer.nextToken());
			descriptor = new Descriptor(DescriptorType.COLOR_HISTO, colValues,
					255.d);

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
