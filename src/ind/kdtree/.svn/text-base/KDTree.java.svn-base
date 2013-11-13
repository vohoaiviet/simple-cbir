package ind.kdtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.interfaces.Metric;

/**
 * Implements a k-d-tree and some essential functions.
 * 
 * @author Wendler Chris & Stanic Matej
 * 
 */
public class KDTree {

	private final int k;
	private KDNode root;
	private final DescriptorType type;

	/**
	 * Constructor.
	 * 
	 * @param list
	 *            list of images to construct the tree of
	 */
	public KDTree(List<Image> list, int k, DescriptorType type) {
		this.k = k;
		this.type = type;
		root = createNode(list, k, 0);

	}

	/**
	 * Create nodes from a list of Images.
	 * 
	 * @param list
	 *            list of images
	 * @param k
	 *            the k of the tree
	 * @param depth
	 *            depth of the node
	 * @returns the created node
	 */
	public KDNode createNode(List<Image> list, int k, int depth) {
		// if list is empty return null as node
		if (list == null || list.size() == 0) {
			return null;
		}

		// if list has just one element return node directly
		if (list.size() == 1) {
			KDNode node = new KDNode(k, depth, list.get(0), type);
			return node;
		}

		// determine current dimension (for splitting subtrees)
		final int dim = depth % k;

		// sort the list corresponding to the current axis
		Collections.sort(list, new Comparator<Image>() {
			@Override
			public int compare(Image arg0, Image arg1) {
				double[] descriptor0 = arg0.getDescriptor(type).getValues();
				double[] descriptor1 = arg1.getDescriptor(type).getValues();
				if (descriptor0[dim] < descriptor1[dim])
					return -1;
				if (descriptor0[dim] > descriptor1[dim])
					return 1;
				return 0;
			}

		});

		int median = list.size() / 2;
		KDNode node = new KDNode(k, depth, list.get(median), type);

		// split sorted list into 2 sublists and set subtrees recursively
		if (list.size() > 0) {
			if ((median - 1) >= 0) {
				List<Image> less = list.subList(0, median);
				if (less.size() > 0) {
					node.setLesser(createNode(less, k, depth + 1));
					node.getLesser().setParent(node);
				}
			}
			if ((median + 1) <= (list.size() - 1)) {
				List<Image> more = list.subList(median + 1, list.size());
				if (more.size() > 0) {
					node.setGreater(createNode(more, k, depth + 1));
					node.getGreater().setParent(node);
				}
			}
		}
		return node;
	}

	/**
	 * Adds an image to the existing tree.
	 * 
	 * @param image
	 *            image to add to the tree
	 * @returns true if the image was successfully added.
	 */
	public boolean add(Image image) {
		// if image is null return false
		if (image == null) {
			return false;

		}

		// if tree is empty set image as root
		if (root == null) {
			root = new KDNode(image);
			return true;
		}

		// search for right place to put the image in, traverse the tree until
		// there is a null-reference for putting the node in
		KDNode node = root;
		while (true) {
			if (KDNode.compareTo(node.getDepth(), node.getK(), node.getImage(),
					image, type) <= 0) {
				// lesser subtree
				if (node.getLesser() == null) {
					KDNode newNode = new KDNode(k, node.getDepth() + 1, image,
							type);
					newNode.setParent(node);
					node.setLesser(newNode);
					break;
				} else {
					node = node.getLesser();
				}
			} else {
				// greater subtree
				if (node.getGreater() == null) {
					KDNode newNode = new KDNode(k, node.getDepth() + 1, image,
							type);
					newNode.setParent(node);
					node.setGreater(newNode);
					break;
				} else {
					node = node.getGreater();
				}
			}
		}
		return true;

	}

	/**
	 * Checks whether the tree contains an image.
	 * 
	 * @param image
	 *            image to search for
	 * @returns true if the tree contains the image
	 */
	public boolean contains(Image image) {
		if (image == null) {
			return false;
		}
		KDNode node = getNode(this, image);
		if (node == null) {
			return false;
		}
		return true;
	}

	/**
	 * Removes the first instance of an image.
	 * 
	 * @param image
	 *            image to be removed
	 * @returns true if the image was removed successfully
	 */
	public boolean remove(Image image) {

		if (image == null) {
			return false;
		}

		// traverse to the needed node
		KDNode node = getNode(this, image);
		if (node == null) {
			return false;
		}

		// remove node
		KDNode parent = node.getParent();
		if (parent != null) {
			if (parent.getLesser() != null && parent.getLesser().equals(node)) {
				// node is lesser child
				List<Image> nodes = getTree(node);
				if (nodes.size() > 0) {
					// build new tree at position of removed node
					parent.setLesser(createNode(nodes, node.getK(),
							node.getDepth()));
					// set parent of the new subtree root
					if (parent.getLesser() != null) {
						parent.getLesser().setParent(parent);
					}
				} else {
					// leaf
					parent.setLesser(null);
				}

			} else {
				// node is greater child
				List<Image> nodes = getTree(node);
				if (nodes.size() > 0) {
					// build new tree at position of removed node
					parent.setGreater(createNode(nodes, node.getK(),
							node.getDepth()));
					// set parent of the new subtree root
					if (parent.getGreater() != null) {
						parent.getGreater().setParent(parent);
					}
				} else {
					// leaf
					parent.setGreater(null);
				}
			}
		} else {
			// node is root
			List<Image> nodes = getTree(node);
			if (nodes.size() > 0) {
				root = createNode(nodes, node.getK(), node.getDepth());
			} else {
				root = null;
			}
		}
		return true;
	}

	/**
	 * Locate an image in the tree.
	 * 
	 * @param tree
	 *            tree to search
	 * @param image
	 *            image to search for
	 * @returns node or null if not found
	 */
	public KDNode getNode(KDTree tree, Image image) {
		// return if tree is empty or image is null
		if (tree == null || tree.root == null || image == null) {
			return null;
		}

		// traverse tree until a the node is found or until a null pointer is
		// reached
		KDNode node = tree.getRoot();
		while (true) {
			if (node == null) {
				return null;
			}
			if (node.getImage().equals(image)) {
				return node;
				// greater
			} else if (KDNode.compareTo(node.getDepth(), node.getK(),
					node.getImage(), image, type) < 0) {
				if (node.getGreater() == null) {
					return null;
				} else {
					node = node.getGreater();
				}
				// lesser
			} else {
				if (node.getLesser() == null) {
					return null;
				} else {
					node = node.getLesser();
				}
			}
		}

	}

	/**
	 * Get the subtree starting at root.
	 * 
	 * @param root
	 *            root of the tree to get nodes for
	 * @returns a list of images of the subtree, root excluded
	 */
	public List<Image> getTree(KDNode root) {
		List<Image> list = new ArrayList<Image>();
		if (root.getLesser() != null) {
			list.add(root.getLesser().getImage());
			list.addAll(getTree(root.getLesser()));
		}
		if (root.getGreater() != null) {
			list.add(root.getGreater().getImage());
			list.addAll(getTree(root.getGreater()));
		}
		return list;

	}

	/**
	 * Search for num nearest neighbors.
	 * 
	 * @param num
	 *            the number of nearest neighbors to be retrieved
	 * @param image
	 *            image to find neighbors of
	 * @param metric
	 *            metric used for finding neighbors
	 * @param type
	 *            descriptor type which is considered
	 * @returns a list of the num nearest neighbors, null if image is null
	 */
	public List<Image> nearestNeighborSearch(int num, final Image image,
			final Metric metric, final DescriptorType type) {

		if (image == null)
			return null;

		// Map used for results (with comparator for descending sorting)
		TreeSet<KDNode> results = new TreeSet<KDNode>(new Comparator<KDNode>() {
			@Override
			public int compare(KDNode arg0, KDNode arg1) {
				Image a = arg0.getImage();
				Image b = arg1.getImage();
				if (metric.distance(a, image, type) < metric.distance(b, image,
						type))
					return -1;
				if (metric.distance(a, image, type) > metric.distance(b, image,
						type))
					return 1;
				return 0;
			}

		});

		// Find the closest leaf node
		KDNode prev = null;
		KDNode node = root;
		while (node != null) {
			if (KDNode.compareTo(node.getDepth(), node.getK(), node.getImage(),
					image, type) < 0) {
				// Greater
				prev = node;
				node = node.getGreater();
			} else {
				// Lesser
				prev = node;
				node = node.getLesser();
			}
		}
		KDNode leaf = prev;

		if (leaf != null) {
			// Used to not re-examine nodes
			Set<KDNode> examined = new HashSet<KDNode>();

			// Go up the tree, looking for better solutions
			node = leaf;
			while (node != null) {
				// Search node is called for every parent of the nearest leaf
				searchNode(image, node, num, results, examined, type, metric);
				node = node.getParent();
			}
		}

		// Load up the collection of the results
		Collection<Image> collection = new ArrayList<Image>(num);
		for (KDNode KDNode : results) {
			collection.add(KDNode.getImage());
		}
		return (List<Image>) collection;

	}

	/**
	 * Helper function for nearest neighbor search.
	 * 
	 */
	private static final void searchNode(Image image, KDNode node, int num,
			TreeSet<KDNode> results, Set<KDNode> examined, DescriptorType type,
			Metric metric) {
		// search for better results starting from the current node
		examined.add(node);

		// Search node
		KDNode lastNode = null;
		double lastDistance = Double.MAX_VALUE;

		// if there are already images in the result sets, mark the last one
		// -> the image with the worst distance
		if (results.size() > 0) {
			lastNode = results.last();
			lastDistance = metric.distance(lastNode.getImage(), image, type);
		}
		// get distance of current node (some parent of the nearest leaf)
		double nodeDistance = metric.distance(node.getImage(), image, type);

		// if the current node is better, insert it into the set and remove the
		// worst node of the results if necessary
		if (nodeDistance < lastDistance) {
			if (results.size() == num && lastNode != null)
				results.remove(lastNode);
			results.add(node);
			// TODO should be ok
			// if the distances are equal, insert
		} else if (nodeDistance == lastDistance) {
			results.add(node);
			// if there is enough space, insert regardless of distance
		} else if (results.size() < num) {
			results.add(node);
		}

		// mark new worst node
		lastNode = results.last();
		lastDistance = metric.distance(lastNode.getImage(), image, type);

		// get plane axis of the current node
		int dim = node.getDepth() % node.getK();
		// specify children
		KDNode lesser = node.getLesser();
		KDNode greater = node.getGreater();

		// The algorithm checks whether there could be any points on the other
		// side of the splitting plane that are closer to the search point than
		// the current best. In concept, this is done by intersecting the
		// splitting hyperplane with a hypersphere around the search point that
		// has a radius equal to the current nearest distance. Since the
		// hyperplanes are all axis-aligned this is implemented as a simple
		// comparison to see whether the difference between the splitting
		// coordinate of the search point and current node is less than the
		// distance (overall coordinates) from the search point to the current
		// best.
		if (lesser != null && !examined.contains(lesser)) {
			examined.add(lesser);

			double p1 = Double.MIN_VALUE;
			double p2 = Double.MIN_VALUE;

			// p1 =
			// Math.abs(node.getImage().getDescriptor(type).getValues()[dim]
			// - image.getDescriptor(type).getValues()[dim]);
			// boolean lineIntersectsCube = ((p1 <= lastDistance) ? true :
			// false);
			p1 = node.getImage().getDescriptor(type).getValues()[dim];
			p2 = image.getDescriptor(type).getValues()[dim] - lastDistance;
			boolean lineIntersectsCube = ((p2 <= p1) ? true : false);

			// Continue down lesser branch
			// TODO test if always true & old version
			if (lineIntersectsCube) {
				searchNode(image, lesser, num, results, examined, type, metric);
			}
		}

		// Same algorithm as lesser branch
		if (greater != null && !examined.contains(greater)) {
			examined.add(greater);

			double p1 = Double.MIN_VALUE;
			double p2 = Double.MIN_VALUE;

			// p1 =
			// Math.abs(node.getImage().getDescriptor(type).getValues()[dim]
			// - image.getDescriptor(type).getValues()[dim]);
			// boolean lineIntersectsCube = ((p1 <= lastDistance) ? true :
			// false);

			p1 = node.getImage().getDescriptor(type).getValues()[dim];
			p2 = image.getDescriptor(type).getValues()[dim] + lastDistance;
			boolean lineIntersectsCube = ((p2 >= p1) ? true : false);

			// Continue down greater branch
			if (lineIntersectsCube) {
				searchNode(image, greater, num, results, examined, type, metric);
			}
		}

	}

	public KDNode getRoot() {
		return root;
	}

}
