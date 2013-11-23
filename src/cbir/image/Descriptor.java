/*
 * Copyright (C) 2013 Justus Piater,
 * Intelligent and Interactive Systems Group,
 * University of Innsbruck, Austria.
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
