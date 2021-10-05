package gui;

import java.util.*;

import lexer.MathLexer;
import lexer.Token;
import tree.ExpressionTreeTools;
import tree.ExpressionTreeNode;


/**
 * A lexer and parser for mathematical expressions.
 *
 * @author Vincent Samuel Kroeger
 * @version 1.3
 */
public class MainGUI {
	
	/**
	 * main-method used for testing purposes.
	 */
	public static void main(String[] args) {
		LinkedList<Character> inputList = new LinkedList<Character>();
		MathLexer lexer = new MathLexer();
		
		try(Scanner sc = new Scanner(System.in)){
			sc.useDelimiter("");
			while(sc.hasNext()) {
				char nextChar = sc.next().charAt(0);
				if(nextChar != '\n') {
					inputList.add(nextChar);
				} else break;
			}
		}
		
		lexer.tokenize(inputList);
		ExpressionTreeNode<Token> test = ExpressionTreeTools.buildExpressionTree(lexer.getTokenList());
		System.out.print(test.toString());
		System.out.println(ExpressionTreeTools.evaluate(test));
	}
}
