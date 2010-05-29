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


public class LeafNode<K extends Comparable<K>, V> extends AbstractNode<K, V> {
	private V values[];
	private AbstractNode<K, V> next;
	/*private LeafNode<K, V> previous;*/
	
	/**
	 * @param keys
	 * @param values
	 * @param slots
	 * @param next
	 */
	public LeafNode(V[] values, int maxSlots, AbstractNode<K, V> next) {
		super(maxSlots);
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
	public void setValues(V[] values) {
		this.values = values;
	}

	/**
	 * @return the next
	 */
	public AbstractNode<K, V> getNext() {
		return next;
	}

	/**
	 * @param next the next to set
	 */
	public void setNext(AbstractNode<K, V> next) {
		this.next = next;
	}
	
	
	public void insert(K key, V value) {
		V[] values = getValues();
		
		int index = getSlots() - 1;
		
		while (index >= 0 && key.compareTo(getKey(index)) < 0) {
			setKey(getKey(index), index + 1);
			values[index + 1] = values[index];

			index--;
		}
		
		setKey(key, index + 1);
		values[index + 1] = value;
		
		setSlots(getSlots() + 1);
	}
	
	public LeafNode<K, V> split() {
		checkIsFull();
		
		@SuppressWarnings("unchecked")
		V values[] = (V[]) new Object[getValues().length];

		return new LeafNode<K, V>(values, getMaxSlots(), next);
	}
	
	public void remove(int index) {
		V[] values = getValues();
		
		for (int i = index; i < getSlots() - 1; i++) {
			setKey(getKey(i + 1), i);
			values[i] = values[i + 1];
		}
		
		setSlots(getSlots() - 1);
	}
	
	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#hasEnoughSlots()
	 */
	@Override
	public boolean hasEnoughSlots() {
		return getSlots() >= (getMaxSlots() + 1) / 2;
	}

	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#canGive()
	 */
	@Override
	public boolean canGiveSlots() {
		return getSlots() - 1 >= (getMaxSlots() + 1) / 2;
	}

	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#leftShift(int)
	 */
	@Override
	public void leftShift(int count) {
		for (int i = 0; i < getSlots() - count; i++) {
			setKey(getKey(i + count), i);
			getValues()[i] = getValues()[i + count]; 
		}
	}

	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#rightShift(int)
	 */
	@Override
	public void rightShift(int count) {
		for (int i = getSlots() - 1; i >= 0 ; i--) {
			setKey(getKey(i), i + count);
			getValues()[i + count] = getValues()[i];
		}
	}

	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#copyToLeft(int)
	 */
	@Override
	public void copyToLeft(AbstractNode<K,V> node, int count) {
		for (int i = 0; i < count; i++) {
			node.setKey(getKey(i), node.getSlots() + i);
			((LeafNode<K, V>) node).getValues()[node.getSlots() + i] =
				getValues()[i];
		}
	}

	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#copyToRight(int)
	 */
	@Override
	public void copyToRight(AbstractNode<K,V> node, int count) {
		for (int i = 0; i < count; i++) {
			node.setKey(getKey(getSlots() - count + i), i);
			((LeafNode<K, V>) node).getValues()[i] =
				getValues()[getSlots() - count + i];
		}
	}

	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#toString(int)
	 */
	@Override
	public String toString(int level) {
		StringBuffer buffer = new StringBuffer(super.toString(level));
		StringBuffer indent = getIndent(level);
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
		buffer.append(next == null ? "null" : next.getKey(0));
		
		return buffer.toString();
	}

}
