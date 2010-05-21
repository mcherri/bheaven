/*
 * Copyright 2010 Moustapha Cherri
 * 
 * This file is part of bheaven.
 * 
 * bheaven is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * bheaven is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with bheaven.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package cherri.bheaven.bplustree;

/**
 *
 */
public class BPlusTreeChecker<K extends Comparable<K>, V> {
	
	private Node<K, V> root;
	
	/**
	 * @param tree
	 */
	public BPlusTreeChecker(BPlusTree<K, V> tree) {
		this.root = tree.getRoot();
	}


	/**
	 * The B+ tree stores records (or pointers to actual records) only at
	 * the leaf nodes, which are all found at the same level in the tree,
	 * so the tree is always height balanced. 
	 */
	boolean checkTreeIsBalanced() {
		return root == null || root.isBalanced();
	}

	
	/**
	 * All internal nodes, except the root, have between 
	 * Ceiling(m/2) and m children.	
	 */
	boolean checkInternalNodesChildrenCount() {
		if(root == null) {
			return true;
		}
		
		boolean result = true;
		if(root instanceof InnerNode<?, ?>) {
			Node<K, V> children[] = ((InnerNode<K, V>) root)
					.getChildren();
			for (int i = 0; result && i < root.getSlots() + 1; i++) {
				result = result && children[i].checkCount();
			}
	
		}
		
		return result;
	}
	
	/**
	 * The root is either a leaf or has at least two children.
	 */
	boolean checkRootNode() {
		return root == null || root.checkRootNode();
	}
	
	/**
	 * Internal nodes store search key values, and are used only as
	 * placeholders to guide the search. The number of search key values in
	 * each internal node is one less than the number of its non-empty children,
	 * and these keys partition the keys in the children in the fashion of a
	 * search tree. 
	 */
	boolean checkInternalNodesKeysCountWithRespectToChildren() {
		/* Inernal node will always have key values one less than then number
		 * of its non-empty children according to our node structure. The
		 * remaining thing to check it the last node key value.
		 */  
		if(root instanceof InnerNode<?, ?>) {
			return ((InnerNode<K, V>) root).checkLastKey();
		}
		return true;
	}

	/**
	 * Internal nodes keys are stored in non-decreasing order (i.e. sorted in
	 * lexicographical order).
	 */ 
	boolean checkInternalNodesKeysOrder() {
		return root == null || root.checkKeyOrder();
	}
	
	/**
	 * Depending on the size of a record as compared to the size of a key, a
	 * leaf node in a B+ tree of order m may store more or less than m records.
	 * Typically this is based on the size of a disk block, the size of a
	 * record pointer, etcetera. The leaf pages must store enough records to
	 * remain at least half full. 
	 */
	boolean checkLeafNodesEntriesCount() {
		return checkInternalNodesChildrenCount();
	}
	
	/**
	 * The leaf nodes of a B+ tree are linked together to form a linked list.
	 * This is done so that the records can be retrieved sequentially without
	 * accessing the B+ tree index. This also supports fast processing of
	 * range-search queries. 
	 */
	boolean checkLeafNodesLinksOrder() {
		if(root == null) {
			return true;
		}
		
		Node<K, V> nodes[] = root.getLeafNodes();
		
		for (int i = 0; i < nodes.length - 1; i++) {
			LeafNode<K, V> node = (LeafNode<K, V>) nodes[i]; 
			if(node.getNext() != nodes[i + 1]) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Method used in unit testing to check the validity of the B+ Tree 
	 */
	boolean isValid() {
		return checkTreeIsBalanced() &&
				checkInternalNodesChildrenCount() &&
				checkRootNode() &&
				checkInternalNodesKeysCountWithRespectToChildren() &&
				checkInternalNodesKeysOrder() &&
				checkLeafNodesEntriesCount() &&
				checkLeafNodesLinksOrder();
	}
	
	/**
	 * @return A message containing the reason why the B+Tree is invalid.
	 *         If the B+ Tree is valid, an empty string is returned.
	 */
	String getInvalidReason() {
		StringBuffer message = new StringBuffer(520);
		
		if (!checkTreeIsBalanced()) {
			message.append("\ttree is not balanced.\n");
		}
		
		if (!checkInternalNodesChildrenCount()) {
			message.append("\ttree internal nodes, except the root, do not " +
					"have between Ceiling(m/2) and m children.\n");
		}
		
		if (!checkRootNode()) {
			message.append("\ttree root is neither a leaf nor has at least " +
					"two children.\n");
		}
		
		if (!checkInternalNodesKeysCountWithRespectToChildren()) {
			message.append("\ttree internal nodes entries count is not less " +
					"than children.\n");
		}
		
		if (!checkInternalNodesKeysOrder()) {
			message.append("\ttree internal nodes keys are not stored in " +
					"non-decreasing order.\n");
		}
		
		if (!checkLeafNodesEntriesCount()) {
			message.append("\ttree leaf nodes do store enough records to " +
					"remain at least half full.\n");
		}
		
		if (!checkLeafNodesLinksOrder()) {
			message.append("\ttree leaf nodes are not linked correctly or " +
					"the leaf entries are not ordered.\n");
		}
		
		return message.toString();
	}
	
}
