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

import java.util.Arrays;


/**
 *
 */
public abstract class AbstractNode<K extends Comparable<K>, V> /*implements Comparable<Node<K, V>>*/ {

	private K keys[];
	private int slots;

	/**
	 * @param maxSlots
	 */
	@SuppressWarnings("unchecked")
	public AbstractNode(int maxSlots) {
		this.keys = (K[]) new Comparable[maxSlots];
		this.slots = 0;
	}

	/**
	 * @return the key
	 */
	public K getKey(int index) {
		return keys[index];
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(K key, int index) {
		keys[index] = key;
	}
	
	public int getKeyIndex(K key) {
		return Arrays.binarySearch(keys, 0, slots, key, null);
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
	
	public int getMaxSlots() {
		return keys.length;
	}

	public boolean isEmpty() {
		return getSlots() == 0;
	}
	
	public boolean isFull() {
		return getSlots() == keys.length;
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

	public abstract boolean hasEnoughSlots();
	
	public abstract boolean canGiveSlots();
	
	public abstract void leftShift(int count);
	
	public abstract void rightShift(int count);
	
	public abstract void copyToLeft(AbstractNode<K,V> node, int count);
	
	public abstract void copyToRight(AbstractNode<K,V> node, int count);
	
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
	
}
