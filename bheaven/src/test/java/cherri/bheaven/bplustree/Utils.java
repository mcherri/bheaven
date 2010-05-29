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
public class Utils {

	public static AbstractNode<String, String>[] getInnerNodes(
			AbstractNode<String, String> parent, int size, int slots) {
		
		@SuppressWarnings("unchecked")
		AbstractNode<String, String> nodes[] = new AbstractNode[size];
		AbstractNode<String, String> next = null;
		
		for (int i = slots - 1; i >= 0; i--) {
			nodes[i] = new InnerNode<String, String>(null, size);
			AbstractNode<String, String> children[] = getLeafNodes(nodes[i],
					size, slots, String.valueOf((char) ('a' + i)), "v"
							+ ((char) ('a' + i)));
			
			((LeafNode<String, String>) children[slots - 1]).setNext(next);
			
			setChildrenKeys(nodes[i], children, size, slots);
			((InnerNode<String, String>) nodes[i]).setChildren(children);
			
			next = children[0];
		}
		
		return nodes;
	}

	public static void setChildrenKeys(AbstractNode<String, String> node,
			AbstractNode<String, String> children[],
			int size, int slots) {
		for (int j = 0; j < slots - 1; j++) {
	
			node.setKey(AbstractNodeChecker.getNodeChecker(children[j]).getLastKey(), j);
		}
		node.setSlots(slots - 1);
	}

	public static AbstractNode<String, String>[] getLeafNodes(
			AbstractNode<String, String> parent, int size, int slots,
			String key, String value) {
		
		@SuppressWarnings("unchecked")
		AbstractNode<String, String> nodes[] = new AbstractNode[size];
		AbstractNode<String, String> next = null;
		
		for (int i = slots - 1; i >= 0; i--) {
			nodes[i] = new LeafNode<String, String>(
					generateStrings(size * 2, slots * 2, value + i), 
					size * 2, next);
			generateStrings(nodes[i], slots * 2, key + i);
			next = nodes[i]; 
		}
		
		return nodes;
	}

	public static String[] generateStrings(int size, int slots,
			String prefix) {
		String result[] = new String[size];
		
		for (int i = 0; i < slots; i++) {
			result[i] = prefix + i;
		}
		
		return result;
	}
	
	public static void generateStrings(AbstractNode<String, String> node,
			int slots, String prefix) {
		
		for (int i = 0; i < slots; i++) {
			node.setKey(prefix + i, i);
		}
		
		node.setSlots(slots);
	}
	
}
