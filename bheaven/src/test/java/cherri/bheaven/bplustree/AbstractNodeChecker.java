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
public abstract class AbstractNodeChecker<K extends Comparable<K>, V> {
	
	protected Node<K, V> node;
	
	/**
	 * @param node
	 */
	AbstractNodeChecker(Node<K, V> node) {
		this.node = node;
	}
	
	public static <K extends Comparable<K>, V> AbstractNodeChecker<K, V> getNodeChecker(
			Node<K, V> node) {
		if(node instanceof InnerNode<?, ?>) {
			return new InnerNodeChecker<K, V>((InnerNode<K, V>) node);
		} else {
			return new LeafNodeChecker<K, V>((LeafNode<K, V>) node);
		}
	}

	/*
	 * Self check used in unit testing.
	 */
	public abstract int getDepth();
	
	/*
	 * Self check used in unit testing.
	*
	 * Recursively check that the given depth in the tree depth.
	 * TODO: Elaborate.
	 */
	public abstract boolean isBalanced();
	
	/*
	 * Self check used in unit testing.
	 */
	protected abstract boolean isBalanced(int depth);
	
	/*
	 * Self check used in unit testing.
	 */
	protected boolean checkCount(int count, int length) {
		return count >= (length + 1) /2 && count <= length;
	}

	/*
	 * Self check used in unit testing.
	 */
	public abstract boolean checkCount();
	
	/*
	 * Self check used in unit testing.
	 */
	protected abstract boolean checkRootNode();
	
	/*
	 * Self check used in unit testing.
	 */
	public boolean checkKeyOrder() {
		boolean result = true;
		int index = 0;
		K[] keys = node.getKeys();
		while (index < node.getSlots() - 1) {
			result = result && keys[index].compareTo(keys[index + 1]) <= 0;
			index++;
		}
		return result;
	}
	
	/*
	 * Self check used in unit testing.
	 */
	public abstract Node<K, V>[] getLeafNodes();
	
	/*
	 * Self check used in unit testing.
	 */
	public int getValuesCount() {
		Node<K, V> nodes[] = getLeafNodes();
		
		int count = 0;
		
		for (int i = 0; i < nodes.length; i++) {
			count += nodes[i].getSlots();
		}
		
		return count;
	}
	
	/*
	 * Self check used in unit testing.
	 */
	public abstract K getLastKey();
	

}
