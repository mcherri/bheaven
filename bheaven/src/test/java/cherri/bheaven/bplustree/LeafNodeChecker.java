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

import cherri.bheaven.bplustree.memory.MemoryLeafNode;

/**
 *
 */
public class LeafNodeChecker<K extends Comparable<K>, V> extends
		AbstractNodeChecker<K, V> {

	/**
	 * @param node
	 */
	LeafNodeChecker(MemoryLeafNode<K, V> node) {
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
	public boolean isBalanced() {
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
	public boolean checkCount() {
		return checkCount(node.getSlots(), node.getMaxSlots());
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
	public Node<K, V>[] getLeafNodes() {
		return new Node[] { node };
	}

	/* (non-Javadoc)
	 * @see cherri.bheaven.bplustree.NodeChecker#getLastKey()
	 */
	@Override
	public K getLastKey() {
		return node.getKey(node.getSlots() - 1);
	}

}
