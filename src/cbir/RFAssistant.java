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

import cbir.image.ImageContainer;

/**
 * This class provides a RFAssistant that is supposed to help the poor guy who
 * has to evaluate different relevance feedback mechanisms by hand. The
 * RFAssistant maintains lists of positve images and negative images for a
 * specific query that are already known and corrects the user if he is tired
 * and does inconsistent marking.
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
	 * 
	 * @param positives
	 *            is the list of known positive images for a query image.
	 * @param negatives
	 *            is the list of known negative images for a query image.
	 */
	public RFAssistant(List<String> positives, List<String> negatives) {
		this.positives = positives;
		this.negatives = negatives;
	}

	/**
	 * Creates a RFAssistant object from a given file containing the known
	 * marked images.
	 * 
	 * @param filename
	 *            the filename of the file containing the filenames of the
	 *            marked images.
	 * @return a RF assistant object is returned.
	 * @throws IOException
	 *             if the file is not found.
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
	 * 
	 * @param filename
	 *            the name of the outputfile.
	 * @param query
	 *            the query image of which the marked positives/negatives should
	 *            be stored.
	 * @throws IOException
	 *             is thrown if there is a problem with creating the file.
	 */
	public static void printQueryHits(String filename, ImageContainer query)
			throws IOException {
		List<String> positives = new LinkedList<String>();
		List<String> negatives = new LinkedList<String>();
		File file = new File(filename);
		if (file.exists()) {
			RFAssistant assi = createRFAssistant(filename);
			positives = assi.getPositives();
			negatives = assi.getNegatives();
		}
		for (ImageContainer curr : query.getPositives())
			if (!positives.contains(curr.getFilename()))
				positives.add(curr.getFilename());
		for (ImageContainer curr : query.getNegatives())
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
	 * Revises the new positive/negative labeled images form the user, when
	 * there are inconsistencies compared to the already known lists these
	 * inconsistencies get eliminated.
	 * 
	 * @param positives
	 * @param negatives
	 */
	public void revisePositivesAndNegatives(List<ImageContainer> positives,
			List<ImageContainer> negatives) {
		Iterator<ImageContainer> it = positives.iterator();
		int falsePositives = 0, falseNegatives = 0;
		while (it.hasNext()) {
			ImageContainer curr = it.next();
			if (this.negatives.contains(curr.getFilename())) {
				falsePositives++;
				negatives.add(curr);
				it.remove();
			}
		}
		it = negatives.iterator();
		while (it.hasNext()) {
			ImageContainer curr = it.next();
			if (this.positives.contains(curr.getFilename())) {
				falseNegatives++;
				positives.add(curr);
				it.remove();
			}
		}
		System.out.println("corrected " + (falsePositives + falseNegatives)
				+ " mistakes: fp=" + falsePositives + " fn=" + falseNegatives);
	}

	public List<String> getPositives() {
		return positives;
	}

	public List<String> getNegatives() {
		return negatives;
	}
}
