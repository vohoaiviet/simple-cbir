package cbir.score;

import java.util.Comparator;
import java.util.List;

import rf.bayesian.Bayesian;
import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.interfaces.Metric;

public class BayesScore implements cbir.interfaces.Score {
	private List<Image> database;
	private Metric norm;
	private volatile Image BQS;
	private volatile Image BQSmax;
	private volatile int k = 0;

	public BayesScore(Metric metric, List<Image> database) {
		super();
		this.norm = metric;
		this.database = database;
	}

	public void init(final Image query, final DescriptorType type) {
		BQS = new Bayesian().shiftQuery(query, type);
		BQSmax = cbir.retriever.Utility.findNearestNeighbors(database, 1,
				new Comparator<Image>() {
					@Override
					public int compare(Image a, Image b) {
						double distA = norm.distance(a, query, type);
						double distB = norm.distance(b, query, type);
						if (distA > distB)
							return -1;
						if (distA < distB)
							return 1;
						return 0;
					}
				}).get(0);
	}

	@Override
	public double score(final Image query, final Image image,
			final DescriptorType type) {
		int knew = query.getPositives().size() + query.getNegatives().size();
		synchronized (this) {
			if (k != knew) {
				//System.out.println("k " + k + " knew " + knew);
				k = knew;
				init(query, type);
			}
		}
		double dBQS, dBQSmax, relevanceBQS;

		dBQS = norm.distance(BQS, image, type);
		dBQSmax = norm.distance(BQSmax, image, type);
		// relevanceBQS = 1 - dBQS;
		relevanceBQS = (1 - Math.pow(Math.E, (1 - (dBQS / dBQSmax))))
				/ (1 - Math.E);

		return relevanceBQS;
	}

}
