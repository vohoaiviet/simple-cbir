package cbir;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
 * Performs a query with Relevance Feedback.
 * 
 * @author Chris Wendler
 * @author Matej Stanic
 * 
 */
public class RelevanceFeedbackTest {
	/** The folder where the resulting html file will be stored. **/
	public static String resultFolder = "C:\\Users\\Stanic\\Desktop\\CorelDB\\";
	/** The name of the output-file can be specified here. **/
	public static String outputfile = resultFolder + "manual_corel_cedd.html";
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
	 * Alternatively you can add all the types that you want to index into this
	 * array manually. (care that if you set useIndexing true the element on
	 * position 0 will get overwritten)
	 **/
	public static DescriptorType[] indexingFor;
	/**
	 * The relevance feedback method that should be used has to be specified
	 * here.
	 **/
	public static RelevanceFeedback rf = new Bayesian();
	/**
	 * For this test you can change the amount of random queries that is
	 * performed here, if you want to use your own query images you have to
	 * rewrite the main method.
	 **/
	public static int queryAmount = 5;

	/**
	 * This is a demo implementation of a relevance feedback iteration. usage: *
	 * for each rf iteration you need to specify the indices of the relevant and
	 * irrelevant images. There are several commands that the demo user
	 * interface accepts: "others": this command has to be entered when the
	 * program asks for irrelevant images, by entering "others" all images that
	 * are not marked as relevant are considered as irrelevant. "all": this
	 * command has to be entered when the program asks for relevant images. This
	 * means that all images are considered as positive. "assist": this command
	 * has to be entered when the program asks for relevant images, after assist
	 * you have to specify the file name of the file that contains the already
	 * marked images. IMPORTANT: you still have to mark the positive images in
	 * the same line. "quit": this command has to be entered when the program
	 * asks for relevant images, and causes the rf demo to stop, so the next
	 * query can be performed. After stop you can specify a filename where the
	 * marked images should be saved, this is needed when you want to use the
	 * assistant function in the future.
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
		List<ImageContainer> positives;
		List<ImageContainer> negatives;
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
						RFAssistant.printQueryHits(resultFolder + tokens[1],
								query);
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
				positives = new LinkedList<ImageContainer>();
				negatives = new LinkedList<ImageContainer>();
				int i = 0;
				if (indicesRelevant[0].equals("assist")) {
					rfassi = RFAssistant.createRFAssistant(resultFolder
							+ indicesRelevant[1]);
					i = 2;
				}

				// get positives
				if (relevant.equals("all")) {
					for (ImageContainer curr : result)
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
					for (ImageContainer curr : result)
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
										.size() + negatives.size())) * 100));

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

			long starttime, endtime;
			starttime = System.currentTimeMillis();

			List<ImageContainer> database = new XMLReader()
					.parseXMLFile(xml_path);

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
			try {
				for (int i = 0; i < queryAmount; i++) {
					Random rand = new Random();
					queries.add(database.get(rand.nextInt(database.size()))
							.deepCopy());
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			for (ImageContainer query : queries) {
				starttime = System.currentTimeMillis();
				List<ImageContainer> results = retriever
						.search(query, type, 20);
				retriever.printResultListHTML(results, type, outputfile);
				endtime = System.currentTimeMillis();
				Utils.printToFile(outputfile, "<p> query: "
						+ (endtime - starttime) + " ms.</p>");
				relevanceFeedbackDemo(rf, retriever, query, type, metric,
						results);
			}

		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}

	}
}
