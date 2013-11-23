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
 *  simple-cbir ist Freie Software: Sie k�nnen es unter den Bedingungen
 *  der GNU General Public License, wie von der Free Software Foundation,
 *  Version 3 der Lizenz oder (nach Ihrer Wahl) jeder sp�teren
 *  ver�ffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 *  simple-cbir wird in der Hoffnung, dass es n�tzlich sein wird, aber
 *  OHNE JEDE GEW�HELEISTUNG, bereitgestellt; sogar ohne die implizite
 *  Gew�hrleistung der MARKTF�HIGKEIT oder EIGNUNG F�R EINEN BESTIMMTEN ZWECK.
 *  Siehe die GNU General Public License f�r weitere Details.
 *
 *  Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 *  Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package cbir.interfaces;

import cbir.image.DescriptorType;
import cbir.image.ImageContainer;

/**
 * This is the Score interface.
 * 
 * @author Chris Wendler
 * 
 */
public interface Score {
	/**
	 * Calculates a score for the given image.
	 * 
	 * @param query
	 *            the query vector containing all positively and negatively
	 *            marked images.
	 * @param image
	 *            the image for which the score is computed.
	 * @param type
	 *            the type of descriptor which is considered in the score
	 *            computation.
	 * @return the score of the image.
	 */
	public double score(ImageContainer query, final ImageContainer image,
			final DescriptorType type);
}
