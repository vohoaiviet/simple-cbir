package ind.kdtree;

import cbir.image.DescriptorType;
import cbir.image.ImageContainer;

/**
 * Implements a node of a k-d-tree. Every node has a point (image), a dimension
 * (k), a depth, a descriptor type and references to the parent and the child
 * nodes.
 * 
 * This class offers overridden compareTo and equals functions.
 * 
 * @author Matej Stanic
 */

public class KDNode implements Comparable<KDNode> {
	/** Dimensionality of the node descriptor. */
	private int k;
	/** The depth of the node point. */
	private int depth;
	/** The image which is contained in the node. */
	private ImageContainer image = null;
	/** The descriptor type of the node descriptor. */
	private DescriptorType type;
	/** The parent node. */
	private KDNode parent = null;
	/** The left child node. */
	private KDNode lesser = null;
	/** The right child node. */
	private KDNode greater = null;

	/**
	 * Constructor of KDNode.
	 */
	public KDNode(ImageContainer image) {
		super();
		this.image = image;
	}

	/**
	 * Constructor of KDNode.
	 */
	public KDNode(int dim, int depth, ImageContainer image, DescriptorType type) {
		this(image);
		this.k = dim;
		this.depth = depth;
		this.type = type;
	}

	/**
	 * Compares the values of the actual splitting coordinate of two node
	 * descriptors.
	 * 
	 * @param depth
	 *            The depth of the actual node.
	 * @param k
	 *            The dimensionality of the actual node descriptor.
	 * @param image1
	 *            The actual node image.
	 * @param image2
	 *            The node image which is compared.
	 * @param type
	 *            The descriptor type used.
	 * @return
	 */
	public static int compareTo(int depth, int k, ImageContainer image1, ImageContainer image2,
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

	/**
	 * Compares the values of the actual splitting coordinate of a specified
	 * node descriptor to the actual node descriptor.
	 * 
	 * @param node
	 *            The node that is compared with the actual node.
	 */
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

	public ImageContainer getImage() {
		return image;
	}

	public void setImage(ImageContainer image) {
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
