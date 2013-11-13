package cbir;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.dom4j.DocumentException;

import rf.Utility;
import rf.Utility.Normalization;
import rf.mars.MarsGaussianImproved;
import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.interfaces.Metric;
import cbir.interfaces.RelevanceFeedback;
import cbir.metric.WeightedEuclidean;
import cbir.reader.FireReader;
import cbir.reader.XMLReader;
import cbir.retriever.RetrieverDistanceBased;

/**
 * A class which simulates an automatic CBIR test.
 * 
 * @author Chris Wendler & Matej Stanic
 * 
 */
public class AutomatedRFTest2 {
	// TODO change output file here
	public static String outputfile = "C:\\MBOX\\results\\corel\\precisiontest_colorhisto_500queries_25results_1.html";
	public static int numOfQueries = 500;
	public static int numOfRFIterations = 5;
	public static int numOfResults = 25;
	// Turn RF off
	public static boolean noRelevanceFeedback = true;
	// Speedtest
	public static boolean speedTest = true;
	// Output ein/aus
	public static boolean output = false;
	public static double precision = 0.0;
	public static long queryTime = 0;

	/**
	 * demo implementation of relevance feedback circle
	 * 
	 * @param type
	 */

	public static void relevanceFeedbackDemo(RelevanceFeedback rf,
			RetrieverDistanceBased retriever, Image query, DescriptorType type,
			Metric metric, List<Image> result) {

		int j = 1;
		while (true) {
			if (j > numOfRFIterations) {
				break;
			}

			int numPositives = 0;

			StringBuilder relevant = new StringBuilder();
			StringBuilder irrelevant = new StringBuilder();

			System.out.println("RF Iteration " + j);
			LinkedList<Image> positives = new LinkedList<Image>();
			LinkedList<Image> negatives = new LinkedList<Image>();

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
			System.out.println("positives: " + relevant.toString());
			System.out.println("negatives: " + irrelevant.toString());
			if (output) {
				Utils.printToFile(outputfile, "<p>Number of positives: "
						+ numPositives + "</p>\n");
				Utils.printToFile(outputfile,
						"<p>positives: " + relevant.toString() + "</p>\n");
				Utils.printToFile(outputfile,
						"<p>negatives: " + irrelevant.toString() + "</p>\n");
			}
			precision += ((double) numPositives / (double) numOfResults);

			if (noRelevanceFeedback) {
				break;
			}
			long starttime, endtime;
			starttime = System.currentTimeMillis();
			result = rf.relevanceFeedbackIteration(retriever, query, type,
					metric, positives, negatives, numOfResults);
			if (output) {
				retriever.printResultListHTML(result, type, outputfile);
			}
			endtime = System.currentTimeMillis();
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

			// TODO change metric, descriptor & normalization here
			Metric metric = new WeightedEuclidean(null);
			DescriptorType type = DescriptorType.CEDD;
			Normalization normalization = Normalization.GAUSSIAN;

			long starttime, endtime;
			starttime = System.currentTimeMillis();

			// List<Image> database = new XMLReader()
			// .parseXMLFile(new File(
			// "C:\\MBOX\\signed_all\\lentos_color_ehd_2.xml"));

			// List<Image> database = new XMLReader()
			// .parseXMLFile(new File(
			// "C:\\MBOX\\signed_all\\oemv_cedd_ehd_color.xml"));
			//

			// cars
			// TODO change XML file here (EHD)
			System.out.println("Reading and labelling databases...");
			List<Image> database = new XMLReader().parseXMLFile(new File(
					"C:\\MBOX\\signed_all\\corel_ehd_cedd.xml"));
			// List<Image> database = new XMLReader()
			// .parseXMLFile(new File(
			// "C:\\MBOX\\signed_all\\madias_index.xml"));

			// set labels of images
			LabelUtils.labelDatabase(database);

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
			// TODO change RF, add indexing
			RetrieverDistanceBased retriever = new RetrieverDistanceBased(
					database, metric);

			endtime = System.currentTimeMillis();
			Utils.printToFile(outputfile, "<p>indexing: "
					+ (endtime - starttime) + " ms.</p>");
			starttime = endtime;

			List<Image> queries = new LinkedList<Image>();
			// TODO change query images here
			// make random queries
			System.out.println("Choosing random queries...");
			Random rnd = new Random();
			for (int i = 0; i < numOfQueries; i++) {
				try {
					queries.add(database.get(rnd.nextInt(database.size()))
							.deepCopy());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}

			int i = 1;
			for (Image query : queries) {
				System.out.println("Query " + i + "...");
				starttime = System.currentTimeMillis();
				long starttime_query, endtime_query;
				starttime_query = System.currentTimeMillis();
				List<Image> results = retriever.search(query, type,
						numOfResults);
				endtime_query = System.currentTimeMillis();
				queryTime += (endtime_query - starttime_query);
				if (output) {
					retriever.printResultListHTML(results, type, outputfile);
				}
				endtime = System.currentTimeMillis();
				Utils.printToFile(outputfile, "<p> query: "
						+ (endtime - starttime) + " ms.</p>");
				relevanceFeedbackDemo(new MarsGaussianImproved(database,
						DescriptorType.MERGED), retriever, query, type, metric,
						results);
				i++;
			}

		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}

		System.out.println("all precision: " + precision);
		System.out.println("all query time (only search): " + queryTime);
		precision /= numOfQueries;
		queryTime /= numOfQueries;
		System.out.println("avg precision: " + precision);
		System.out.println("avg query time (only search): " + queryTime);
		System.out.println("terminated");

	}
}
