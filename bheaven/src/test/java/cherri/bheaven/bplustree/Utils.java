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

import cherri.bheaven.bplustree.memory.MemoryInnerNode;
import cherri.bheaven.bplustree.memory.MemoryLeafNode;

/**
 *
 */
public class Utils {

	public static void setInnerNodes(
			InnerNode<String, String> parent, int size, int slots) {
		
		Node<String, String> next = null;
		
		for (int i = slots - 1; i >= 0; i--) {
			MemoryInnerNode<String, String> node = new MemoryInnerNode<String, String>(size);
			setLeafNodes(node,
					size, slots, String.valueOf((char) ('a' + i)), "v"
							+ ((char) ('a' + i)));
			
			((LeafNode<String, String>) node.getChild(slots - 1)).setNext(next);
			
			setChildrenKeys(node, slots);
			
			next = node.getChild(0);
			parent.setChild(node, i);
		}
		
	}

	public static void setChildrenKeys(MemoryInnerNode<String, String> node,
			int slots) {
		for (int j = 0; j < slots - 1; j++) {
	
			node.setKey(AbstractNodeChecker.getNodeChecker(node.getChild(j)).getLastKey(), j);
		}
		node.setSlots(slots - 1);
	}

	public static void setLeafNodes(
			InnerNode<String, String> parent, int size, int slots,
			String key, String value) {
		
		Node<String, String> next = null;
		
		for (int i = slots - 1; i >= 0; i--) {
			Node<String, String> node =
				new MemoryLeafNode<String, String>(size * 2, next);
			generateStrings(node, slots * 2, key + i);
			generateValueStrings((LeafNode<String, String>) node,
					slots * 2, value + i);
			parent.setChild(node, i);
			next = node; 
		}
		
	}

	public static void generateStrings(Node<String, String> node,
			int slots, String prefix) {
		
		for (int i = 0; i < slots; i++) {
			node.setKey(prefix + i, i);
		}
		
		node.setSlots(slots);
	}
	
	public static void generateValueStrings(LeafNode<String, String> node,
			int slots, String prefix) {
		
		for (int i = 0; i < slots; i++) {
			node.setValue(prefix + i, i);
		}
		
	}
}
