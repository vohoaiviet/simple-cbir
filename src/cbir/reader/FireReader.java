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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import cbir.Utils;
import cbir.image.Descriptor;
import cbir.image.DescriptorType;
import cbir.image.ImageContainer;

/**
 * A very simple reader that is able to read histograms of the given format: +
 * (optional) The histograms can contain a delimiter "counter" followed by one
 * double value, if the "counter" delimiter is used it has to be located in
 * front of the data part. * The histogram files have to be compressed using
 * gzip. * The histograms have to contain the delimiter "data" followed by an
 * arbitrary amount of double values separated by spaces. * The descriptors have
 * to be located in the image directory.
 * 
 * @author Chris Wendler
 */

public class FireReader {

	/**
	 * Reads all the corresponding descriptors from a specific type into the
	 * according image data-structure. note: the descriptors have to be located
	 * in the same directory as the images.
	 * 
	 * @param images
	 *            a list of images that can be generated using the XMLReader.
	 * @param type
	 *            the type determines where the descriptors get stored in the
	 *            image data-structure.
	 * @throws IOException
	 *             when a descriptor file is not found.
	 */
	public void readDescriptors(List<ImageContainer> images, DescriptorType type)
			throws IOException {
		String suffix = Utils.getSuffix(type);
		for (ImageContainer curr : images)
			curr.addDescriptor(readDescriptorFile(curr.getFilename() + suffix,
					type));
	}

	/**
	 * Reads a single descriptor from a given file.
	 * 
	 * @param file
	 *            is the filename of the descriptor file.
	 * @param type
	 *            the type determines where the descriptors get stored in the
	 *            image data-structure.
	 * @throws IOException
	 *             when a descriptor file is not found.
	 */
	public Descriptor readDescriptorFile(String file, DescriptorType type)
			throws IOException {
		List<Double> values = null;
		double maxValue = 1;
		boolean readMaxValue = false;

		InputStream fileStream = new FileInputStream(file);
		InputStream gzipStream = new GZIPInputStream(fileStream);
		Reader decoder = new InputStreamReader(gzipStream);
		BufferedReader reader = new BufferedReader(decoder);
		String line;
		/* parse color histo */
		while ((line = reader.readLine()) != null) {
			String[] tokens = line.split("\\s+");
			for (String token : tokens) {
				if (token.equals("counter")) {
					readMaxValue = true;
					continue;
				}
				if (token.equals("data")) {
					values = new LinkedList<Double>();
					continue;
				}
				if (readMaxValue) {
					readMaxValue = false;
					maxValue = Double.parseDouble(token);
				}
				if (values != null)
					values.add(Double.parseDouble(token));
			}
		}
		reader.close();

		return new Descriptor(type, Utils.doubleFromDouble(values), maxValue);
	}
}
