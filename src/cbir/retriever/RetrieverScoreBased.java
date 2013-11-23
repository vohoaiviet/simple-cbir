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
package cbir.retriever;

import java.util.Comparator;
import java.util.List;

import cbir.image.DescriptorType;
import cbir.image.ImageContainer;
import cbir.interfaces.Retriever;
import cbir.interfaces.Score;

/**
 * A Retriever that uses a score function for the ranking instead of a distance
 * function. NOTE: The score based retriever is not compatible with indexing,
 * since our index structure did not improve the results.
 * 
 * @author Chris Wendler
 * 
 */

public class RetrieverScoreBased implements Retriever {
	/** The image database defined by a list of images. **/
	private List<ImageContainer> database;
	/** The score function that is used to compare the images in the database. **/
	private Score score;

	/**
	 * @param database
	 *            is a list of images describing an image database.
	 * @param score
	 *            determines which scoring function is used.
	 */
	public RetrieverScoreBased(List<ImageContainer> database, Score score) {
		this.database = database;
		this.score = score;
	}

	/**
	 * Finds the "amount" nearest neighbors of the given image.
	 * 
	 * @param image
	 *            the query image.
	 * @param type
	 *            the descriptor type of interest.
	 * @param amount
	 *            the desired amount of nearest neighbors.
	 * @return The list of the nearest neighbors.
	 */
	public List<ImageContainer> findNearestNeighbors(
			final ImageContainer image, final DescriptorType type, int amount) {
		return Utility.findNearestNeighbors(database, amount,
				new Comparator<ImageContainer>() {
					@Override
					public int compare(ImageContainer a, ImageContainer b) {
						double scoreA = score.score(image, a, type);
						double scoreB = score.score(image, b, type);
						if (scoreA > scoreB)
							return -1;
						if (scoreA < scoreB)
							return 1;
						return 0;
					}
				});
	}

	/**
	 * Performs a search for the given query image and returns the
	 * "resultAmount" best results.
	 * 
	 * @param query
	 *            the query image that is used.
	 * @param resultAmount
	 *            the desired amount of results.
	 * @return the best "resultAmount" results in a list.
	 */
	@Override
	public List<ImageContainer> search(final ImageContainer query,
			final DescriptorType type, int resultAmount) {
		return findNearestNeighbors(query, type, resultAmount);
	}

	/**
	 * Prints a list of images to a given file in html format.
	 * 
	 * @param results
	 *            the list of images to be printed.
	 * @param type
	 *            the descriptortype is also printed.
	 * @param filename
	 *            the filename of the target file.
	 */
	@Override
	public void printResultListHTML(List<ImageContainer> results,
			DescriptorType type, String filename) {
		Utility.printResultListHTML(results, type, filename);
	}

	/**
	 * Queries for an image with a specific name.
	 * 
	 * @param name
	 *            the filename of the image.
	 * @return The image object with the given filename or null if not found.
	 */
	@Override
	public ImageContainer getImageByName(String name) {
		return Utility.getImageByName(database, name);
	}

	/**
	 * A getter for the database.
	 * 
	 * @return a list of Images that describe the database.
	 */
	@Override
	public List<ImageContainer> getDatabase() {
		return database;
	}

	public void setDatabase(List<ImageContainer> database) {
		this.database = database;
	}
}
