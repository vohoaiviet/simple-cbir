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
 * A class which implements an image (jpeg...). An image has a filename, a list
 * of descriptors, lists of positive and negative images marked during RF and an
 * optional label.
 * 
 * @author Matej Stanic
 * @author Chris Wendler
 * 
 */
public class ImageContainer implements Serializable {
	private static final long serialVersionUID = -3920208288841347100L;
	/** The path of the image. */
	private final String filename;
	/** A hash map of the descriptors of the image. */
	private HashMap<DescriptorType, Descriptor> descriptors;
	/** List of positive images marked during RF */
	private List<ImageContainer> positives = new LinkedList<ImageContainer>();
	/** List of negative images marked during RF */
	private List<ImageContainer> negatives = new LinkedList<ImageContainer>();
	/** Label of a image when annotated databases are used */
	private String label = null;
	/**
	 * A list to remember the order in which the descriptors have been merged to
	 * be able to compute the weights.
	 */
	private List<DescriptorType> order = new LinkedList<DescriptorType>();

	/**
	 * Constructor of the Image class. Adds descriptors if specified.
	 */
	public ImageContainer(String filename, Descriptor... histograms) {
		super();
		this.filename = filename;
		descriptors = new HashMap<DescriptorType, Descriptor>();
		for (int i = 0; i < histograms.length; i++)
			addDescriptor(histograms[i]);
	}

	/**
	 * Adds a descriptor to descriptor list of an image. (always add descriptors
	 * using this function to garantuee that the merged descriptor is correct)
	 * 
	 * @param descriptor
	 *            The descriptor to be added.
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

	/**
	 * Makes a deep copy of an object via "this"
	 * 
	 * @return the deep copy
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public ImageContainer deepCopy() throws IOException, ClassNotFoundException {
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
		ImageContainer deepCopy = (ImageContainer) inStream.readObject();
		return deepCopy;
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

	public List<DescriptorType> getOrder() {
		return order;
	}

	public void setOrder(List<DescriptorType> order) {
		this.order = order;
	}

	public List<ImageContainer> getPositives() {
		return positives;
	}

	public void setPositives(List<ImageContainer> positives) {
		this.positives = positives;
	}

	public List<ImageContainer> getNegatives() {
		return negatives;
	}

	public void setNegatives(List<ImageContainer> negatives) {
		this.negatives = negatives;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
