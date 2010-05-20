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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @param <K>
 *
 */
public /*abstract*/ class BPlusTree<K extends Comparable<K>, V> /*implements Map<K, V>*/ {
	
	private Node<K, V> root;
	private final int order;
	private final int records;

	/**
	 * @param order the order of the B+ Tree
	 * @param records TODO
	 */
	public BPlusTree(int order, int records) {
		this.order = order;
		this.records = records;
	}
	
	private LeafNode<K, V> findLeafNode(K key) {
		return findLeafNode(key, null);
	}
	
	private LeafNode<K, V> findLeafNode(K key,
			List<Breadcrumb<K, V>> breadcrumbList) {
		if (root == null) {
			return null;
		}
		
		Node<K, V> node = root;
		breadcrumbAdd(breadcrumbList, node, -1);
		
		while (!(node instanceof LeafNode<?, ?>)) {
		
			int index = Arrays.binarySearch(node.getKeys(), 0, node.getSlots(),
					key, null);
			
			if(index < 0) {
				index = -index - 1;
			}
			
			
			node = ((InnerNode<K, V>) node).getChildren()[index];
			breadcrumbAdd(breadcrumbList, node, index);

		}
		
		return (LeafNode<K, V>) node;
	}

	private void breadcrumbAdd(List<Breadcrumb<K, V>> breadcrumbList,
			Node<K, V> node, int index) {
		if(breadcrumbList != null) {
			breadcrumbList.add(new Breadcrumb<K, V>(node, index));
		}
	}
	
	private Node<K, V> getParent(List<Breadcrumb<K, V>> breadcrumbList,
			int position) {
		if (position <= 0) {
			return null;
		} else {
			return breadcrumbList.get(position - 1).getNode();
		}
	}

	private int getIndex(List<Breadcrumb<K, V>> breadcrumbList,
			int position) {
		if (position < 0) {
			return -1;
		} else {
			return breadcrumbList.get(position).getIndex();
		}
	}

	public V get(K key) {
		/*
		   1. Perform a binary search on the search key values in the current
		      node -- recall that the search key values in a node are sorted
		      and that the search starts with the root of the tree. We want to
		      find the key Ki  such that Ki <= K < Ki+1.
		   2. If the current node is an internal node, follow the proper branch
		      associated with the key Ki by loading the disk page corresponding
		      to the node and repeat the search process at that node.
		*/
		LeafNode<K, V> node = findLeafNode(key);
		
		/*
		   3. If the current node is a leaf, then:
		         1. If K = Ki, then the record exists in the table and we can
		            return the record associated with Ki
		         2. Otherwise, K is not found among the search key values at
		            the leaf, we report that there is no record in the table
		            with the value K.
		*/
		
		if (node == null) {
			return null;
		}
		
		int index = Arrays.binarySearch(node.getKeys(), 0, node.getSlots(),
				key, null);
		
		if(index >= 0) {
			return node.getValues()[index];
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void put(K key, V value) {
		if (root == null) {
			root = new LeafNode<K, V>((K[]) new Comparable[records],
					(V[]) new Object[records], 0, null);
		}
		
		/*
		   1.  Follow the path that is traversed as if a Search is being
		       performed on the key of the new record to be inserted.
           2. The leaf page L that is reached is the node where the new record
              is to be indexed.
		*/
		List<Breadcrumb<K, V>> breadcrumbList =
			new ArrayList<Breadcrumb<K, V>>(10);
		LeafNode<K, V> leafNode = findLeafNode(key, breadcrumbList);
		Node<K, V> node = leafNode;
		int position = breadcrumbList.size() - 1;

		/*
           3. If L is not full then an index entry is created that includes the
              search key value of the new row and a reference to where new row
              is in the data file. We are done; this is the easy case!
        */
		if(!leafNode.isFull()) {
			leafNode.insert(key, value);
		} else {
			/*
	           4. If L is full, then a new leaf node Lnew is introduced to the
	              B+-tree as a right sibling of L. The keys in L along with the an
	              index entry for the new record are distributed evenly among L and
	              Lnew. Lnew is inserted in the linked list of leaf nodes just to
	              the right of L. We must now link Lnew to the tree and since Lnew
	              is to be a sibling of L, it will then be pointed to by the
	              parent of L. The smallest key value of Lnew is copied and
	              inserted into the parent of L -- which will also be the parent of
	              Lnew. This entire step is known as commonly referred to as a
	              split of a leaf node.
	        */
			K newKey = key;
			LeafNode<K, V> newLeafNode = split(leafNode, newKey, value);
			
			InnerNode<K, V> parent = 
				(InnerNode<K, V>) getParent(breadcrumbList, position--); // node.getParent();
			Node<K, V> newNode = newLeafNode;
			newKey = node.getKeys()[node.getSlots() - 1];
			
			/*
            a. If the parent P of L is full, then it is split in turn.
               However, this split of an internal node is a bit different.
               The search key values of P and the new inserted key must
               still be distributed evenly among P and the new page
               introduced as a sibling of P. In this split, however, the
               middle key is moved to the node above -- note, that unlike
               splitting a leaf node where the middle key is copied and
               inserted into the parent, when you split an internal node
               the middle key is removed from the node being split and
               inserted into the parent node. This splitting of nodes may
               continue upwards on the tree.
			*/
			while (parent != null && parent.isFull()) {
				InnerNode<K, V> newInnerNode = split(parent, newKey, newNode);
				newKey = parent.getKeys()[parent.getSlots()];
				node = parent;
				newNode = newInnerNode;
				parent = (InnerNode<K, V>) getParent(breadcrumbList, position--); //parent.getParent();
				//node.setNext(newLeafNode);
				
			}
			
			/*
            b. When a key is added to a full root, then the root splits
               into two and the middle key is promoted to become the new
               root. This is the only way for a B+-tree to increase in
               height -- when split cascades the entire height of the tree
               from the leaf to the root. 
            */
			if (parent == null) {
				parent = new InnerNode<K, V>((K[]) new Comparable[order - 1],
						new Node[order], 0);
				parent.getChildren()[0] = node;
				root = parent;
			}
			
			parent.insert(newKey, newNode);
		}
	}

	/*
	 * A very complex method needs documentation. It is used in insertion.
	 */
	private InnerNode<K, V> split(InnerNode<K, V> parent, K key,
			Node<K, V> newNode) {
		InnerNode<K, V> newInnerNode = parent.split();
		int count = parent.getSlots() / 2;
		int right = count - 1;
		int left = parent.getSlots() - 1;
		boolean found = false;
		for (int i = 0; i < count; i++, right--) {
			if(found || key.compareTo(parent.getKeys()[left]) < 0) {
				newInnerNode.getKeys()[right] = parent.getKeys()[left];
				newInnerNode.getChildren()[right + 1] = parent.getChildren()[left + 1];
				left--;
			} else {
				newInnerNode.getKeys()[right] = key;
				newInnerNode.getChildren()[right + 1] = newNode;
				found = true;
			}
		}
		parent.setSlots(parent.getSlots() - count + (found ? 1 : 0));
		newInnerNode.setSlots(count);
		if (!found) {
			parent.insert(key, newNode);
		}
		parent.setSlots(parent.getSlots() - 1);
		newInnerNode.getChildren()[0] = parent.getChildren()[parent.getSlots() + 1];
		return newInnerNode;
	}

	/*
	 * A very complex method needs documentation. It is used in insertion.
	 */
	private LeafNode<K, V> split(LeafNode<K, V> node, K key, V value) {
		LeafNode<K, V> newLeafNode = node.split();
		int count = (node.getSlots() + 1) / 2;
		int right = count - 1;
		int left = node.getSlots() - 1;
		boolean found = false;
		for (int i = 0; i < count; i++, right--) {
			if(found || key.compareTo(node.getKeys()[left]) < 0) {
				newLeafNode.getKeys()[right] = node.getKeys()[left];
				newLeafNode.getValues()[right] = node.getValues()[left];
				left--;
			} else {
				newLeafNode.getKeys()[right] = key;
				newLeafNode.getValues()[right] = value;
				found = true;
			}
		}
		node.setSlots(node.getSlots() - count + (found ? 1 : 0));
		newLeafNode.setSlots(count);
		if (!found) {
			node.insert(key, value);
		}
		node.setNext(newLeafNode);
		return newLeafNode;
	}
	
	private Node<K, V>[] getSiblings(Node<K, V> node,
			List<Breadcrumb<K, V>> breadcrumbList, int position) {
		InnerNode<K, V> parent =
			(InnerNode<K, V>) getParent(breadcrumbList, position);
		
		int index = getIndex(breadcrumbList, position);
		
		// Should never happen.
		if (index < 0) {
			throw new IllegalArgumentException("Node is not child of parent!");
		}
		
		@SuppressWarnings("unchecked")
		Node<K, V> results[] = new Node[2];
		
		if (index > 0) {
			results[0] = parent.getChildren()[index - 1];
		}
		
		if (index < parent.getSlots()) {
			results[1] = parent.getChildren()[index + 1];
		}

		return results;
	}
	
	private void updateLeafParentKey(Node<K, V> node, int nodeIndex,
			List<Breadcrumb<K, V>> breadcrumbList, int position) {
		InnerNode<K, V> parent = 
			(InnerNode<K, V>) getParent(breadcrumbList, position);
		
		int index = getIndex(breadcrumbList, position) + nodeIndex;
		
		// Should never happen.
		if (index < 0) {
			throw new IllegalArgumentException("Node is not child of parent!");
		}
		
		parent.getKeys()[index] =  node.getKeys()[node.getSlots() - 1];
	}
	
	private K getParentKey(Node<K, V> node, boolean left,
			List<Breadcrumb<K, V>> breadcrumbList, int position) {
		InnerNode<K, V> parent = 
			(InnerNode<K, V>) getParent(breadcrumbList, position - 1);
		
		int index = getIndex(breadcrumbList, position - 1);
		
		// Should never happen.
		if (index < 0) {
			throw new IllegalArgumentException("Node is not child of parent!");
		}
		
		return parent.getKeys()[left ? index - 1 : index];
	}
	
	private void updateParentKey(Node<K, V> node, int nodeIndex, K key,
			List<Breadcrumb<K, V>> breadcrumbList, int position) {
		InnerNode<K, V> parent = 
			(InnerNode<K, V>) getParent(breadcrumbList, position - 1);
		
		int index = getIndex(breadcrumbList, position - 1) + nodeIndex;
		
		// Should never happen.
		if (index < 0) {
			throw new IllegalArgumentException("Node is not child of parent!");
		}

		parent.getKeys()[index] = key;
	}
	
	private LeafNode<K, V> getPreviousLeafNode(LeafNode<K, V> leafNode,
			List<Breadcrumb<K, V>> breadcrumbList, int position) {
		LeafNode<K, V> result = null;
		Node<K, V> node = leafNode;
		
		InnerNode<K, V> parent = null;
		int index = -1;
		int loopPosition = position;
		
		do {
			parent  = (InnerNode<K, V>) getParent(breadcrumbList, loopPosition); //node.getParent();
			
			index = getIndex(breadcrumbList, loopPosition); //search(parent.getChildren(), node);
			
			// Should never happen.
			if (index < 0) {
				throw new IllegalArgumentException("Node is not child of parent!");
			}
			
			node = parent;
			loopPosition--;
		} while (parent != root && index == 0);

		if (parent != root || index != 0) {
			
			node = ((InnerNode<K, V>) node).getChildren()[index - 1];
			while (node instanceof InnerNode<?, ?>) {
				node = ((InnerNode<K, V>) node).getChildren()[node.getSlots()];				
			}
			result = (LeafNode<K, V>) node;
		}
		
		return result;
	}
	
	/*
	 * TODO Complex Method
	 */
	private void removeParentKey(Node<K, V> node,
			List<Breadcrumb<K, V>> breadcrumbList, int position) {
		InnerNode<K, V> parent =
			(InnerNode<K, V>) getParent(breadcrumbList, position);
		
		if (parent != null) {
			int index = getIndex(breadcrumbList, position);
			
			// Should never happen.
			if (index < 0) {
				throw new IllegalArgumentException("Node is not child of parent!");
			}
			
			parent.remove(index);
			
			if (parent != root) {
				if (!parent.hasEnoughSlots()) {
					Node<K, V> siblings[] =
						getSiblings(parent, breadcrumbList, position - 1);
					int siblingIndex = getSiblingIndex(siblings);
					
					if (canGiveSlots(siblings)) {
						int count = (siblings[siblingIndex].getSlots() -
								parent.getSlots()) / 2;
						if (siblingIndex == 0) {
							parent.rightShift(count);
							parent.getKeys()[count - 1] = getParentKey(parent, true, breadcrumbList, position);
							siblings[siblingIndex].copyToRight(parent, count);
						} else {
							parent.getKeys()[parent.getSlots()] = getParentKey(parent, false, breadcrumbList, position);
							siblings[siblingIndex].copyToLeft(parent, count);
							siblings[siblingIndex].leftShift(count);
						}
						parent.setSlots(parent.getSlots() + count);
						siblings[siblingIndex].setSlots(siblings[siblingIndex].getSlots() - count);
						updateParentKey(siblingIndex == 0 ? siblings[0] : parent,
								siblingIndex == 0 ? -1 : 0,
										(siblingIndex == 0 ? siblings[0] : parent).getKeys()[(siblingIndex == 0 ? siblings[0] : parent).getSlots()],
										breadcrumbList, position);
					} else {
				/*
				         b. If both Lleft and Lright have only the minimum number of
				            entries, then L gives its records to one of its siblings
				            and it is removed from the tree. The new leaf will contain
				            no more than the maximum number of entries allowed. This
				            merge process combines two subtrees of the parent, so the
				            separating entry at the parent needs to be removed -- this
				            may in turn cause the parent node to underflow; such an
				            underflow is handled the same way that an underflow of a
				            leaf node.
				*/
						if(siblingIndex == 0) {
							siblings[siblingIndex].getKeys()[siblings[siblingIndex].getSlots()] =
								getParentKey(parent, true, breadcrumbList, position);
							parent.copyToLeft(siblings[siblingIndex], parent.getSlots() + 1);
						} else {
							siblings[siblingIndex].rightShift(siblings[siblingIndex].getSlots());
							siblings[siblingIndex].getKeys()[parent.getSlots()] =
								getParentKey(parent, false, breadcrumbList, position);
							parent.copyToRight(siblings[siblingIndex], parent.getSlots() + 1);
						}
						siblings[siblingIndex].setSlots(siblings[siblingIndex].getSlots() + parent.getSlots() + 1);
						removeParentKey(parent, breadcrumbList, position - 1);
					}
	
				}
			} else {
				/*
		         c. If the last two children of the root merge together into
		            one node, then this merged node becomes the new root and
		            the tree loses a level. 
				 */
				if(parent.getSlots() == 0) {
					root = parent.getChildren()[0];
				}
				
			}
		}
	}
	
	/*
	 * TODO: Complex Method
	 */
	public void remove(K key) {
		/*
		   1. Perform the search process on the key of the record to be
		      deleted. This search will end at a leaf L.
		*/
		List<Breadcrumb<K, V>> breadcrumbList =
			new ArrayList<Breadcrumb<K, V>>(10);
		LeafNode<K, V> leafNode = findLeafNode(key, breadcrumbList);
		int position = breadcrumbList.size() - 1;

		/*
		   2. If the leaf L contains more than the minimum number of elements
		      (more than m/2 - 1), then the index entry for the record to be
		      removed can be safely deleted from the leaf with no further
		      action.
		*/
		int index = Arrays.binarySearch(leafNode.getKeys(), 0,
				leafNode.getSlots(), key, null);
		
		if (index >= 0) {
			leafNode.remove(index);
		}
		
		if (leafNode != root) {
			if (!leafNode.hasEnoughSlots()) {
	
			/*
			   3. If the leaf contains the minimum number of entries, then the
			      deleted entry is replaced with another entry that can take its
			      place while maintaining the correct order. To find such entries,
			      we inspect the two sibling leaf nodes Lleft and Lright adjacent
			      to L -- at most one of these may not exist.
			*/
				Node<K, V> siblings[] =
					getSiblings(leafNode, breadcrumbList, position);
				int siblingIndex = getSiblingIndex(siblings);
				
				if (canGiveSlots(siblings)) {
					int count = (siblings[siblingIndex].getSlots() - 
							leafNode.getSlots()) / 2;
					if (siblingIndex == 0) {
						leafNode.rightShift(count);
						siblings[siblingIndex].copyToRight(leafNode, count);
					} else {
						siblings[siblingIndex].copyToLeft(leafNode, count);
						siblings[siblingIndex].leftShift(count);
					}
					leafNode.setSlots(leafNode.getSlots() + count);
					siblings[siblingIndex].setSlots(siblings[siblingIndex].getSlots() - count);
					updateLeafParentKey(siblingIndex == 0 ? siblings[0] : leafNode, siblingIndex == 0 ? -1 : 0, breadcrumbList, position);
				} else {
			/*
			         b. If both Lleft and Lright have only the minimum number of
			            entries, then L gives its records to one of its siblings
			            and it is removed from the tree. The new leaf will contain
			            no more than the maximum number of entries allowed. This
			            merge process combines two subtrees of the parent, so the
			            separating entry at the parent needs to be removed -- this
			            may in turn cause the parent node to underflow; such an
			            underflow is handled the same way that an underflow of a
			            leaf node.
			*/
					if(siblingIndex == 0) {
						leafNode.copyToLeft(siblings[siblingIndex], leafNode.getSlots());
					} else {
						siblings[siblingIndex].rightShift(leafNode.getSlots());
						leafNode.copyToRight(siblings[siblingIndex], leafNode.getSlots());
					}
					siblings[siblingIndex].setSlots(siblings[siblingIndex].getSlots() + leafNode.getSlots());
					if(siblings[0] == null) {
						LeafNode<K, V> previousLeafNode = 
							getPreviousLeafNode(leafNode, breadcrumbList,
									position);
						if (previousLeafNode != null) {
							previousLeafNode.setNext(leafNode.getNext());
						}
					} else {
						((LeafNode<K,V>) siblings[0]).setNext(leafNode.getNext());
					}
					removeParentKey(leafNode, breadcrumbList, position);
				}
			/*
			         c. If the last two children of the root merge together into
			            one node, then this merged node becomes the new root and
			            the tree loses a level. 
			*/
			}
		} else {
			if (leafNode.getSlots() == 0) {
				root = null;
			}
		}
	}

	private boolean canGiveSlots(Node<K, V>[] siblings) {
		boolean found = false;
		// TODO optimize
		if (siblings[0] == null) {
			 if (siblings[1].canGiveSlots()) {
				 found = true;
			 }
		} else if (siblings[1] == null) {
			if (siblings[0].canGiveSlots()) {
				found = true;
			}
		} else if (siblings[0].getSlots() > siblings[1].getSlots()) {
			if (siblings[0].canGiveSlots()) {
				found = true;
			}
		} else {
			 if (siblings[1].canGiveSlots()) {
				 found = true;
			 }
		}
		return found;
	}
	
	private int getSiblingIndex(Node<K, V> siblings[]) {
		if (siblings[0] == null) {
			return 1;//index = 1;
		} else if (siblings[1] == null) {
			return 0;//index = 0;
		} else if (siblings[0].getSlots() > siblings[1].getSlots()) {
			return 0;//index = 0;
		} else {
			return 1;//index = 1;
		}
		
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
