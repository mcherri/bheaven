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
public class LeafNodeChecker<K extends Comparable<K>, V> extends
		NodeChecker<K, V> {

	/**
	 * @param node
	 */
	LeafNodeChecker(LeafNode<K, V> node) {
		super(node);
	}

	/* (non-Javadoc)
	 * @see cherri.bheaven.bplustree.NodeChecker#getDepth()
	 */
	@Override
	public int getDepth() {
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see cherri.bheaven.bplustree.NodeChecker#isBalanced()
	 */
	@Override
	boolean isBalanced() {
		return true;
	}

	/* (non-Javadoc)
	 * @see cherri.bheaven.bplustree.NodeChecker#isBalanced(int)
	 */
	@Override
	protected boolean isBalanced(int depth) {
		return depth == 0;
	}

	/* (non-Javadoc)
	 * @see cherri.bheaven.bplustree.NodeChecker#checkCount()
	 */
	@Override
	boolean checkCount() {
		return checkCount(node.getSlots(), node.getKeys().length);
	}

	/* (non-Javadoc)
	 * @see cherri.bheaven.bplustree.NodeChecker#checkRootNode()
	 */
	@Override
	protected boolean checkRootNode() {
		return true;
	}

	/* (non-Javadoc)
	 * @see cherri.bheaven.bplustree.NodeChecker#getLeafNodes()
	 */
	@Override
	@SuppressWarnings("unchecked")
	Node<K, V>[] getLeafNodes() {
		return new Node[] { node };
	}

	/* (non-Javadoc)
	 * @see cherri.bheaven.bplustree.NodeChecker#getLastKey()
	 */
	@Override
	K getLastKey() {
		return node.getKeys()[node.getSlots() - 1];
	}

}
