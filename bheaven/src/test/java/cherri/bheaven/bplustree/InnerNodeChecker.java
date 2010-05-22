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
public class InnerNodeChecker<K extends Comparable<K>, V> extends
		NodeChecker<K, V> {

	InnerNodeChecker(InnerNode<K, V> node) {
		super(node);
	}

	/* (non-Javadoc)
	 * @see cherri.bheaven.bplustree.NodeChecker#getDepth()
	 */
	@Override
	public int getDepth() {
		Node<K, V> firstChild = ((InnerNode<K, V>) node).getChildren()[0];
		return NodeChecker.getNodeChecker(firstChild).getDepth() + 1;
	}

	/* (non-Javadoc)
	 * @see cherri.bheaven.bplustree.NodeChecker#isBalanced(int)
	 */
	@Override
	protected boolean isBalanced(int depth) {
		boolean result = true;
		Node<K, V> children[] = ((InnerNode<K, V>) node).getChildren();
		for (int i = 0; result && i < node.getSlots() + 1; i++) {
			NodeChecker<K, V> nodeChecker =
				NodeChecker.getNodeChecker(children[i]);
			result = result && nodeChecker.isBalanced(depth - 1);
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see cherri.bheaven.bplustree.NodeChecker#isBalanced()
	 */
	@Override
	boolean isBalanced() {
		return isBalanced(getDepth());
	}
	
	/* (non-Javadoc)
	 * @see cherri.bheaven.bplustree.NodeChecker#checkCount()
	 */
	@Override
	boolean checkCount() {
		Node<K, V> children[] = ((InnerNode<K, V>) node).getChildren();
		boolean result = checkCount(node.getSlots() + 1, children.length);
		for (int i = 0; result && i < node.getSlots() + 1; i++) {
			NodeChecker<K, V> nodeChecker =
				NodeChecker.getNodeChecker(children[i]);
			result = result && nodeChecker.checkCount();
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see cherri.bheaven.bplustree.NodeChecker#checkRootNode()
	 */
	@Override
	protected boolean checkRootNode() {
		return node.getSlots() > 0;
	}

	/* (non-Javadoc)
	 * @see cherri.bheaven.bplustree.NodeChecker#checkKeyOrder()
	 */
	@Override
	boolean checkKeyOrder() {
		boolean result = super.checkKeyOrder();
		
		Node<K, V> children[] = ((InnerNode<K, V>) node).getChildren();
		for (int i = 0; result && i < node.getSlots() + 1; i++) {
			NodeChecker<K, V> nodeChecker =
				NodeChecker.getNodeChecker(children[i]);
			result = result && nodeChecker.checkKeyOrder();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see cherri.bheaven.bplustree.NodeChecker#getLeafNodes()
	 */
	@Override
	Node<K, V>[] getLeafNodes() {
		@SuppressWarnings("unchecked")
		Node<K, V> array[][] = new Node[node.getSlots() + 1][];
		
		Node<K, V> children[] = ((InnerNode<K, V>) node).getChildren();
		for (int i = 0; i < node.getSlots() + 1; i++) {
			NodeChecker<K, V> nodeChecker =
				NodeChecker.getNodeChecker(children[i]);
			array[i] = nodeChecker.getLeafNodes();
		}
		return merge(array);
	}
	
	private Node<K, V>[] merge(Node<K, V> array[][]) {
		int length = 0;
		
		// Find the length.
		for (int i = 0; i < array.length; i++) {
			length += array[i].length;
		}
		
		@SuppressWarnings("unchecked")
		Node<K, V> nodes[] = new Node[length];
		
		int pos = 0;
		for (int i = 0; i < array.length; i++) {
			System.arraycopy(array[i], 0, nodes, pos, array[i].length);
			pos += array[i].length;
		}
		
		return nodes;
	}

	/* (non-Javadoc)
	 * @see cherri.bheaven.bplustree.NodeChecker#getLastKey()
	 */
	@Override
	K getLastKey() {
		Node<K, V> lastChild =
			((InnerNode<K, V>) node).getChildren()[node.getSlots()];
		NodeChecker<K, V> nodeChecker = NodeChecker.getNodeChecker(lastChild);
		return nodeChecker.getLastKey();
	}

	/*
	 * Self check used in unit testing.
	 */
	boolean checkLastKey() {
		boolean result = true;
		
		K keys[] = node.getKeys();
		for (int i = 0; result && i < node.getSlots(); i++) {
			NodeChecker<K, V> nodeChecker =
				NodeChecker.getNodeChecker(((InnerNode<K, V>) node).getChildren()[i]);
			result = result && keys[i].compareTo(nodeChecker.getLastKey()) >= 0;
		}
		return result;
	}

}
