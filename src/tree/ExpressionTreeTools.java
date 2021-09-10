package tree;
import java.util.ListIterator;

import lexer.*;

import java.util.LinkedList;

/**
 * Baumstrukturen für die Darstellung mathematischer Ausdrücke.
 *
 * @author Vincent Samuel Kröger
 * @version 1.0
 */
public class ExpressionTreeTools<T> {
	
	public static ExpressionTreeNode<Token> buildExpressionTree(LinkedList<Token> tokenList) {
		return buildExpressionTreeInternal(tokenList, null);
	}
	
	private static ExpressionTreeNode<Token> buildExpressionTreeInternal(LinkedList<Token> tokenList, ExpressionTreeNode<Token> parent) {	
		int lowestPriorityIndex = findLowestPriorityIndex(tokenList);
		if(lowestPriorityIndex != -1) {
			ExpressionTreeNode<Token> root = new ExpressionTreeNode<Token>(tokenList.get(lowestPriorityIndex));
			root.setParent(parent);
			root.setLeftChild(buildExpressionTreeInternal(new LinkedList<Token>(tokenList.subList(0, lowestPriorityIndex)), root));
			root.setRightChild(buildExpressionTreeInternal(new LinkedList<Token>(tokenList.subList(lowestPriorityIndex + 1, tokenList.size())), root));
			return root;
		} else {
			ListIterator<Token> iter = tokenList.listIterator();
			while(iter.hasNext()) {
				int nextIndex = iter.nextIndex();
				Token next = iter.next();
				if(next instanceof OperatorToken) {
					ExpressionTreeNode<Token> root = new ExpressionTreeNode<Token>(next);
					root.setParent(parent);
					int offset = findClosingParenthesesOffset(new LinkedList<Token>(tokenList.subList(nextIndex + 1, tokenList.size())));
					LinkedList<Token> sublist = new LinkedList<Token>(tokenList.subList(nextIndex + 1,
							offset==-1 ? tokenList.size() : nextIndex+offset+2));
					ListIterator<Token> subIter = sublist.listIterator();
					while(subIter.hasNext()) {
						Token subNext = subIter.next();
						if(subNext instanceof BinaryOperatorToken) {
							((BinaryOperatorToken) subNext).changeInOperator(-1);
						}
					}
					root.setRightChild(buildExpressionTreeInternal(sublist, root));
					return root;
				}
			}
			ExpressionTreeNode<Token> root = new ExpressionTreeNode<Token>(tokenList.getFirst());
			root.setParent(parent);
			return root;
		}
		
	}
	
	private static int findLowestPriorityIndex(LinkedList<Token> tokenList) {
		ListIterator<Token> iter = tokenList.listIterator(tokenList.size());
		int highest = Integer.MAX_VALUE;
		int highestIndex = -1;
		while(iter.hasPrevious()) {
			Integer currentIndex = iter.previousIndex();
			Token current = iter.previous();
			if(current instanceof BinaryOperatorToken && ((BinaryOperatorToken) current).getInOperator() == 0) {
				int priority = ((BinaryOperatorToken) current).getPriority();
				if (priority < highest) {
					highest = priority;
					highestIndex = currentIndex;
				}
			}
		}
		return highestIndex;
	}
	
	private static int findClosingParenthesesOffset(LinkedList<Token> tokenList) {
		if(tokenList.isEmpty()) {
			return -1;
		} else {
			ListIterator<Token> iter = tokenList.listIterator();
			iter.next();
			int count = 1;
			while(iter.hasNext()) {
				String next = iter.next().getValue();
				if(next.equals("(")) {
					count++;
				} else if (next.equals(")")) {
					count--;
				}
				if(count == 0) {
					return iter.previousIndex();
				}
			}
		return -1;
		}
	}
	
	public static String postOrderTraverse(ExpressionTreeNode<Token> root) {
		if(root == null) {
			return "";
		}
		return postOrderTraverse(root.getLeftChild()) + postOrderTraverse(root.getRightChild()) + root.getContent().getValue();
	}
	
	public static String inOrderTraverse(ExpressionTreeNode<Token> root) {
		if(root == null) {
			return "";
		}
		return inOrderTraverse(root.getLeftChild()) + root.getContent().getValue()  + inOrderTraverse(root.getRightChild());
	}
	
}
