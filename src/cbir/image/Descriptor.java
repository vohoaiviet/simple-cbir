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

import java.io.Serializable;
import java.util.Arrays;

/**
 * A class which implements a image descriptor. A descriptor has a type (Color
 * Histogram, MPEG-EHD...) and the actual values computed from an image.
 * 
 * @author Matej Stanic
 * 
 */
public class Descriptor implements Serializable {

	private static final long serialVersionUID = -7766751268769162177L;
	/** The type of the descriptor. */
	private final DescriptorType type;
	/** The descriptor itself (the values). */
	private double[] values;
	/** The maximum value of a descriptor that is possible. */
	private final double maxValue;

	/**
	 * Constructor.
	 */
	public Descriptor(DescriptorType type, double[] values, double maxValue) {
		super();
		this.type = type;
		this.values = values;
		this.maxValue = maxValue;
	}

	public DescriptorType getType() {
		return type;
	}

	public double[] getValues() {
		return values;
	}

	public void setValues(double[] values) {
		this.values = values;
	}

	public double getMaxValue() {
		return maxValue;
	}

	@Override
	public String toString() {
		return "Descriptor [type=" + type + ", values="
				+ Arrays.toString(values) + ", maxValue=" + maxValue + "]";
	}

}
