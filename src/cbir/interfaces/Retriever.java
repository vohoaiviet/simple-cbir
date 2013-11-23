/*
 * Copyright (C) 2013 Justus Piater,
 * Intelligent and Interactive Systems Group,
 * University of Innsbruck, Austria.
 */
package cbir.interfaces;

import java.util.List;

import cbir.image.DescriptorType;
import cbir.image.ImageContainer;

/**
 * This is the Retriever interface.
 * 
 * @author Chris Wendler.
 * 
 */
public interface Retriever {
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
	public List<ImageContainer> search(final ImageContainer query,
			final DescriptorType type, int resultAmount);

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
	public void printResultListHTML(List<ImageContainer> results,
			DescriptorType type, String filename);

	/**
	 * Queries for an image with a specific name.
	 * 
	 * @param name
	 *            of the image.
	 * @return The image object with the given filename or null if not found.
	 */
	public ImageContainer getImageByName(String name);

	/**
	 * A getter for the database.
	 * 
	 * @return a list of Images that describe the database.
	 */
	public List<ImageContainer> getDatabase();
}
