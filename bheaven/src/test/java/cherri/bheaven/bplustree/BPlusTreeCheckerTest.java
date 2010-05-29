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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import cherri.bheaven.bplustree.memory.MemoryInnerNode;
import cherri.bheaven.bplustree.memory.MemoryLeafNode;
import cherri.bheaven.bplustree.memory.MemoryNodeFactory;


/**
 *
 */
public class BPlusTreeCheckerTest {
	
	private MemoryLeafNode<String, String> root1;
	private MemoryInnerNode<String, String> root2;
	private MemoryInnerNode<String, String> root3;
	private BPlusTreeChecker<String, String> checker;
	
	@Before
	public void setUp() {
		NodeFactory<String, String> factory =
			new MemoryNodeFactory<String, String>(4, 5);
		BPlusTree<String, String> tree = new BPlusTree<String, String>(factory);
		checker = new BPlusTreeChecker<String, String>(tree);
		
		root1 = new MemoryLeafNode<String, String>(
				10, null);
		Utils.generateStrings(root1, 5, "a");
		Utils.generateValueStrings(root1, 5, "va");
		
		root2 = new MemoryInnerNode<String, String>(3);
		Utils.setLeafNodes(root2, 4, 2, "a", "va");
		Utils.setChildrenKeys(root2, 2);
		
		root3 = new MemoryInnerNode<String, String>(4); 
		Utils.setInnerNodes(root3, 5, 3);
		Utils.setChildrenKeys(root3, 3);
		
	}
	
	@Test
	public void balancedValidationBehaviour() {
		
		ReflectionTestUtils.setField(checker, Constants.ROOT, root1);
		
		assertThat("", checker.checkTreeIsBalanced(), is(true));
		
		ReflectionTestUtils.setField(checker, Constants.ROOT, root2);
		
		assertThat("", checker.checkTreeIsBalanced(), is(true));
		
		ReflectionTestUtils.setField(checker, Constants.ROOT, root3);
		
		assertThat("", checker.checkTreeIsBalanced(), is(true));
		
		MemoryInnerNode<String, String> child = new MemoryInnerNode<String, String>(1);
		child.setKey("k1", 0);
		for (int i = 0; i < 2; i ++) {
			child.setChild(new MemoryLeafNode<String, String>(0, null), i);
		}
		((InnerNode<String, String>) ((InnerNode<String, String>) root3.getChild(0))).setChild(child, 1);

		assertThat("", checker.checkTreeIsBalanced(), is(false));
	}
	
	@Test
	public void internalNodesEntriesCountBehaviour() {
		ReflectionTestUtils.setField(checker, Constants.ROOT, root1);
		
		assertThat("", checker.checkInternalNodesChildrenCount(), is(true));
		
		ReflectionTestUtils.setField(checker, Constants.ROOT, root2);
		
		assertThat("", checker.checkInternalNodesChildrenCount(), is(true));
		
		ReflectionTestUtils.setField(checker, Constants.ROOT, root3);
		
		assertThat("", checker.checkInternalNodesChildrenCount(), is(true));
		
		MemoryInnerNode<String, String> node =
			(MemoryInnerNode<String, String>) root3.getChild(0);
		node.setChild(null, 2);
		node.setSlots(1);
		
		assertThat("", checker.checkInternalNodesChildrenCount(), is(false));
	}
	
	@Test
	public void rootNodeBehaviour() {
		ReflectionTestUtils.setField(checker, Constants.ROOT, root1);
		
		assertThat("", checker.checkRootNode(), is(true));
		
		ReflectionTestUtils.setField(checker, Constants.ROOT, root2);
		
		assertThat("", checker.checkRootNode(), is(true));
		
		ReflectionTestUtils.setField(checker, Constants.ROOT, root3);
		
		assertThat("", checker.checkRootNode(), is(true));
		
		root3.setChild(null, 1);
		root3.setSlots(0);
		
		assertThat("", checker.checkRootNode(), is(false));
	}
	
	@Test
	public void internalNodesKeysCountWithRespectToChildrenBehaviour() {
		ReflectionTestUtils.setField(checker, Constants.ROOT, root1);
		
		assertThat("",
				checker.checkInternalNodesKeysCountWithRespectToChildren(),
				is(true));

		ReflectionTestUtils.setField(checker, Constants.ROOT, root2);

		assertThat("",
				checker.checkInternalNodesKeysCountWithRespectToChildren(),
				is(true));

		ReflectionTestUtils.setField(checker, Constants.ROOT, root3);

		assertThat("",
				checker.checkInternalNodesKeysCountWithRespectToChildren(),
				is(true));
		
		root3.setKey("a20", 0);
		
		assertThat("",
				checker.checkInternalNodesKeysCountWithRespectToChildren(),
				is(false));
	}
	
	@Test
	public void internalNodesKeysOrderBehaviour() {
		ReflectionTestUtils.setField(checker, Constants.ROOT, root1);
		
		assertThat("", checker.checkInternalNodesKeysOrder(), is(true));
		
		ReflectionTestUtils.setField(checker, Constants.ROOT, root2);
		
		assertThat("", checker.checkInternalNodesKeysOrder(), is(true));
		
		ReflectionTestUtils.setField(checker, Constants.ROOT, root3);
		
		assertThat("", checker.checkInternalNodesKeysOrder(), is(true));
		
		String temp = root3.getKey(0);
		root3.setKey(root3.getKey(1), 0);
		root3.setKey(temp, 1);
		
		assertThat("", checker.checkInternalNodesKeysOrder(), is(false));
	}
	
	@Test
	public void leafNodesLinksOrderBehaviour() {
		ReflectionTestUtils.setField(checker, Constants.ROOT, root1);
		
		assertThat("", checker.checkLeafNodesLinksOrder(), is(true));
		
		ReflectionTestUtils.setField(checker, Constants.ROOT, root2);
		
		assertThat("", checker.checkLeafNodesLinksOrder(), is(true));
		
		ReflectionTestUtils.setField(checker, Constants.ROOT, root3);
		
		assertThat("", checker.checkLeafNodesLinksOrder(), is(true));
		
		Node<String, String> temp = root3.getChild(0);
		root3.setChild(root3.getChild(1), 0);
		root3.setChild(temp, 1);
		
		assertThat("", checker.checkLeafNodesLinksOrder(), is(false));
	}

}
