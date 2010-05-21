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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class BPlusTreeTest {
	
	private static final String KEY = "key";
	private static final String VALUE = "value";
	private static final String B_TREE_SHOULD_RETURN = "B+ Tree should return \"";
	private static final String B_TREE_SHOULD_RETURN_REST = "\".";
	private LeafNode<String, String> root1;
	private InnerNode<String, String> root2;
	private InnerNode<String, String> root3;
	private BPlusTree<String, String> tree1;
	
	@Before
	public void setUp() {
		tree1 = new BPlusTree<String, String>(4, 5);
		
		root1 = new LeafNode<String, String>(
					Utils.generateStrings(10, 5, "a"), 
					Utils.generateStrings(10, 5, "va"), 
					5, null);
		
		root2 = new InnerNode<String, String>(null, null, 2 - 1);
		Node<String, String> children2[] = Utils.getLeafNodes(root2, 4, 2, "a",
				"va");
		String keys2[] = Utils.getChildrenKeys(children2, 4, 2);
		root2.setKeys(keys2);
		root2.setChildren(children2);
		
		root3 = new InnerNode<String, String>(null, null, 3 - 1); 
		Node<String, String> children3[] = Utils.getInnerNodes(root3, 5, 3);
		String keys3[] = Utils.getChildrenKeys(children3, 5, 3);
		root3.setKeys(keys3);
		root3.setChildren(children3);
		
	}

	private void assertThatTreeIsValid(BPlusTree<String, String> tree) {
		BPlusTreeChecker<String, String> checker =
			new BPlusTreeChecker<String, String>(tree);
		assertThat("B+Tree is not valid:\n" + checker.getInvalidReason(),
				checker.isValid(), is(true));
	}
	
	/*@Test
	public void SysOut() {
		System.out.println(root1);
		System.out.println(root2);
		System.out.println(root3);
	}*/
	
	@Test
	public void bPlusTreeShouldReturnNullWhenGettingANonExistantKey() {
		BPlusTree<String, String> tree = new BPlusTree<String, String>(4, 5);
		
		assertThat("B+ Tree should return null when getting non exsistant key.",
				tree.get("test"), is(nullValue()));
		
		assertThatTreeIsValid(tree);
	}

	@Test
	public void bPlusTreeShouldReturnTheCorrespondingKeyValue() {
		
		ReflectionTestUtils.setField(tree1, Constants.ROOT, root1);
		
		for (int i = 0; i < 5; i++) {
			assertThat(
					B_TREE_SHOULD_RETURN + "va" + i + B_TREE_SHOULD_RETURN_REST,
					tree1.get("a" + i), is("va" + i));
		}
		
		assertThatTreeIsValid(tree1);
		
		ReflectionTestUtils.setField(tree1, Constants.ROOT, root2);
		
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 4; j++) {
				assertThat(B_TREE_SHOULD_RETURN + "va" + i + j
						+ B_TREE_SHOULD_RETURN_REST, tree1.get("a" + i + j),
						is("va" + i + j));
			}
		}
		
		assertThatTreeIsValid(tree1);
		
		ReflectionTestUtils.setField(tree1, Constants.ROOT, root3);
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 5; k++) {
					assertThat(B_TREE_SHOULD_RETURN + "v"
							+ ((char) ('a' + i)) + j + k
							+ B_TREE_SHOULD_RETURN_REST, tree1.get(String
							.valueOf((char) ('a' + i))
							+ j + k), is("v" + ((char) ('a' + i))
							+ j + k));
				}
			}
		}
		
		assertThatTreeIsValid(tree1);

		BPlusTree<String, String> tree2 = new BPlusTree<String, String>(4, 5);
		
		tree2.put(KEY, VALUE);
		
		assertThat("B+ Tree should return \"value\".",
				tree2.get(KEY), is(VALUE));
	
		assertThatTreeIsValid(tree2);
	}
	
	@Test
	public void insertingInAnEmtpyBPlusTreeShouldInitateTheRootNode() {
		BPlusTree<String, String> tree = new BPlusTree<String, String>(5, 6);
		
		tree.put(KEY, VALUE);
		
		@SuppressWarnings("unchecked")
		Node<String, String> node =
			(Node<String, String>) ReflectionTestUtils.getField(tree, Constants.ROOT);
		
		assertThat(
				"Inserting in an empty B+ tree should initiate the root node" +
				" as a leaf node.",
				node, is(instanceOf(LeafNode.class)));
		
		assertThatTreeIsValid(tree);
	}
	
	@SuppressWarnings("unchecked")
	private void assertThatValuesCountIs(BPlusTree<String, String> tree,
			int count) {
		Node<String, String> node =
			(Node<String, String>) ReflectionTestUtils.getField(tree, Constants.ROOT);

		assertThat("Tree has an incorrect values count.", node.getValuesCount(), is(count));
	}
	
	private void forwardFill(BPlusTree<String, String> tree, int count) {
		for (int i = 0; i < count; i++) {
			tree.put("a" + i, "va" + i);
			
			assertThatTreeIsValid(tree);
			assertThatValuesCountIs(tree, i + 1);
		}
	}

	private void reverseFill(BPlusTree<String, String> tree, int count) {
		for (int i = count - 1; i >= 0; i--) {
			tree.put("a" + i, "va" + i);
			
			assertThatTreeIsValid(tree);
			assertThatValuesCountIs(tree, count - i);
		}
	}
	
	private void forwardEmpty(BPlusTree<String, String> tree, int count) {
		
		//int valuesCount = tree.get
		for (int i = 0; i < count; i++) {
			tree.remove("a" + i);
			
			assertThatTreeIsValid(tree);
			// TODO
			//assertThatValuesCountIs(tree, i + 1);
		}
	}
	
	private void reverseEmpty(BPlusTree<String, String> tree, int count) {
		for (int i = count - 1; i >= 0; i--) {
			tree.remove("a" + i);
			
			assertThatTreeIsValid(tree);
			// TODO
			//assertThatValuesCountIs(tree, count - i);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void assertThatSplitOccured(String message,
			BPlusTree<String, String> tree, int count, int depth) {
		Node<String, String> node = (Node<String, String>) ReflectionTestUtils
				.getField(tree, Constants.ROOT);
		
		assertThat(message, node
				.getSlots() + 1, is(count));
		
		//System.out.println(node);
		
		int treeDepth = 0;
		
		while (node instanceof InnerNode<?, ?>) {
			node = ((InnerNode<String, String>) node).getChildren()[0];
			treeDepth++;
		}
		
		assertThat(message, treeDepth, is(depth));
	}

	private void fillAndTest(String message, int count,
			int forwardSplit, int reverseSplit, int depth) {
		BPlusTree<String, String> tree = new BPlusTree<String, String>(4, 4);

		forwardFill(tree, count);

		assertThatSplitOccured(message, tree, forwardSplit, depth);

		tree = new BPlusTree<String, String>(4, 4);

		reverseFill(tree, count);

		assertThatSplitOccured(message, tree, reverseSplit, depth);
	}
	
	private void fillEmptyAndTest(String message, int order, int fillCount,
			int emptyCount, int forwardSplit, int reverseSplit,
			int forwardDepth, int reverseDepth) {
		BPlusTree<String, String> tree = new BPlusTree<String, String>(order, order);

		forwardFill(tree, fillCount);
		forwardEmpty(tree, emptyCount);
		
		assertThatSplitOccured(message, tree, forwardSplit, forwardDepth);
		
		tree = new BPlusTree<String, String>(order, order);

		reverseFill(tree, fillCount);
		reverseEmpty(tree, emptyCount);

		assertThatSplitOccured(message, tree, reverseSplit, reverseDepth);
		
	}
	
	@Test
	public void insertingInAFullRootLeafNodeShouldSplitIt() {
		fillAndTest("Inserting in a full root leaf should split it.", 5, 2, 2,
				1);
	}

	@Test
	public void insertingInAFullLeafNodeShouldSplitIt() {
		fillAndTest("Inserting in a full leaf should split it.", 9, 3, 4, 1);
	}
	
	@Test
	public void insertingInAFullInnerNodeShouldSplitIt() {
		fillAndTest("Inserting in a full inner node should split it.", 17, 2,
				3, 2);
	}
	
	@Test
	public void insertingInAFullRootInnerNodeShouldSplitIt() {
		fillAndTest("Inserting in a full root inner node should split it.", 49,
				2, 4, 3);
		
		BPlusTree<String, String> tree = new BPlusTree<String, String>(4, 4);

		forwardFill(tree, 49);
	}
	
	@Test
	public void deletingTheLastBPlusTreeElementShouldNullTheRootNode() {
		BPlusTree<String, String> tree = new BPlusTree<String, String>(5, 6);
		
		tree.put(KEY, VALUE);
		tree.remove(KEY);
		
		@SuppressWarnings("unchecked")
		Node<String, String> node =
			(Node<String, String>) ReflectionTestUtils.getField(tree, Constants.ROOT);
		
		assertThat(
				"Deleting the last element from B+ tree should null the root" +
				" node.",
				node, is(nullValue()));
		
		assertThatTreeIsValid(tree);
	}
	
	@Test
	public void deletingShouldMergeTheRootNodeToLeafNode() {
		fillEmptyAndTest("Deleting should merge the root node to leaf node.", 4, 5, 2,
				4, 4, 0, 0);
	}

	@Test
	public void deleteingShouldMergeEmptyLeafNode() {
		fillEmptyAndTest("Deleting should merge empty leaf node.", 4, 9, 3, 2, 3, 1, 1);
	}
	
	@Test
	public void deletingShouldMergeEmptyInnerNode() {
		fillEmptyAndTest("Deleting should merge empty inner node.", 4, 17, 7,
				2, 2, 2, 2);
	}

	@Test
	public void deletingShouldMergeTheRootNodeToInnerNode() {
		fillEmptyAndTest("Deleting should merge the root node to inner node.", 4, 49,
				48, 2, 2, 0, 0);
	}

	// repeat delete tests for large nodes
	@Test
	public void repeatDeleteTestsForLargeNodes() {
		fillEmptyAndTest("Deleting should merge the root node to leaf node.", 6, 7, 2,
				6, 6, 0, 0);
		fillEmptyAndTest("Deleting should merge empty leaf node.", 6, 13, 8, 6, 6, 0, 0);
		fillEmptyAndTest("Deleting should merge empty inner node.", 6, 37, 18,
				5, 2, 1, 2);
		fillEmptyAndTest("Deleting should merge the root node to inner node.", 6, 190,
				189, 2, 2, 0, 0);
	}
	
	@Test
	public void insertingAndDeletingSameKeysShouldGiveEmptyTree() {
		BPlusTree<String, String> tree = new BPlusTree<String, String>(5, 6);
		
		for (int i = 0; i < 200 ; i++) {
			tree.put(KEY, VALUE);
		}
		
		for (int i = 0; i < 200 ; i++) {
			tree.remove(KEY);
		}
		
		@SuppressWarnings("unchecked")
		Node<String, String> node =
			(Node<String, String>) ReflectionTestUtils.getField(tree, Constants.ROOT);
		
		assertThat(
				"Inserting and Deleting the same keys should give a valid " +
				"empty tree.",
				node, is(nullValue()));
		
		assertThatTreeIsValid(tree);
	}
}
