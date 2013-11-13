package ind.kdtree;

import java.util.ArrayList;
import java.util.List;

/**
 * A class which offers a function for computing string representations of
 * trees. For testing purposes.
 * 
 * @author Matej Stanic
 * 
 */
public class TreePrinter {

	/**
	 * Computes and returnes a string representation of a given tree.
	 * 
	 * @param tree
	 *            Tree which has to be printed.
	 * @returns A string representation of the tree or "Tree has no nodes." if
	 *          the tree is empty.
	 */
	public static String getString(KDTree tree) {
		if (tree.getRoot() == null)
			return "Tree has no nodes.";
		return getString(tree.getRoot(), "", true);
	}

	/**
	 * A helper function.
	 */
	private static String getString(KDNode node, String prefix, boolean isTail) {
		StringBuilder builder = new StringBuilder();

		if (node.getParent() != null) {
			String side = "left";
			if (node.getParent().getGreater() != null
					&& node.getImage().equals(
							node.getParent().getGreater().getImage()))
				side = "right";
			builder.append(prefix + (isTail ? "'-- " : "|-- ") + "[" + side
					+ "] " + "Depth=" + node.getDepth() + " Image="
					+ node.getImage().getFilename() + "\n");
		} else {
			builder.append(prefix + (isTail ? "'--  " : "|-- ") + "Depth="
					+ node.getDepth() + " Image="
					+ node.getImage().getFilename() + "\n");
		}
		List<KDNode> children = null;
		if (node.getLesser() != null || node.getGreater() != null) {
			children = new ArrayList<KDNode>(2);
			if (node.getLesser() != null)
				children.add(node.getLesser());
			if (node.getGreater() != null)
				children.add(node.getGreater());
		}
		if (children != null) {
			for (int i = 0; i < children.size() - 1; i++) {
				builder.append(getString(children.get(i), prefix
						+ (isTail ? "    " : "|   "), false));
			}
			if (children.size() >= 1) {
				builder.append(getString(children.get(children.size() - 1),
						prefix + (isTail ? "    " : "|   "), true));
			}
		}

		return builder.toString();
	}
}