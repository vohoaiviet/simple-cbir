package cbir;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.DocumentException;

import rf.Utility;
import rf.Utility.Normalization;
import rf.bayesian.Bayesian;
import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.interfaces.Metric;
import cbir.interfaces.RelevanceFeedback;
import cbir.metric.WeightedEuclidean;
import cbir.reader.FireReader;
import cbir.reader.XMLReader;
import cbir.retriever.RetrieverDistanceBased;

public class RelevanceFeedbackTest {
	// TODO change output file here
	public static String resultFolder = "C:\\Users\\Chris\\Desktop\\";
	public static String outputfile = resultFolder
			+ "livedemo_cars.html";

	/**
	 * demo implementation of relevance feedback circle
	 * 
	 * @param type
	 */
	public static void relevanceFeedbackDemo(RelevanceFeedback rf,
			RetrieverDistanceBased retriever, Image query, DescriptorType type,
			Metric metric, List<Image> result) {
		List<Image> positives;
		List<Image> negatives;
		BufferedReader stdin = new BufferedReader(new InputStreamReader(
				System.in));
		String relevant = null, irrelevant = null;
		RFAssistant rfassi = null;
		while (true) {
			try {
				System.out
						.print("Enter the indices of relevant images (start with 0): ");
				System.out.flush();
				relevant = stdin.readLine();
				if (relevant.length() >= 4
						&& relevant.substring(0, 4).equals("quit")) {
					String[] tokens = relevant.split(" ");
					if (tokens.length == 2)
						Utils.printQueryHits(resultFolder + tokens[1], query);
					break;
				}
				System.out
						.print("Enter the indices of irrelevant images (start with 0): ");
				System.out.flush();
				irrelevant = stdin.readLine();

				Utils.printToFile(outputfile, "<p>positives: " + relevant
						+ "</p>\n");
				Utils.printToFile(outputfile, "<p>negatives: " + irrelevant
						+ "</p>\n");

				String[] indicesRelevant = relevant.split(" ");
				String[] indicesIrrelevant = irrelevant.split(" ");
				positives = new LinkedList<Image>();
				negatives = new LinkedList<Image>();
				int i = 0;
				if (indicesRelevant[0].equals("assist")) {
					rfassi = Utils.createRFAssistant(resultFolder
							+ indicesRelevant[1]);
					i = 2;
				}

				// get positives
				if (relevant.equals("all")) {
					for (Image curr : result)
						positives.add(curr);
				} else {
					for (; i < indicesRelevant.length; i++)
						try {
							String curr = indicesRelevant[i];
							int index = Integer.parseInt(curr);
							positives.add(result.get(index));

						} catch (NumberFormatException e) {
							continue;
						}
				}

				// get negatives
				if (irrelevant.equals("others")) {
					for (Image curr : result)
						if (positives.contains(curr))
							continue;
						else
							negatives.add(curr);
				} else {
					for (String curr : indicesIrrelevant)
						try {
							int index = Integer.parseInt(curr);
							negatives.add(result.get(index));

						} catch (NumberFormatException e) {
							continue;
						}
				}

				long starttime, endtime;
				starttime = System.currentTimeMillis();

				// correct results if rfassistant is enabled
				if (rfassi != null)
					rfassi.revisePositivesAndNegatives(positives, negatives);

				System.out
						.println("precision of previous query: "
								+ ((positives.size() / (double) (positives
										.size() + negatives.size()))*100));

				result = rf.relevanceFeedbackIteration(retriever, query, type,
						metric, positives, negatives, 20);
				retriever.printResultListHTML(result, type, outputfile);
				endtime = System.currentTimeMillis();
				Utils.printToFile(outputfile, "<p>Relevancefeedback time: "
						+ (endtime - starttime) + " ms </p>\n");

			} catch (IOException e) {
				e.printStackTrace();
			}
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
			DescriptorType type = DescriptorType.MERGED;
			Normalization normalization = Normalization.GAUSSIAN_0to1;

			long starttime, endtime;
			starttime = System.currentTimeMillis();

			 List<Image> database = new XMLReader()
			 .parseXMLFile(new File(
			 "C:\\MBOX\\signed_all\\lentos_color_ehd_2.xml"));

			// List<Image> database = new XMLReader()
			// .parseXMLFile(new File(
			// "C:\\MBOX\\signed_all\\oemv_cedd_ehd_color.xml"));
			//

			// cars
			// TODO change XML file here (EHD)
//			 List<Image> database = new XMLReader().parseXMLFile(new File(
//			 "C:\\MBOX\\signed_all\\cars_ehd.xml"));

//			List<Image> database = new XMLReader().parseXMLFile(new File(
//					"C:\\MBOX\\signed_all\\madias_index.xml"));

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
			//
			// // lentos
			// queries.add(retriever
			// .getImageByName("C:\\MBOX\\signed_all\\Lentos_signed\\0_000_000_063.jpg").deepCopy());
			// queries.add(retriever
			// .getImageByName("C:\\MBOX\\signed_all\\Lentos_signed\\0_000_001_161.jpg").deepCopy());
			// queries.add(retriever
			// .getImageByName("C:\\MBOX\\signed_all\\Lentos_signed\\0_000_009_410.jpg").deepCopy());
			// queries.add(retriever
			// .getImageByName("C:\\MBOX\\signed_all\\Lentos_signed\\0_000_001_023.jpg").deepCopy());
			// oemv
			// queries.add(retriever.getImageByName("C:\\MBOX\\signed_all\\OeMV_signed\\0_000_028_693.jpg").deepCopy());
			// queries.add(retriever.getImageByName("C:\\MBOX\\signed_all\\OeMV_signed\\0_000_010_614.jpg").deepCopy());
			// queries.add(retriever.getImageByName("C:\\MBOX\\signed_all\\OeMV_signed\\0_000_025_287.jpg").deepCopy());
			// queries.add(retriever.getImageByName("C:\\MBOX\\signed_all\\OeMV_signed\\0_000_025_138.jpg").deepCopy());
			// madias
//			queries.add(retriever
//					.getImageByName(
//							"C:\\MBOX\\signed_all\\madias_signed\\0_000_002_517.jpg")
//					.deepCopy());
//			queries.add(retriever
//					.getImageByName(
//							"C:\\MBOX\\signed_all\\madias_signed\\0_000_005_319.jpg")
//					.deepCopy());
//			queries.add(retriever
//					.getImageByName(
//							"C:\\MBOX\\signed_all\\madias_signed\\0_000_007_167.jpg")
//					.deepCopy());
//			queries.add(retriever
//					.getImageByName(
//							"C:\\MBOX\\signed_all\\madias_signed\\0_000_011_014.jpg")
//					.deepCopy());

			// queries.add(retriever.getImageByName("C:\\MBOX\\signed_all\\madias_signed\\0_000_007_321.jpg"));
			// queries.add(retriever.getImageByName("C:\\MBOX\\signed_all\\madias_signed\\0_000_018_468.jpg"));
			// queries.add(retriever.getImageByName("C:\\MBOX\\signed_all\\madias_signed\\0_000_019_441.jpg"));
			// queries.add(retriever.getImageByName("C:\\MBOX\\signed_all\\madias_signed\\0_000_000_017.jpg"));
			// queries.add(retriever.getImageByName("C:\\MBOX\\signed_all\\madias_signed\\0_000_000_094.jpg"));
			// queries.add(retriever.getImageByName("C:\\MBOX\\signed_all\\madias_signed\\0_000_000_656.jpg"));
			// queries.add(retriever.getImageByName("C:\\MBOX\\signed_all\\madias_signed\\0_000_005_242.jpg"));
			// queries.add(retriever.getImageByName("C:\\MBOX\\signed_all\\madias_signed\\0_000_005_586.jpg"));
			// queries.add(retriever.getImageByName("C:\\MBOX\\signed_all\\madias_signed\\0_000_007_120.jpg"));
			// queries.add(retriever.getImageByName("C:\\MBOX\\signed_all\\madias_signed\\0_000_019_516.jpg"));
			// cars
			// queries.add(retriever.getImageByName(
			// "C:\\MBOX\\signed\\hdk_5730.jpg").deepCopy());
			// queries.add(retriever.getImageByName(
			// "C:\\MBOX\\signed\\hdk_2859.jpg").deepCopy());
			// queries.add(retriever.getImageByName(
			// "C:\\MBOX\\signed\\hdk_1809.jpg").deepCopy());
			// queries.add(retriever.getImageByName(
			// "C:\\MBOX\\signed\\hdk_2497.jpg").deepCopy());
			// queries.add(retriever.getImageByName(
			// "C:\\MBOX\\signed\\hdk_1025.jpg").deepCopy());
			// queries.add(retriever.getImageByName(
			// "C:\\MBOX\\signed\\hdk_4882.jpg").deepCopy());
			// queries.add(retriever.getImageByName(
			// "C:\\MBOX\\signed\\hdk_5730.jpg").deepCopy());
			try {
				 queries.add(retriever
				 .getImageByName("C:\\MBOX\\signed_all\\Lentos_signed\\0_000_000_063.jpg").deepCopy());
				 queries.add(retriever
				 .getImageByName("C:\\MBOX\\signed_all\\Lentos_signed\\0_000_001_161.jpg").deepCopy());
				 queries.add(retriever
				 .getImageByName("C:\\MBOX\\signed_all\\Lentos_signed\\0_000_009_410.jpg").deepCopy());
				 queries.add(retriever
				 .getImageByName("C:\\MBOX\\signed_all\\Lentos_signed\\0_000_001_023.jpg").deepCopy());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			// queries.add(database.get((int) (Math.random()*10000) %
			// database.size()));
			// queries.add(database.get((int) (Math.random()*10000) %
			// database.size()));
			// queries.add(database.get((int) (Math.random()*10000) %
			// database.size()));
			// queries.add(database.get((int) (Math.random()*10000) %
			// database.size()));
			// queries.add(database.get((int) (Math.random()*10000) %
			// database.size()));
			// queries.add(database.get((int) (Math.random()*10000) %
			// database.size()));
			// queries.add(database.get((int) (Math.random()*10000) %
			// database.size()));
			// queries.add(database.get((int) (Math.random()*10000) %
			// database.size()));
			// queries.add(database.get((int) (Math.random()*10000) %
			// database.size()));
			// queries.add(database.get((int) (Math.random()*10000) %
			// database.size()));
			// queries.add(database.get((int) (Math.random()*10000) %
			// database.size()));
			// queries.add(database.get((int) (Math.random()*10000) %
			// database.size()));
			for (Image query : queries) {
				starttime = System.currentTimeMillis();
				List<Image> results = retriever.search(query, type, 20);
				retriever.printResultListHTML(results, type, outputfile);
				endtime = System.currentTimeMillis();
				Utils.printToFile(outputfile, "<p> query: "
						+ (endtime - starttime) + " ms.</p>");
				// TODO: change rf here
				// new NearestNeighbors(new NNBayesScore(metric, new BayesScore(metric, results), new NNScore(metric)))
				relevanceFeedbackDemo(new Bayesian(false), retriever,
						query, type, metric, results);
			}

		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}

	}
}
