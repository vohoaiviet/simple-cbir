package rf;

import java.util.List;

import cbir.Utils;
import cbir.image.DescriptorType;
import cbir.image.Image;

public class Utility {
	
	public enum Normalization {GAUSSIAN, GAUSSIAN_0to1, JUSTUS, IDF};
	
	public static void normalizeDescriptors(List<Image> database, DescriptorType descriptorOfInterest, Normalization type){
		double [] means = calculateMeans(database, descriptorOfInterest);
		double [] deviations;

		switch(type){
		case GAUSSIAN:
			deviations = calculateDeviations(database, descriptorOfInterest, means);
			normalizeDescriptors(database,descriptorOfInterest,means,deviations);
			break;
		case GAUSSIAN_0to1:
			deviations = calculateDeviations(database, descriptorOfInterest, means);
			normalizeDescriptorsPositive(database,descriptorOfInterest,means,deviations);
			break;
		case JUSTUS:
			deviations = calculateVariance(database, descriptorOfInterest, means);
			normalizeDescriptorsJustus(database,descriptorOfInterest,means,deviations);
			break;
		case IDF:
			deviations = calculateDeviations(database, descriptorOfInterest, means);
			
			break;
		}
	}
	
	/**
	 * normalizes all descriptors of the given type to ??
	 * @param database
	 * @param descriptorOfInterest
	 * @param means
	 * @param deviations
	 */
	public static void normalizeDescriptorsJustus(List<Image> database, DescriptorType descriptorOfInterest, double [] means, double [] variance){
		for(Image curr: database){
			double [] descriptor = curr.getDescriptor(descriptorOfInterest).getValues();
			for(int i = 0; i<descriptor.length; i++){
				if(variance[i]!=0)
					descriptor[i] = (descriptor[i]-means[i])/(variance[i]);
				else
					descriptor[i] = 1;
			}
			curr.getDescriptor(descriptorOfInterest).setValues(descriptor);
		}
	}
	
	/**
	 * normalizes all descriptors of the given type to [-1;1]
	 * @param database
	 * @param descriptorOfInterest
	 * @param means
	 * @param deviations
	 */
	public static void normalizeDescriptors(List<Image> database, DescriptorType descriptorOfInterest, double [] means, double [] deviations){
		for(Image curr: database){
			double [] descriptor = curr.getDescriptor(descriptorOfInterest).getValues();
			for(int i = 0; i<descriptor.length; i++){
				if(deviations[i]!=0)
					descriptor[i] = (descriptor[i]-means[i])/(3*deviations[i]);
				else
					descriptor[i] = 1;
			}
			curr.getDescriptor(descriptorOfInterest).setValues(descriptor);
		}
	}
	
	/**
	 * normalizes all descriptors of the given type to [0;1]
	 * @param database
	 * @param descriptorOfInterest
	 * @param means
	 * @param deviations
	 */
	public static void normalizeDescriptorsPositive(List<Image> database, DescriptorType descriptorOfInterest, double [] means, double [] deviations){
		normalizeDescriptors(database,descriptorOfInterest,means,deviations);
		for(Image curr: database){
			double [] descriptor = curr.getDescriptor(descriptorOfInterest).getValues();
			for(int i = 0; i<descriptor.length; i++)
				descriptor[i] = (descriptor[i]+1.d)/2.d;
			curr.getDescriptor(descriptorOfInterest).setValues(descriptor);
		}
		
	}
	
	
	/**
	 * performs the conversion from descriptors to weight vectors explained in
	 * the paper
	 */
	public static void convertToWeights(List<Image> database, DescriptorType descriptorOfInterest, double [] means, double [] deviations) {
		for (Image curr : database) {
			double[] descriptor = curr.getDescriptor(descriptorOfInterest)
					.getValues();
			for (int i = 0; i < descriptor.length; i++) {
				if (means[i] == 0)
					descriptor[i] = 0;
				else
					descriptor[i] = (descriptor[i] / means[i])
							* (Utils.LogBaseX(deviations[i] + 2.d, 2.d));
			}
			curr.getDescriptor(descriptorOfInterest).setValues(descriptor);
		}
	}
	
	/**
	 * calculates the mean for every feature of a feature vector among all images in the database
	 * @param database the image database
	 * @param descriptorOfInterest the type of the feature vector
	 * @return the array of means
	 */
	public static double[] calculateMeans(List<Image> database, DescriptorType descriptorOfInterest){
		double [] means = new double[database.get(0).getDescriptor(descriptorOfInterest).getValues().length];
		for(Image curr: database){
			double [] descriptor = curr.getDescriptor(descriptorOfInterest).getValues();
			for(int i = 0; i<descriptor.length; i++){
				means[i] += descriptor[i];
			}
		}
		for(int i = 0; i<means.length; i++){
			means[i] /= database.size();
		}
		return means;
	}
	
	/**
	 * calculates the deviation for every feature of a feature vector among all images in the database
	 * @param database the image database
	 * @param descriptorOfInterest the type of the feature vector
	 * @param means the array of means which is necessary for the computation of deviations
	 * @return the array of deviations
	 */
	public static double[] calculateDeviations(List<Image> database, DescriptorType descriptorOfInterest, double [] means){
		double [] deviations = new double[means.length];
		for(Image curr: database){
			double [] descriptor = curr.getDescriptor(descriptorOfInterest).getValues();
			for(int i = 0; i<descriptor.length; i++){
				deviations[i] += Math.pow(descriptor[i]-means[i],2);//refactor a*a
			}
		}
		for(int i = 0; i<deviations.length; i++){
			deviations[i] /= database.size();
			deviations[i] = Math.sqrt(deviations[i]);
		}
		return deviations;
	}
	
	/**
	 * calculates the variance for every feature of a feature vector among all images in the database
	 * @param database the image database
	 * @param descriptorOfInterest the type of the feature vector
	 * @param means the array of means which is necessary for the computation of deviations
	 * @return the array of deviations
	 */
	public static double[] calculateVariance(List<Image> database, DescriptorType descriptorOfInterest, double [] means){
		double [] deviations = new double[means.length];
		for(Image curr: database){
			double [] descriptor = curr.getDescriptor(descriptorOfInterest).getValues();
			for(int i = 0; i<descriptor.length; i++){
				deviations[i] += Math.pow(descriptor[i]-means[i],2);//refactor a*a
			}
			for(int i = 0; i<deviations.length; i++){
				deviations[i] /= database.size();
			}
		}
		return deviations;
	}
	
	public static void addImagesToList(List<Image> list, List<Image> toAdd){
		for(Image curr: toAdd)
			if(!list.contains(curr))
				list.add(curr);
	}

}
