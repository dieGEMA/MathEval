package tree;
import java.util.ListIterator;

import lexer.*;

import java.util.LinkedList;

/**
 * Tree structures used to represent and evaluate mathematical expressions.
 *
 * @author Vincent Samuel Kröger
 * @version 1.3
 */
public class ExpressionTreeTools<T> {
	
	/**
	 * Creates an ExpressionTree from a given list of tokens.
	 * 
	 * @param tokenList A LinkedList holding instances of classes inheriting from the class Token.
	 * 
	 * @return An ExpressionTree build from the list of tokens.
	 */
	public static ExpressionTreeNode<Token> buildExpressionTree(LinkedList<Token> tokenList) {
		return buildExpressionTreeInternal(tokenList, null);
	}
	
	/**
	 * Creates an ExpressionTree from a given list of tokens.
	 * 
	 * @param tokenList	A LinkedList holding instances of classes inheriting from the class Token.
	 * @param parent 	The ExpressionTreeNode to be set as the new tree's parent node.
	 * 
	 * @return An ExpressionTree build from the list of tokens.
	 */
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
	
	/**
	 * Find's the BinaryOperatorToken with the lowest priority.
	 * 
	 * @param tokenList	A LinkedList holding instances of classes inheriting from the class Token.
	 * 
	 * @return The index of the BinaryOperatorToken with the lowest priority.
	 * 			Returns the last index if multiple Tokens have the same priority.
	 */
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
	
	/**
	 * Searches for the offset from the last OperatorToken to the closing parentheses.
	 * First Token in the given list of Tokens should be the Token after the OperatorToken
	 
	 * 
	 * @param tokenList	A LinkedList holding instances of classes inheriting from the class Token.
	 * 
	 * @return The offset.
	 * 		    Returns -1 if no closing parentheses exists for this OperatorToken.
	 */
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
	
	/**
	 * Post-order traversal of the given tree.
	 * 
	 * @param root	The root of the ExpressionTree to be traversed.
	 * 
	 * @return A string representation of the post-order traversal.
	 */
	public static String postOrderTraverse(ExpressionTreeNode<Token> root) {
		if(root == null) {
			return "";
		}
		return postOrderTraverse(root.getLeftChild()) + postOrderTraverse(root.getRightChild()) + root.getContent().getValue();
	}
	
	/**
	 * In-order traversal of the given tree.
	 * 
	 * @param root	The root of the ExpressionTree to be traversed.
	 * 
	 * @return A string representation of the in-order traversal.
	 */
	public static String inOrderTraverse(ExpressionTreeNode<Token> root) {
		if(root == null) {
			return "";
		}
		return inOrderTraverse(root.getLeftChild()) + root.getContent().getValue()  + inOrderTraverse(root.getRightChild());
	}
	
	/**
	 * Recursive evaluation of ExpressionTrees
	 * @param root The root of the ExpressionTree to be evaluated
	 * @return The expressions value
	 */
	public static Double evaluate(ExpressionTreeNode<Token> root) {
		if(root == null) {
			return 0d;
		} else if(root.getContent() instanceof NumberToken) {
			return ((NumberToken) root.getContent()).toDouble();
		} else if(root.getContent() instanceof BinaryOperatorToken) {
			switch(root.getContent().getValue()) {
			case("-"): return evaluate(root.getLeftChild()) - evaluate(root.getRightChild());
			case("+"): return evaluate(root.getLeftChild()) + evaluate(root.getRightChild());
			case("*"): return evaluate(root.getLeftChild()) * evaluate(root.getRightChild());
			case("/"): return evaluate(root.getLeftChild()) / evaluate(root.getRightChild());
			case("%"): return evaluate(root.getLeftChild()) % evaluate(root.getRightChild()); 
			case("^"): return Math.pow(evaluate(root.getLeftChild()), evaluate(root.getRightChild())); 
			default: return 0d;
			}
		} else if(root.getContent() instanceof OperatorToken) {
			Double returnValue;
			switch(root.getContent().getValue()) {
			//TO-DO: Add missing operators
			case("sin"): returnValue = Math.sin(evaluate(root.getRightChild())); break;
			case("cos"): returnValue = Math.cos(evaluate(root.getRightChild())); break;
			case("tan"): returnValue =  Math.tan(evaluate(root.getRightChild())); break;
			case("sinh"): returnValue =  Math.sinh(evaluate(root.getRightChild())); break;
			case("cosh"): returnValue =  Math.cosh(evaluate(root.getRightChild())); break;
			case("tanh"): returnValue =  Math.tanh(evaluate(root.getRightChild())); break;
			case("arcsin"): returnValue =  Math.asin(evaluate(root.getRightChild())); break;
			case("arccos"): returnValue =  Math.acos(evaluate(root.getRightChild())); break;
			case("arctan"): returnValue =  Math.atan(evaluate(root.getRightChild())); break;
			case("sqrt"): returnValue =  Math.sqrt(evaluate(root.getRightChild())); break;
			default: returnValue =  0d;
			}
			if(((OperatorToken) root.getContent()).getNegative()) {
				return -returnValue;
			} else {
				return returnValue;
			}
		} else {
			return 0d;
		}
	}
	
}