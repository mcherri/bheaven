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
	 * @param keys
	 * @param children
	 * @param maxSlots
	 */
	public InnerNode(AbstractNode<K, V>[] children, int maxSlots) {
		super(maxSlots);
		
		checkChildrenAreValid(children);
		this.children = children;
	}

	private void checkChildrenAreValid(AbstractNode<K, V>[] children) {
		if (children != null
				&& getMaxSlots() + 1 != children.length) {
			throw new IllegalArgumentException("Keys should be less that " +
					"children by 1");
		}
	}

	/**
	 * @return the children
	 */
	public AbstractNode<K, V>[] getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(AbstractNode<K, V>[] children) {
		this.children = children;
	}
	
	public void insert(K key, AbstractNode<K, V> child) {
		AbstractNode<K, V>[] children = getChildren();
		
		int index = getSlots() - 1;
		
		while (index >= 0 && key.compareTo(getKey(index)) < 0) {
			setKey(getKey(index), index + 1);
			children[index + 2] = children[index + 1];

			index--;
		}
		
		setKey(key, index + 1);
		children[index + 2] = child;
		
		setSlots(getSlots() + 1);
	}
	
	public InnerNode<K, V> split() {
		checkIsFull();
		
		@SuppressWarnings("unchecked")
		AbstractNode<K, V> children[] = new AbstractNode[getChildren().length];
		
		return new InnerNode<K, V>(children, getMaxSlots());
	}
	
	public void remove(int index) {
		AbstractNode<K, V>[] children = getChildren();
		
		for (int i = index; i < getSlots(); i++) {
			if (i < getSlots() - 1) {
				setKey(getKey(i + 1), i);
			}
			children[i] = children[i + 1];
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
			getChildren()[i] = getChildren()[i + count]; 
		}
		getChildren()[getSlots() - count] = getChildren()[getSlots()];
	}

	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#rightShift(int)
	 */
	@Override
	public void rightShift(int count) {
		for (int i = getSlots() - 1; i >= 0 ; i--) {
			setKey(getKey(i), i + count);
			getChildren()[i + count + 1] = getChildren()[i + 1];
		}
		getChildren()[count] = getChildren()[0];
		
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
			((InnerNode<K, V>) node).getChildren()[node.getSlots() + i + 1] =
				getChildren()[i];
		}
	}

	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#copyToRight(int)
	 */
	@Override
	public void copyToRight(AbstractNode<K,V> node, int count) {
		for (int i = 0; i < count - 1; i++) {
			node.setKey(getKey(getSlots() - count + i + 1), i);
			((InnerNode<K, V>) node).getChildren()[i + 1] =
				getChildren()[getSlots() - count + i + 2];
		}
		((InnerNode<K, V>) node).getChildren()[0] = 
			getChildren()[getSlots() - count + 1];

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
