/*
 * Copyright (C) 2013 Justus Piater,
 * Intelligent and Interactive Systems Group,
 * University of Innsbruck, Austria.
 */
/*
 * This file is part of simple-cbir.
 *
 *  simple-cbir is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  simple-cbir is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with simple-cbir.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Diese Datei ist Teil von simple-cbir.
 *
 *  simple-cbir ist Freie Software: Sie können es unter den Bedingungen
 *  der GNU General Public License, wie von der Free Software Foundation,
 *  Version 3 der Lizenz oder (nach Ihrer Wahl) jeder späteren
 *  veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 *  simple-cbir wird in der Hoffnung, dass es nützlich sein wird, aber
 *  OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite
 *  Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
 *  Siehe die GNU General Public License für weitere Details.
 *
 *  Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 *  Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
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
	 * @return a list of images with their descriptors.
	 * @throws DocumentException
	 */
	public List<ImageContainer> parseXMLFile(File file)
			throws DocumentException {

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
