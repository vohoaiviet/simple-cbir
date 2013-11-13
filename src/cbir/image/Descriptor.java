package cbir.image;

import java.io.Serializable;
import java.util.Arrays;

/**
 * A class which implements a image descriptor. A descriptor has a type (Color
 * Histogram, MPEG-EHD...) and the actual values computed from an image.
 * 
 * @author Stanic Matej
 * 
 */
public class Descriptor implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7766751268769162177L;
	private final DescriptorType type;
	private double[] values;
	private final double maxValue;

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
	
	public void setValues(double [] values){
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
