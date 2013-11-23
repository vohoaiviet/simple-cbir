/*
 * Copyright (C) 2013 Justus Piater,
 * Intelligent and Interactive Systems Group,
 * University of Innsbruck, Austria.
 */
package rf;

import java.util.List;

import cbir.Utils;
import cbir.image.DescriptorType;
import cbir.image.ImageContainer;

/**
 * This is a Utility class for all RF methods and provides methods to normalize
 * descriptors to calculate mean, variance and standard deviation and so on.
 * 
 * @author Chris Wendler
 * 
 */
public class Utility {
	/**
	 * GAUSSIAN: apply gaussian normalization by dividing each component of the
	 * corresponding descriptor by 3 times the standard deviation.
	 * GAUSSIAN_0to1: the same as GAUSSIAN except that the values are normalized
	 * to the interval [0,1]. JUSTUS: normalizes the descriptors by dividing
	 * each entry by the corresponding variance. IDF: denotes the inverse
	 * document frequency normalization for images.
	 * 
	 * @author Chris Wendler
	 */
	public enum Normalization {
		GAUSSIAN, GAUSSIAN_0to1, JUSTUS, IDF
	};

	/**
	 * Normalizes the descriptors of given type and given database.
	 * 
	 * @param database
	 *            is an image list containing.
	 * @param descriptorOfInterest
	 *            denotes for which descriptortype the operation is applied.
	 * @param type
	 *            denotes the type of normalization: GAUSSIAN: apply gaussian
	 *            normalization by dividing each component of the corresponding
	 *            descriptor by 3 times the standard deviation. GAUSSIAN_0to1:
	 *            the same as GAUSSIAN except that the values are normalized to
	 *            the interval [0,1]. JUSTUS: normalizes the descriptors by
	 *            dividing each entry by the corresponding variance. IDF:
	 *            denotes the inverse document frequency normalization for
	 *            images.
	 */
	public static void normalizeDescriptors(List<ImageContainer> database,
			DescriptorType descriptorOfInterest, Normalization type) {
		double[] means = calculateMeans(database, descriptorOfInterest);
		double[] deviations;

		switch (type) {
		case GAUSSIAN:
			deviations = calculateDeviations(database, descriptorOfInterest,
					means);
			normalizeDescriptors(database, descriptorOfInterest, means,
					deviations);
			break;
		case GAUSSIAN_0to1:
			deviations = calculateDeviations(database, descriptorOfInterest,
					means);
			normalizeDescriptorsPositive(database, descriptorOfInterest, means,
					deviations);
			break;
		case JUSTUS:
			deviations = calculateVariance(database, descriptorOfInterest,
					means);
			normalizeDescriptorsJustus(database, descriptorOfInterest, means,
					deviations);
			break;
		case IDF:
			deviations = calculateDeviations(database, descriptorOfInterest,
					means);

			break;
		}
	}

	/**
	 * normalizes all descriptors of the given type to by dividing it's
	 * components by the component variance.
	 * 
	 * @param database
	 *            a list of images.
	 * @param descriptorOfInterest
	 *            the descriptortype of interest.
	 * @param means
	 *            the means of the different features in the database.
	 * @param variance
	 *            the variances of the different features in the database.
	 */
	public static void normalizeDescriptorsJustus(
			List<ImageContainer> database, DescriptorType descriptorOfInterest,
			double[] means, double[] variance) {
		for (ImageContainer curr : database) {
			double[] descriptor = curr.getDescriptor(descriptorOfInterest)
					.getValues();
			for (int i = 0; i < descriptor.length; i++) {
				if (variance[i] != 0)
					descriptor[i] = (descriptor[i] - means[i]) / (variance[i]);
				else
					descriptor[i] = 1;
			}
			curr.getDescriptor(descriptorOfInterest).setValues(descriptor);
		}
	}

	/**
	 * normalizes all descriptors of the given type to [-1;1]
	 * 
	 * @param database
	 *            a list of images.
	 * @param descriptorOfInterest
	 *            the descriptortype of interest.
	 * @param means
	 *            the means of the different features in the database.
	 * @param deviations
	 *            the standard deviations of the different features in the
	 *            database.
	 */
	public static void normalizeDescriptors(List<ImageContainer> database,
			DescriptorType descriptorOfInterest, double[] means,
			double[] deviations) {
		for (ImageContainer curr : database) {
			double[] descriptor = curr.getDescriptor(descriptorOfInterest)
					.getValues();
			for (int i = 0; i < descriptor.length; i++) {
				if (deviations[i] != 0)
					descriptor[i] = (descriptor[i] - means[i])
							/ (3 * deviations[i]);
				else
					descriptor[i] = 1;
			}
			curr.getDescriptor(descriptorOfInterest).setValues(descriptor);
		}
	}

	/**
	 * normalizes all descriptors of the given type to [0;1]
	 * 
	 * @param database
	 *            a list of images.
	 * @param descriptorOfInterest
	 *            the descriptortype of interest.
	 * @param means
	 *            the means of the different features in the database.
	 * @param deviations
	 *            the standard deviations of the different features in the
	 *            database.
	 */
	public static void normalizeDescriptorsPositive(
			List<ImageContainer> database, DescriptorType descriptorOfInterest,
			double[] means, double[] deviations) {
		normalizeDescriptors(database, descriptorOfInterest, means, deviations);
		for (ImageContainer curr : database) {
			double[] descriptor = curr.getDescriptor(descriptorOfInterest)
					.getValues();
			for (int i = 0; i < descriptor.length; i++)
				descriptor[i] = (descriptor[i] + 1.d) / 2.d;
			curr.getDescriptor(descriptorOfInterest).setValues(descriptor);
		}

	}

	/**
	 * Performs the conversion from descriptors to weight vectors explained in
	 * the paper
	 * 
	 * @param database
	 *            a list of images.
	 * @param descriptorOfInterest
	 *            the descriptortype of interest.
	 * @param means
	 *            the means of the different features in the database.
	 * @param deviations
	 *            the standard deviations of the different features in the
	 *            database.
	 */
	public static void convertToWeights(List<ImageContainer> database,
			DescriptorType descriptorOfInterest, double[] means,
			double[] deviations) {
		for (ImageContainer curr : database) {
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
	 * Calculates the mean for every feature of a feature vector among all
	 * images in the database.
	 * 
	 * @param database
	 *            an image list.
	 * @param descriptorOfInterest
	 *            the descriptortype of interest.
	 * @return the array of means of the different features.
	 */
	public static double[] calculateMeans(List<ImageContainer> database,
			DescriptorType descriptorOfInterest) {
		double[] means = new double[database.get(0)
				.getDescriptor(descriptorOfInterest).getValues().length];
		for (ImageContainer curr : database) {
			double[] descriptor = curr.getDescriptor(descriptorOfInterest)
					.getValues();
			for (int i = 0; i < descriptor.length; i++) {
				means[i] += descriptor[i];
			}
		}
		for (int i = 0; i < means.length; i++) {
			means[i] /= database.size();
		}
		return means;
	}

	/**
	 * Calculates the deviation for every feature of a feature vector among all
	 * images in the database.
	 * 
	 * @param database
	 *            an image list.
	 * @param descriptorOfInterest
	 *            the descriptortype of interest.
	 * @param means
	 *            the array of means which is necessary for the computation of
	 *            deviations.
	 * @return the array of deviations of the different features.
	 */
	public static double[] calculateDeviations(List<ImageContainer> database,
			DescriptorType descriptorOfInterest, double[] means) {
		double[] deviations = new double[means.length];
		for (ImageContainer curr : database) {
			double[] descriptor = curr.getDescriptor(descriptorOfInterest)
					.getValues();
			for (int i = 0; i < descriptor.length; i++) {
				deviations[i] += Math.pow(descriptor[i] - means[i], 2);// refactor
																		// a*a
			}
		}
		for (int i = 0; i < deviations.length; i++) {
			deviations[i] /= database.size();
			deviations[i] = Math.sqrt(deviations[i]);
		}
		return deviations;
	}

	/**
	 * Calculates the variance for every feature of a feature vector among all
	 * images in the database.
	 * 
	 * @param database
	 *            an image list.
	 * @param descriptorOfInterest
	 *            the descriptortype of interest.
	 * @param means
	 *            the array of means which is necessary for the computation of
	 *            deviations.
	 * @return the array of variances of the different features.
	 */
	public static double[] calculateVariance(List<ImageContainer> database,
			DescriptorType descriptorOfInterest, double[] means) {
		double[] deviations = new double[means.length];
		for (ImageContainer curr : database) {
			double[] descriptor = curr.getDescriptor(descriptorOfInterest)
					.getValues();
			for (int i = 0; i < descriptor.length; i++) {
				deviations[i] += Math.pow(descriptor[i] - means[i], 2);// refactor
																		// a*a
			}
			for (int i = 0; i < deviations.length; i++) {
				deviations[i] /= database.size();
			}
		}
		return deviations;
	}

	/**
	 * A utility function that combines to image lists to one, every Image
	 * should occur only once in the resulting list.
	 * 
	 * @param list
	 *            the list where the new images should be added.
	 * @param toAdd
	 *            the list of images that should be added.
	 */
	public static void addImagesToList(List<ImageContainer> list,
			List<ImageContainer> toAdd) {
		for (ImageContainer curr : toAdd)
			if (!list.contains(curr))
				list.add(curr);
	}

}
