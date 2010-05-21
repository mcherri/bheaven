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


/**
 *
 */
public class BPlusTreeCheckerTest {
	
	private LeafNode<String, String> root1;
	private InnerNode<String, String> root2;
	private InnerNode<String, String> root3;
	private BPlusTreeChecker<String, String> checker;
	
	@Before
	public void setUp() {
		BPlusTree<String, String> tree = new BPlusTree<String, String>(4, 5);
		checker = new BPlusTreeChecker<String, String>(tree);
		
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
	
	@Test
	public void balancedValidationBehaviour() {
		
		ReflectionTestUtils.setField(checker, Constants.ROOT, root1);
		
		assertThat("", checker.checkTreeIsBalanced(), is(true));
		
		ReflectionTestUtils.setField(checker, Constants.ROOT, root2);
		
		assertThat("", checker.checkTreeIsBalanced(), is(true));
		
		ReflectionTestUtils.setField(checker, Constants.ROOT, root3);
		
		assertThat("", checker.checkTreeIsBalanced(), is(true));
		
		@SuppressWarnings("unchecked")
		Node<String, String> children41[] = new Node[] {
			new LeafNode<String, String>(null, null, 0, null),
			new LeafNode<String, String>(null, null, 0, null)
		};
		Node<String, String> children31[] = ((InnerNode<String, String>) root3
				.getChildren()[0]).getChildren();
		String keys[] = { "k1" };
		children31[1] = new InnerNode<String, String>(keys, children41, 2);
		
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
		
		InnerNode<String, String> node =
			(InnerNode<String, String>) root3.getChildren()[0];
		node.getChildren()[2] = null;
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
		
		root3.getChildren()[1] = null;
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
		
		root3.getKeys()[0] = "a20";
		
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
		
		String keys[] = root3.getKeys();
		String temp = keys[0];
		keys[0] = keys[1];
		keys[1] = temp;
		
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
		
		Node<String, String> children[] = root3.getChildren();
		Node<String, String> temp = children[0];
		children[0] = children[1];
		children[1] = temp;
		
		assertThat("", checker.checkLeafNodesLinksOrder(), is(false));
	}

}
