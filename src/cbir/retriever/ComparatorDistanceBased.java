package cbir.retriever;

import java.util.Comparator;

import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.interfaces.Metric;

public class ComparatorDistanceBased implements Comparator<Image> {
	private Image image;
	private Metric metric;
	private DescriptorType type;

	public ComparatorDistanceBased(Image image, Metric metric, DescriptorType type) {
		this.image = image;
		this.metric = metric;
		this.type = type;
	}

	@Override
	public int compare(Image a, Image b) {
		double distA = metric.distance(a, image, type);
		double distB = metric.distance(b, image, type);
		if (distA < distB)
			return -1;
		if (distA > distB)
			return 1;
		return 0;
	}
}
