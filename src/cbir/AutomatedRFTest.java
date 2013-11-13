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
import rf.nn.NearestNeighbors;
import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.interfaces.Metric;
import cbir.interfaces.RelevanceFeedback;
import cbir.metric.WeightedCosine;
import cbir.reader.FireReader;
import cbir.reader.XMLReader;
import cbir.retriever.RetrieverDistanceBased;
import cbir.score.BayesScore;
import cbir.score.NNBayesScore;
import cbir.score.NNScore;

/**
 * A class which simulates an automatic CBIR test.
 * 
 * @author Matej Stanic & Chris Wendler
 * 
 */
public class AutomatedRFTest {
	public static String outputfile = "C:\\MBOX\\results\\corel\\automatic_time_nnbqs_wcosine_gaussian0to1_50_5_20.html";
	public static int numOfQueries = 50;
	public static int numOfRFIterations = 4;
	public static int numOfResults = 20;
	public static List<Integer> randomQueryIndices = new ArrayList<Integer>(numOfQueries);
	public static int currQuery = 0;
	public static double [][] QueryPrecision = new double[numOfQueries][numOfRFIterations+1];
	public static double [] precision = new double[numOfRFIterations+1]; 
	public static double [] precisionDeviation = new double[numOfRFIterations+1];
	// TODO configure whether new Randoms are generated or old ones are loaded
	public static boolean newRandoms = true;
	public static String randomIndicesFile = "C:\\MBOX\\results\\corel\\randomindices50.txt";
	public static double avgRFTIME = 0;
	

	/**
	 * demo implementation of relevance feedback circle
	 * 
	 * @param type
	 */

	public static void relevanceFeedbackDemo(RelevanceFeedback rf,
			RetrieverDistanceBased retriever, Image query, DescriptorType type,
			Metric metric, List<Image> result) {

		int j = 0;
		while (true) {
			int numPositives = 0;

			StringBuilder relevant = new StringBuilder();
			StringBuilder irrelevant = new StringBuilder();
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
			QueryPrecision[currQuery][j] = (double) numPositives / (double) numOfResults;
			precision[j] += ((double) numPositives / (double) numOfResults) / numOfQueries;
			
			if (j >= numOfRFIterations) {
				break;
			}

			System.out.println("RF Iteration " + (j+1));
			
			
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
			avgRFTIME += (endtime-starttime);
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

			// TODO change metric, descriptor & normalization here
			Metric metric = new WeightedCosine();
			DescriptorType type = DescriptorType.MERGED;
			Normalization normalization = Normalization.GAUSSIAN_0to1;

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
			// TODO add indexing
			RetrieverDistanceBased retriever = new RetrieverDistanceBased(
					database, metric);

			endtime = System.currentTimeMillis();
			Utils.printToFile(outputfile, "<p>indexing: "
					+ (endtime - starttime) + " ms.</p>");
			starttime = endtime;

			List<Image> queries = new LinkedList<Image>();
			if(!newRandoms)
				randomQueryIndices = Utils.readIntListFromFile(randomIndicesFile);
			// TODO change query images here
			// make random queries
			System.out.println("Choosing random queries...");
			Random rnd = new Random();
			for (int i = 0; i < numOfQueries; i++) {
				try {
					if(newRandoms)
						randomQueryIndices.add(i,rnd.nextInt(database.size()));
					queries.add(database.get(randomQueryIndices.get(i))
							.deepCopy());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}

			for (Image query : queries) {
				System.out.println("Query " + currQuery + "...");
				starttime = System.currentTimeMillis();
				List<Image> results = retriever.search(query, type,
						numOfResults);
				retriever.printResultListHTML(results, type, outputfile);
				endtime = System.currentTimeMillis();
				//TODO: change RF
				Utils.printToFile(outputfile, "<p> query: "
						+ (endtime - starttime) + " ms.</p>");
				//new NNBayesScore(metric,new BayesScore(metric,database), new NNScore(metric))
				relevanceFeedbackDemo(new NearestNeighbors(new NNBayesScore(new BayesScore(metric,database), new NNScore(metric))), retriever, query, type, metric,
						results);
				currQuery++;
			}
			if(newRandoms)
				Utils.printIntListToFile(randomIndicesFile, randomQueryIndices);
			
			//calculate deviations
			for(int j = 0; j<numOfQueries; j++){
				for(int k = 0; k<=numOfRFIterations; k++){
					precisionDeviation[k] += Math.pow(QueryPrecision[j][k]-precision[k],2)/numOfQueries;//refactor a*a
				}
			}
			
			//print results
			for(int j = 0; j<=numOfRFIterations; j++){
				System.out.println("AVG Precision Iteration "+j+": "+precision[j]);
				Utils.printToFile(outputfile, "<p>AVG Precision Iteration "+j+": "+precision[j]+"</p>\n");
				System.out.println("Precision Deviation Iteration "+j+": "+precisionDeviation[j]);
				Utils.printToFile(outputfile, "<p>Precision Deviation Iteration "+j+": "+precisionDeviation[j]+"</p>\n");
			}
			
			avgRFTIME /= numOfRFIterations*numOfQueries;
			
			System.out.println("AVG RF Iteration Time: "+avgRFTIME+" ms");
			Utils.printToFile(outputfile, "<p>AVG RF Time "+avgRFTIME+" ms</p>");
			
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}

		System.out.println("terminated");

	}
}
