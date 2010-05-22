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

	public static Node<String, String>[] getInnerNodes(
			Node<String, String> parent, int size, int slots) {
		
		@SuppressWarnings("unchecked")
		Node<String, String> nodes[] = new Node[size];
		Node<String, String> next = null;
		
		for (int i = slots - 1; i >= 0; i--) {
			nodes[i] = new InnerNode<String, String>(null, null, slots - 1);
			Node<String, String> children[] = Utils.getLeafNodes(nodes[i],
					size, slots, String.valueOf((char) ('a' + i)), "v"
							+ ((char) ('a' + i)));
			
			((LeafNode<String, String>) children[slots - 1]).setNext(next);
			
			String keys[] = Utils.getChildrenKeys(children, size, slots);
			nodes[i].setKeys(keys);
			((InnerNode<String, String>) nodes[i]).setChildren(children);
			
			next = children[0];
		}
		
		return nodes;
	}

	public static String[] getChildrenKeys(Node<String, String> children[],
			int size, int slots) {
		String keys[] = new String[size - 1];
		for (int j = 0; j < slots - 1; j++) {
	
			keys[j] = NodeChecker.getNodeChecker(children[j]).getLastKey();
		}
		return keys;
	}

	public static Node<String, String>[] getLeafNodes(
			Node<String, String> parent, int size, int slots,
			String key, String value) {
		
		@SuppressWarnings("unchecked")
		Node<String, String> nodes[] = new Node[size];
		Node<String, String> next = null;
		
		for (int i = slots - 1; i >= 0; i--) {
			nodes[i] = new LeafNode<String, String>(
					Utils.generateStrings(size * 2, slots * 2, key + i), 
					Utils.generateStrings(size * 2, slots * 2, value + i), 
					slots * 2, next);
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
	
}
