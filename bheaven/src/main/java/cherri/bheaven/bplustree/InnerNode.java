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
public class InnerNode<K extends Comparable<K>, V> extends Node<K, V> {
	private Node<K, V> children[];
	
	/**
	 * @param keys
	 * @param children
	 * @param slots
	 * @param parent
	 */
	public InnerNode(K[] keys, Node<K, V>[] children, int slots) {
		super(keys, slots);
		
		checkChildrenAreValid(children);
		this.children = children;
	}

	private void checkChildrenAreValid(Node<K, V>[] children) {
		if (getKeys() != null && children != null
				&& getKeys().length + 1 != children.length) {
			throw new IllegalArgumentException("Keys should be less that " +
					"children by 1");
		}
	}

	/**
	 * @return the children
	 */
	public Node<K, V>[] getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(Node<K, V>[] children) {
		checkChildrenAreValid(children);
		this.children = children;
	}
	
	public void insert(K key, Node<K, V> child) {
		K[] keys = getKeys();
		Node<K, V>[] children = getChildren();
		
		int index = getSlots() - 1;
		
		while (index >= 0 && key.compareTo(keys[index]) < 0) {
			keys[index + 1] = keys[index];
			children[index + 2] = children[index + 1];

			index--;
		}
		
		keys[index + 1] = key;
		children[index + 2] = child;
		
		setSlots(getSlots() + 1);
	}
	
	public InnerNode<K, V> split() {
		checkIsFull();
		
		@SuppressWarnings("unchecked")
		K keys[] = (K[]) new Comparable[getKeys().length];
		@SuppressWarnings("unchecked")
		Node<K, V> children[] = new Node[getChildren().length];
		
		return new InnerNode<K, V>(keys, children, 0);
	}
	
	public void remove(int index) {
		K[] keys = getKeys();
		Node<K, V>[] children = getChildren();
		
		for (int i = index; i < getSlots(); i++) {
			if (i < getSlots() - 1) {
				keys[i] = keys[i + 1];
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
		return getSlots() >= (getKeys().length - 1) / 2;
	}

	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#canGive()
	 */
	@Override
	public boolean canGiveSlots() {
		return getSlots() - 1 >= (getKeys().length - 1) / 2;
	}

	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#leftShift(int)
	 */
	@Override
	public void leftShift(int count) {
		for (int i = 0; i < getSlots() - count; i++) {
			getKeys()[i] = getKeys()[i + count]; 
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
			getKeys()[i + count] = getKeys()[i];
			getChildren()[i + count + 1] = getChildren()[i + 1];
		}
		getChildren()[count] = getChildren()[0];
		
	}

	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#copyToLeft(int)
	 */
	@Override
	public void copyToLeft(Node<K,V> node, int count) {
		for (int i = 0; i < count; i++) {
			if(i < getSlots()) {
				node.getKeys()[node.getSlots() + i + 1] =
					getKeys()[i];
			}
			((InnerNode<K, V>) node).getChildren()[node.getSlots() + i + 1] =
				getChildren()[i];
		}
	}

	/* (non-Javadoc)
	 * @see com.cherri.bplustree.Node#copyToRight(int)
	 */
	@Override
	public void copyToRight(Node<K,V> node, int count) {
		for (int i = 0; i < count - 1; i++) {
			node.getKeys()[i] = 
				getKeys()[getSlots() - count + i + 1];
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
