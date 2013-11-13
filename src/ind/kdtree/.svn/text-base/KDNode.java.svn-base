package ind.kdtree;

import cbir.image.DescriptorType;
import cbir.image.Image;

/**
 * Implements a node of a k-d-tree. Every node has a point (image), a dimension
 * and references to the parent and the next nodes.
 * 
 * @author Wendler Chris & Stanic Matej
 * 
 */
public class KDNode implements Comparable<KDNode> {
	private int k;
	private int depth;
	private Image image = null;
	private DescriptorType type;
	private KDNode parent = null;
	private KDNode lesser = null;
	private KDNode greater = null;

	public KDNode(Image image) {
		super();
		this.image = image;
	}

	public KDNode(int dim, int depth, Image image, DescriptorType type) {
		this(image);
		this.k = dim;
		this.depth = depth;
		this.type = type;
	}

	public static int compareTo(int depth, int k, Image image1, Image image2,
			DescriptorType type) {
		int dim = depth % k;
		double[] descriptor1 = image1.getDescriptor(type).getValues();
		double[] descriptor2 = image2.getDescriptor(type).getValues();
		if (descriptor1[dim] < descriptor2[dim])
			return -1;
		if (descriptor1[dim] > descriptor2[dim])
			return 1;
		return 0;
	}

	@Override
	public int compareTo(KDNode node) {
		return compareTo(depth, k, image, node.getImage(), type);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (!(obj instanceof KDNode)) {
			return false;
		} else {
			KDNode node = (KDNode) obj;
			if (this.compareTo(node) == 0) {
				return true;
			}
		}
		return false;

	}

	public int getK() {
		return k;
	}

	public void setK(int dimension) {
		this.k = dimension;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public KDNode getParent() {
		return parent;
	}

	public KDNode getLesser() {
		return lesser;
	}

	public KDNode getGreater() {
		return greater;
	}

	public void setParent(KDNode parent) {
		this.parent = parent;
	}

	public void setLesser(KDNode lesser) {
		this.lesser = lesser;
	}

	public void setGreater(KDNode greater) {
		this.greater = greater;
	}

}
