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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.dom4j.DocumentException;

import rf.Utility;
import rf.Utility.Normalization;
import rf.bayesian.Bayesian;
import cbir.image.DescriptorType;
import cbir.image.ImageContainer;
import cbir.interfaces.Metric;
import cbir.interfaces.RelevanceFeedback;
import cbir.metric.WeightedEuclidean;
import cbir.reader.FireReader;
import cbir.reader.XMLReader;
import cbir.retriever.RetrieverDistanceBased;

/**
 * A class which simulates an automatic CBIR test.
 * 
 * @author Matej Stanic & Chris Wendler
 * 
 */
public class AutomatedRFTest {
	/** The path of the output-file can be specified here. **/
	public static String outputfile = "C:\\Users\\Stanic\\Desktop\\CorelDB\\automatic_corel_cedd.html";
	/** The number of random queries that should be performed. **/
	public static int numOfQueries = 5;
	/** The number of RF iterations is specified here. **/
	public static int numOfRFIterations = 4;
	/** The number of results that are considered. **/
	public static int numOfResults = 20;
	/**
	 * This boolean flag denotes if new random indices should be generated or
	 * not.
	 **/
	public static boolean newRandoms = true;
	/**
	 * If newRandoms == false you can specify a file where the randoms should be
	 * read from. Else the produced randoms will get stored in this file.
	 **/
	public static String randomIndicesFile = "C:\\MBOX\\results\\corel\\randomindices50.txt";
	/** The metric that is used for the ranking. **/
	public static Metric metric = new WeightedEuclidean();
	/** The descriptor-type of interest for the ranking. **/
	public static DescriptorType type = DescriptorType.CEDD;
	/**
	 * The normalization that is used on the descriptor of interest, this is
	 * important for the merged descriptor!
	 **/
	public static Normalization normalization = Normalization.GAUSSIAN;
	/** The path of the xml file containing the descriptors for your database. **/
	public static File xml_path = new File(
			"C:\\Users\\Stanic\\Desktop\\CorelDB\\cedd_descriptors.xml");
	/** Indicates whether you want to use indexing or not. **/
	public static boolean useIndexing = false;
	/**
	 * The relevance feedback method that should be used has to be specified
	 * here.
	 **/
	public static RelevanceFeedback rf = new Bayesian();
	/**
	 * Alternatively you can add all the types that you want to index into this
	 * array manually. (care that if you set useIndexing true the element on
	 * position 0 will get overwritten)
	 **/
	public static DescriptorType[] indexingFor;
	/** Do not touch the following variables. **/
	/** Contains the index of the current query. (do not modify this!) **/
	public static int currQuery = 0;
	/**
	 * The precision of every individual query in the corresponding iteration is
	 * stored here.
	 **/
	public static double[][] QueryPrecision = new double[numOfQueries][numOfRFIterations + 1];
	/**
	 * The average precision of the query in the corresponding iterations is
	 * stored here, where the index of the array is determined by the number of
	 * the current rf iteration.
	 **/
	public static double[] precision = new double[numOfRFIterations + 1];
	/**
	 * The standard deviations corresponding to the different iterations is
	 * stored here.
	 **/
	public static double[] precisionDeviation = new double[numOfRFIterations + 1];
	/**
	 * This is the list containing the random indices that are either generated
	 * in the program if newRandoms == true, or read from a file if newRandoms
	 * == false.
	 **/
	public static List<Integer> randomQueryIndices = new ArrayList<Integer>(
			numOfQueries);
	/**
	 * The average time of a relevance feedback iteration gets accumulated here.
	 **/
	public static double avgRFTIME = 0;
	/** The time of every query without using RF gets measuered here. **/
	public static long queryTime = 0;

	/**
	 * This is a implementation of an automatic relevance feedback iteration
	 * based on the labels in the database. In addition several statistical
	 * calculations are performed.
	 * 
	 * @param rf
	 *            the relevance feedback method that is used.
	 * @param retriever
	 *            the retriever that is used.
	 * @param query
	 *            the query image for which rf is performed.
	 * @param type
	 *            the descriptor-type of interest.
	 * @param metric
	 *            the metric that is used to calculate the ranking.
	 * @param result
	 *            the result list of the previous iteration.
	 */
	public static void relevanceFeedbackDemo(RelevanceFeedback rf,
			RetrieverDistanceBased retriever, ImageContainer query,
			DescriptorType type, Metric metric, List<ImageContainer> result) {

		int j = 0;
		while (true) {
			int numPositives = 0;

			StringBuilder relevant = new StringBuilder();
			StringBuilder irrelevant = new StringBuilder();
			LinkedList<ImageContainer> positives = new LinkedList<ImageContainer>();
			LinkedList<ImageContainer> negatives = new LinkedList<ImageContainer>();

			for (int i = 0; i < numOfResults; i++) {
				// if labels are equal mark as positive, else negative
				if (result.get(i).getLabel().equals(query.getLabel())) {
					positives.add(result.get(i));
					relevant.append(i);
					relevant.append(" ");
					numPositives++;

				} else {
					negatives.add(result.get(i));
					irrelevant.append(i);
					irrelevant.append(" ");
				}
			}
			QueryPrecision[currQuery][j] = (double) numPositives
					/ (double) numOfResults;
			precision[j] += ((double) numPositives / (double) numOfResults)
					/ numOfQueries;

			if (j >= numOfRFIterations) {
				break;
			}

			System.out.println("RF Iteration " + (j + 1));

			System.out.println("positives: " + relevant.toString());
			System.out.println("negatives: " + irrelevant.toString());
			Utils.printToFile(outputfile, "<p>Number of positives: "
					+ numPositives + "</p>\n");
			Utils.printToFile(outputfile,
					"<p>positives: " + relevant.toString() + "</p>\n");
			Utils.printToFile(outputfile,
					"<p>negatives: " + irrelevant.toString() + "</p>\n");

			long starttime, endtime;
			starttime = System.currentTimeMillis();
			result = rf.relevanceFeedbackIteration(retriever, query, type,
					metric, positives, negatives, numOfResults);
			endtime = System.currentTimeMillis();
			avgRFTIME += (endtime - starttime);
			retriever.printResultListHTML(result, type, outputfile);
			Utils.printToFile(outputfile, "<p>Relevancefeedback time: "
					+ (endtime - starttime) + "</p>\n");
			j++;
		}
	}

	/**
	 * 1) define metric 2) initialize list of images (database) & EHD descriptor
	 * from XML file 3) get Color Histogram from Fire file 4)
	 * 
	 */
	public static void main(String[] args) {
		try {

			long starttime, endtime;
			starttime = System.currentTimeMillis();

			System.out.println("Reading and labelling databases...");
			List<ImageContainer> database = new XMLReader()
					.parseXMLFile(xml_path);
			// set labels of images
			LabelUtils.labelDatabase(database);
			if (type.equals(DescriptorType.COLOR_HISTO)
					|| type.equals(DescriptorType.MERGED))
				new FireReader().readDescriptors(database,
						DescriptorType.COLOR_HISTO);

			endtime = System.currentTimeMillis();
			Utils.printToFile(outputfile, "<p>read descriptors: "
					+ (endtime - starttime) + " ms.</p>");
			starttime = endtime;

			// important this must be performed before calculating the index
			// structure
			Utility.normalizeDescriptors(database, type, normalization);

			endtime = System.currentTimeMillis();
			Utils.printToFile(outputfile, "<p>normalize MPEG_EHD descriptor: "
					+ (endtime - starttime) + " ms.</p>");
			starttime = endtime;

			// Add indexing here
			if (useIndexing) {
				indexingFor = new DescriptorType[1];
				indexingFor[0] = type;
			}
			RetrieverDistanceBased retriever;
			if (indexingFor != null)
				retriever = new RetrieverDistanceBased(database, metric,
						indexingFor);
			else
				retriever = new RetrieverDistanceBased(database, metric);

			endtime = System.currentTimeMillis();
			Utils.printToFile(outputfile, "<p>indexing: "
					+ (endtime - starttime) + " ms.</p>");
			starttime = endtime;

			List<ImageContainer> queries = new LinkedList<ImageContainer>();
			if (!newRandoms)
				randomQueryIndices = Utils
						.readIntListFromFile(randomIndicesFile);
			// make random queries
			System.out.println("Choosing random queries...");
			Random rnd = new Random();
			for (int i = 0; i < numOfQueries; i++) {
				try {
					if (newRandoms)
						randomQueryIndices.add(i, rnd.nextInt(database.size()));
					queries.add(database.get(randomQueryIndices.get(i))
							.deepCopy());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}

			for (ImageContainer query : queries) {
				System.out.println("Query " + currQuery + "...");
				starttime = System.currentTimeMillis();
				List<ImageContainer> results = retriever.search(query, type,
						numOfResults);
				retriever.printResultListHTML(results, type, outputfile);
				endtime = System.currentTimeMillis();
				queryTime += (endtime - starttime);
				Utils.printToFile(outputfile, "<p> query: "
						+ (endtime - starttime) + " ms.</p>");
				relevanceFeedbackDemo(rf, retriever, query, type, metric,
						results);
				currQuery++;
			}
			if (newRandoms)
				Utils.printIntListToFile(randomIndicesFile, randomQueryIndices);

			// calculate deviations
			for (int j = 0; j < numOfQueries; j++) {
				for (int k = 0; k <= numOfRFIterations; k++) {
					precisionDeviation[k] += Math.pow(QueryPrecision[j][k]
							- precision[k], 2)
							/ numOfQueries;// refactor a*a
				}
			}

			// print results
			for (int j = 0; j <= numOfRFIterations; j++) {
				System.out.println("AVG Precision Iteration " + j + ": "
						+ precision[j]);
				Utils.printToFile(outputfile, "<p>AVG Precision Iteration " + j
						+ ": " + precision[j] + "</p>\n");
				System.out.println("Precision Deviation Iteration " + j + ": "
						+ precisionDeviation[j]);
				Utils.printToFile(outputfile,
						"<p>Precision Deviation Iteration " + j + ": "
								+ precisionDeviation[j] + "</p>\n");
			}

			avgRFTIME /= numOfRFIterations * numOfQueries;
			System.out.println("AVG RF Iteration Time: " + avgRFTIME + " ms");
			Utils.printToFile(outputfile, "<p>AVG RF Time " + avgRFTIME
					+ " ms</p>");
			System.out.println("absolute query time (only search): "
					+ queryTime + "ms");
			Utils.printToFile(outputfile,
					"<p>absolute query time (only search): " + queryTime
							+ "ms</p>\n");
			queryTime /= numOfQueries;
			System.out.println("avg query time (only search): " + queryTime
					+ "ms");
			Utils.printToFile(outputfile, "<p>avg query time (only search): "
					+ queryTime + "ms</p>\n");

		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}

		System.out.println("terminated");

	}
}
