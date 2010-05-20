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
public abstract class Node<K extends Comparable<K>, V> /*implements Comparable<Node<K, V>>*/ {

	private K keys[];
	private int slots;

	/**
	 * @param keys
	 * @param slots
	 * @param parent
	 */
	public Node(K[] keys, int slots) {
		this.keys = keys;
		this.slots = slots;
	}

	/**
	 * @return the keys
	 */
	public K[] getKeys() {
		return keys;
	}

	/**
	 * @param keys the keys to set
	 */
	public void setKeys(K[] keys) {
		this.keys = keys;
	}

	/**
	 * @return the slots
	 */
	public int getSlots() {
		return slots;
	}

	/**
	 * @param slots the slots to set
	 */
	public void setSlots(int slots) {
		this.slots = slots;
	}

	public boolean isEmpty() {
		return getSlots() == 0;
	}
	
	public boolean isFull() {
		return getSlots() == getKeys().length;
	}
	
	/**
	 * Split the current node if it is full and return the new node. 
	 */
	//public abstract Node<K, V> split();

	protected void checkIsFull() {
		if (!isFull()) {
			throw new IllegalStateException("Cannot split a non full node.");
		}
	}

	public abstract int getDepth();
	
	public abstract boolean hasEnoughSlots();
	
	public abstract boolean canGiveSlots();
	
	public abstract void leftShift(int count);
	
	public abstract void rightShift(int count);
	
	public abstract void copyToLeft(Node<K,V> node, int count);
	
	public abstract void copyToRight(Node<K,V> node, int count);
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toString(0);
	}

	public String toString(int level) {
		StringBuffer buffer = new StringBuffer();
		StringBuffer indent = getIndent(level);
		buffer.append(indent);
		buffer.append(getClass().getName());
		buffer.append('@');
		buffer.append(hashCode());
		
		if (slots > 0) {
			buffer.append('\n');
			buffer.append(indent);
			buffer.append(" keys: \n");
		}

		for (int i = 0; i < slots; i++) {
			if(i > 0) {
				buffer.append('\n');
			}
			buffer.append("  ");
			buffer.append(indent);
			buffer.append(keys[i].toString());
		}
		
		return buffer.toString();
	}

	protected StringBuffer getIndent(int level) {
		StringBuffer indent = new StringBuffer();
		for (int i = 0; i < level; i++) {
			indent.append("  ");
		}
		return indent;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	/*@Override
	public int compareTo(Node<K, V> node) {
		// TODO I do not know if this the right implementation in our case.
		return getKeys()[0].compareTo(node.getKeys()[0]);
	}*/

	/*
	 * Self check used in unit testing.
	 */
	abstract boolean isBalanced();
	
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
	abstract boolean checkCount();
	
	/*
	 * Self check used in unit testing.
	 */
	protected abstract boolean checkRootNode();
	
	/*
	 * Self check used in unit testing.
	 */
	boolean checkKeyOrder() {
		boolean result = true;
		int index = 0;
		while (index < getSlots() - 1) {
			result = result && keys[index].compareTo(keys[index + 1]) <= 0;
			index++;
		}
		return result;
	}
	
	/*
	 * Self check used in unit testing.
	 */
	abstract Node<K, V>[] getLeafNodes();
	
	/*
	 * Self check used in unit testing.
	 */
	int getValuesCount() {
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
	abstract K getLastKey();
	
}
