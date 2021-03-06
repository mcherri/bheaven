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
public class Breadcrumb<K extends Comparable<K>, V> {
	private final Node<K, V> node;
	private final int index;
	
	public Breadcrumb(Node<K, V> node, int index) {
		this.node = node;
		this.index = index;
	}
	
	/**
	 * @return the node
	 */
	public Node<K, V> getNode() {
		return node;
	}
	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}
}
