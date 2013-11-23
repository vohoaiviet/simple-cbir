/*
 * This file was derived from:
 * 
 * Copyright 2013 Justin Wetherell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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
	 * @return A string representation of the tree or "Tree has no nodes." if
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