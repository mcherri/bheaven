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
public class InnerNode<K extends Comparable<K>, V> extends AbstractNode<K, V> {
	private AbstractNode<K, V> children[];
	
	/**
	 * @param maxSlots
	 */
	@SuppressWarnings("unchecked")
	public InnerNode(int maxSlots) {
		super(maxSlots);
		
		children = new AbstractNode[maxSlots + 1];
	}

	/**
	 * @return the child
	 */
	public AbstractNode<K, V> getChild(int index) {
		return children[index];
	}

	/**
	 * @param child the child to set
	 */
	public void setChild(AbstractNode<K, V> child, int index) {
		children[index] = child;
	}
	
	public void insert(K key, AbstractNode<K, V> child) {
		
		int index = getSlots() - 1;
		
		while (index >= 0 && key.compareTo(getKey(index)) < 0) {
			setKey(getKey(index), index + 1);
			setChild(getChild(index + 1), index + 2);

			index--;
		}
		
		setKey(key, index + 1);
		setChild(child, index + 2);
		
		setSlots(getSlots() + 1);
	}
	
	public InnerNode<K, V> split() {
		checkIsFull();
		
		return new InnerNode<K, V>(getMaxSlots());
	}
	
	public void remove(int index) {
		
		for (int i = index; i < getSlots(); i++) {
			if (i < getSlots() - 1) {
				setKey(getKey(i + 1), i);
			}
			setChild(getChild(i + 1), i);
		}
		
		setSlots(getSlots() - 1);
		
	}
	
	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#hasEnoughSlots()
	 */
	@Override
	public boolean hasEnoughSlots() {
		return getSlots() >= (getMaxSlots() - 1) / 2;
	}

	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#canGive()
	 */
	@Override
	public boolean canGiveSlots() {
		return getSlots() - 1 >= (getMaxSlots() - 1) / 2;
	}

	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#leftShift(int)
	 */
	@Override
	public void leftShift(int count) {
		for (int i = 0; i < getSlots() - count; i++) {
			setKey(getKey(i + count), i);
			setChild(getChild(i + count), i);
		}
		
		setChild(getChild(getSlots()), getSlots() - count);
	}

	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#rightShift(int)
	 */
	@Override
	public void rightShift(int count) {
		for (int i = getSlots() - 1; i >= 0 ; i--) {
			setKey(getKey(i), i + count);
			setChild(getChild(i + 1), i + count + 1);
		}
		
		setChild(getChild(0), count);
		
	}

	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#copyToLeft(int)
	 */
	@Override
	public void copyToLeft(AbstractNode<K,V> node, int count) {
		for (int i = 0; i < count; i++) {
			if(i < getSlots()) {
				node.setKey(getKey(i), node.getSlots() + i + 1);
			}
			((InnerNode<K, V>) node).setChild(getChild(i), node.getSlots() + i + 1);
		}
	}

	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#copyToRight(int)
	 */
	@Override
	public void copyToRight(AbstractNode<K,V> node, int count) {
		for (int i = 0; i < count - 1; i++) {
			node.setKey(getKey(getSlots() - count + i + 1), i);
			((InnerNode<K, V>) node).setChild(getChild(getSlots() - count + i + 2), i + 1);
		}
		((InnerNode<K, V>) node).setChild(getChild(getSlots() - count + 1), 0);

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
			buffer.append(" children: \n");
		}
		
		for (int i = 0; i < getSlots() + 1; i++) {
			if(i > 0) {
				buffer.append('\n');
			}
			buffer.append(children[i].toString(level + 1));
		}

		return buffer.toString();
	}

}
