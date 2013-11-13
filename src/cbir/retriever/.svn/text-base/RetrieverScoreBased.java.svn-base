package cbir.retriever;

import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.interfaces.Retriever;
import cbir.interfaces.Score;

/**
 * index structures not supported yet.
 * 
 * @author chris
 * 
 */

public class RetrieverScoreBased implements Retriever {
	private List<Image> database;
	private Score score;
	private ExecutorService executor = new ThreadPoolExecutor(5, 10, 30,
			TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

	/**
	 * @param database
	 * @param types
	 *            indicate for which descriptors, index structures should get
	 *            formed
	 */
	public RetrieverScoreBased(List<Image> database, Score score) {
		this.database = database;
		this.score = score;
	}

	public List<Image> findNearestNeighbors(final Image image,
			final DescriptorType type, int amount) {
		Stack<Future<?>> active = new Stack<Future<?>>();
		// calculate scores in parallel
		final int chunks = 20;
//		for(int i = 0; i<database.size(); i+=(database.size()/chunks)){
//			final int start = i;
//			final int end = i+database.size()/chunks;
//			active.push(executor.submit(new Runnable() {
//				@Override
//				public void run() {
//					for(int j = start; j< end; j++){
//						if(j>=database.size())
//							break;
//						Image curr = database.get(j);
//						curr.putScore(image.getFilename(), new Double(score.score(image, curr, type)));
//					}
//				}
//			}));
//		}
//		for (final Image curr: database) {
//			active.push(executor.submit(new Runnable() {
//				@Override
//				public void run() {
//					curr.putScore(image.getFilename(), new Double(score.score(image, curr, type)));
//				}
//			}));
//		}
//		while(!active.empty()){
//			try {
//				Future<?> curr = active.pop();
//				if(curr != null)
//					curr.get();
//			} catch (InterruptedException | ExecutionException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
		// get the best images sequentiell
//		return Utility.findNearestNeighbors(database, amount, new Comparator<Image>(){
//			@Override
//			public int compare(Image a, Image b) {
//				double scoreA = a.getScore(image.getFilename());
//				double scoreB = b.getScore(image.getFilename());
//				if (scoreA > scoreB)
//					return -1;
//				if (scoreA < scoreB)
//					return 1;
//				return 0;
//			}
//		});
		return Utility.findNearestNeighbors(database, amount,
				new Comparator<Image>() {
					@Override
					public int compare(Image a, Image b) {
						double scoreA = score.score(image, a, type);
						double scoreB = score.score(image, b, type);
						if (scoreA > scoreB)
							return -1;
						if (scoreA < scoreB)
							return 1;
						return 0;
					}
		});
	}

	/**
	 * perform a search for all descriptors...
	 * 
	 * @param query
	 * @param metric
	 * @param resultAmount
	 * @return the top resultAmount results
	 */
	@Override
	public List<Image> search(final Image query, final DescriptorType type,
			int resultAmount) {
		// otherwise just use sorting to make the ranking
		return findNearestNeighbors(query, type, resultAmount);
	}

	@Override
	public void printResultListHTML(List<Image> results, DescriptorType type,
			String filename) {
		Utility.printResultListHTML(results, type, filename);
	}

	@Override
	public Image getImageByName(String name) {
		return Utility.getImageByName(database, name);
	}

	@Override
	public List<Image> getDatabase() {
		return database;
	}

	public void setDatabase(List<Image> database) {
		this.database = database;
	}
}
