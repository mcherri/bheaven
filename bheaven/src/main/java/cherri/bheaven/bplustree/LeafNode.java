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


public class LeafNode<K extends Comparable<K>, V> extends Node<K, V> {
	private V values[];
	private Node<K, V> next;
	/*private LeafNode<K, V> previous;*/
	
	/**
	 * @param keys
	 * @param values
	 * @param slots
	 * @param parent
	 * @param next
	 */
	public LeafNode(final K[] keys, final V[] values, final int slots,
			final Node<K, V> parent, final Node<K, V> next) {
		super(keys, slots, parent);
		this.values = values;
		this.next = next;
	}
	
	/**
	 * @return the values
	 */
	public V[] getValues() {
		return values;
	}
	
	/**
	 * @param values the values to set
	 */
	public void setValues(final V[] values) {
		this.values = values;
	}

	/**
	 * @return the next
	 */
	public Node<K, V> getNext() {
		return next;
	}

	/**
	 * @param next the next to set
	 */
	public void setNext(final Node<K, V> next) {
		this.next = next;
	}
	
	
	public void insert(final K key, final V value) {
		K[] keys = getKeys();
		V[] values = getValues();
		
		int index = getSlots() - 1;
		
		while (index >= 0 && key.compareTo(keys[index]) < 0) {
			keys[index + 1] = keys[index];
			values[index + 1] = values[index];

			index--;
		}
		
		keys[index + 1] = key;
		values[index + 1] = value;
		
		setSlots(getSlots() + 1);
	}
	
	public LeafNode<K, V> split() {
		checkIsFull();
		
		@SuppressWarnings("unchecked")
		final K keys[] = (K[]) new Comparable[getKeys().length];
		@SuppressWarnings("unchecked")
		final V values[] = (V[]) new Object[getValues().length];

		return new LeafNode<K, V>(keys, values, 0, getParent(), next);
	}
	
	public void remove(final int index) {
		K[] keys = getKeys();
		V[] values = getValues();
		
		for (int i = index; i < getSlots() - 1; i++) {
			keys[i] = keys[i + 1];
			values[i] = values[i + 1];
		}
		
		setSlots(getSlots() - 1);
	}
	
	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#getDepth()
	 */
	@Override
	public int getDepth() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#hasEnoughSlots()
	 */
	@Override
	public boolean hasEnoughSlots() {
		return getSlots() >= (getKeys().length + 1) / 2;
	}

	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#canGive()
	 */
	@Override
	public boolean canGiveSlots() {
		return getSlots() - 1 >= (getKeys().length + 1) / 2;
	}

	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#leftShift(int)
	 */
	@Override
	public void leftShift(final int count) {
		for (int i = 0; i < getSlots() - count; i++) {
			getKeys()[i] = getKeys()[i + count]; 
			getValues()[i] = getValues()[i + count]; 
		}
	}

	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#rightShift(int)
	 */
	@Override
	public void rightShift(final int count) {
		for (int i = getSlots() - 1; i >= 0 ; i--) {
			getKeys()[i + count] = getKeys()[i];
			getValues()[i + count] = getValues()[i];
		}
	}

	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#copyToLeft(int)
	 */
	@Override
	public void copyToLeft(final Node<K,V> node, final int count) {
		for (int i = 0; i < count; i++) {
			node.getKeys()[node.getSlots() + i] = getKeys()[i];
			((LeafNode<K, V>) node).getValues()[node.getSlots() + i] =
				getValues()[i];
		}
	}

	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#copyToRight(int)
	 */
	@Override
	public void copyToRight(final Node<K,V> node, final int count) {
		for (int i = 0; i < count; i++) {
			node.getKeys()[i] = getKeys()[getSlots() - count + i];
			((LeafNode<K, V>) node).getValues()[i] =
				getValues()[getSlots() - count + i];
		}
	}

	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#toString(int)
	 */
	@Override
	public String toString(final int level) {
		final StringBuffer buffer = new StringBuffer(super.toString(level));
		final StringBuffer indent = getIndent(level);
		buffer.append('\n');
		
		if (getSlots() > 0) {
			buffer.append(indent);
			buffer.append(" values: \n");
		}
		
		for (int i = 0; i < getSlots(); i++) {
			if(i > 0) {
				buffer.append('\n');
			}
			buffer.append("  ");
			buffer.append(indent);
			buffer.append(values[i].toString());
		}
		
		buffer.append('\n');
		buffer.append(indent);
		buffer.append(" next: ");
		buffer.append(next == null ? "null" : next.getKeys()[0]);
		
		return buffer.toString();
	}

	boolean isBalanced() {
		return true;
	}
	
	protected boolean isBalanced(final int depth) {
		return depth == 0;
	}
	
	
	/*boolean checkKeysCount() {
		return slots >= (keys.length + 1) /2 && slots <= keys.length;
	}*/

	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#checkCount()
	 */
	@Override
	boolean checkCount() {
		return checkCount(getSlots(), getKeys().length);
	}

	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#checkRootNode()
	 */
	@Override
	protected boolean checkRootNode() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#getLeafNodes()
	 */
	@Override
	@SuppressWarnings("unchecked")
	Node<K, V>[] getLeafNodes() {
		return new Node[] { this };
	}

	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#getLastKey()
	 */
	@Override
	K getLastKey() {
		return getKeys()[getSlots() - 1];
	}

}
