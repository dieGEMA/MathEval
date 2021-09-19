package lexer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;


/**
 * Lexer 
 *
 * @author Vincent Samuel Kröger
 * @version 1.3
 */

public class MathLexer {
	
	LinkedList<Token> tokenList;
	
	public MathLexer() {
		tokenList = new LinkedList<Token>();	
	}
	
	public void tokenize(LinkedList<Character> inputList) throws IllegalStateException {
		LinkedList<Character> usedList = new LinkedList<Character>();
		
		while(!inputList.isEmpty()) {
			if(Character.isWhitespace(inputList.peek())) {
				inputList.remove();
			} else if(Character.isDigit(inputList.peek())) {
				usedList.addAll(pollNumbers(inputList));
			} else if(Character.isLetter(inputList.peek())) {
				String word = "";
				usedList.add(inputList.peek());
				word += inputList.poll();
				while(Character.isLetter(inputList.peek())) {
					usedList.add(inputList.peek());
					word += inputList.poll();
				}
				while(searchOperator(word)[0] != -1) {
					int[] result = searchOperator(word);
					for(int i = 0; i < result[0]; i++) {
						tokenList.add(new SymbolToken(word.substring(i, i + 1)));
					}
					tokenList.add(new OperatorToken(word.substring(result[0], result[0] + result[1])));
					word = word.substring(result[0] + result[1]);
				}
				for(int i = 0; i < word.length(); i++) {
					tokenList.add(new SymbolToken(word.substring(i, i + 1)));
				}
			} else if("-+*/^%".indexOf(inputList.peek()) != -1) {
				usedList.add(inputList.peek());
				tokenList.add(new BinaryOperatorToken(String.valueOf(inputList.poll())));
			} else if (inputList.peek().equals('!')) {
				usedList.add(inputList.peek());
				tokenList.add(new OperatorToken(String.valueOf(inputList.poll())));
			} else if("()".indexOf(inputList.peek()) != -1) {
				usedList.add(inputList.peek());
				tokenList.add(new BracketToken(String.valueOf(inputList.poll())));
			} else {
				inputList.remove();
			}
		}
		if(!checkBracketsValid()) {
			throw new IllegalStateException("Ungültige Klammerung");
		} else {
			adjustBracketPriority();
			repairMultiplication();
			inOperatorDepth();
			removeMultiplePlusMinus();
			fixFirstSign();
			invertInParentheses();
			removeBrackets();
		}
	}
	
	/**
	 * Iterates through the token list saved in this MathLexer-instance,
	 * changing the signs of numbers, symbols and operators when they are
	 * contained in parentheses preceded by a minus.
	 */
	private void invertInParentheses() {}
	
	/**
	 * Iterates through a list of characters, in which the first character should be a digit,
	 * and searches for the "rest" of the number, adding the whole number to the token list held by this MathLexer instance.
	 * 
	 * @return The list given to this method with the characters which are part of the found number removed.
	 */
	private LinkedList<Character> pollNumbers(LinkedList<Character> inputList) throws IllegalStateException {
		LinkedList<Character> usedList = new LinkedList<Character>();
		String[] number = {"", ""};
		boolean decimals = false;
		usedList.add(inputList.peek());
		number[0] += inputList.poll();
		while(!inputList.isEmpty() && (Character.isDigit(inputList.peek()) || inputList.peek().equals('.'))) {
			if(inputList.peek().equals('.') && !decimals) {
				decimals = true;
			} else if (inputList.peek().equals('.') && decimals) {
				throw new IllegalStateException("Can't have more than one decimal point in a number.");
			}
			if(decimals) {
				usedList.add(inputList.peek());
				number[1] += inputList.poll();
			} else {
				usedList.add(inputList.peek());
				number[0] += inputList.poll();
			}
			
		}
		tokenList.add(new NumberToken(false, number[0], number[1]));
		return usedList;
	}
	
	/**
	 * Iterates through the token list saved in this MathLexer-instance,
	 * checking whether the parentheses are placed in a valid way.
	 */
	private boolean checkBracketsValid() {
		if(this.tokenList.isEmpty()) {
			return true;
		} else {
			Iterator<Token> iterator = tokenList.iterator();
			int count = 0;
			while(iterator.hasNext()) {
				String next = iterator.next().getValue();
				if(next.equals("(")) {
					count++;
				} else if (next.equals(")")) {
					count--;
				}
			}
			if(count == 0) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * Iterates through the token list saved in this MathLexer-instance,
	 * incrementing the priority of operators by 10 per "level of parentheses".
	 */
	public void adjustBracketPriority() {
		int bonus = 0;
		Iterator<Token> iterator = this.tokenList.iterator();
		while(iterator.hasNext()) {
			Token next = iterator.next();
			if(next.getValue().equals("(")) {
				bonus += 10;
			} else if (next.getValue().equals(")")){
				bonus -= 10;
			} else if (next instanceof BinaryOperatorToken) {
				((BinaryOperatorToken) next).changePriority(bonus);
			}
		}
	}
	
	/**
	 * Iterates through the token list saved in this MathLexer-instance,
	 * and sets the attribute of binary operators whether they are in parentheses after an operator.
	 */
	private void inOperatorDepth() {
		ListIterator<Token> iterator = this.tokenList.listIterator();
		int setTo = 0;
		int count = 0;
		boolean foundOp = false;
		while(iterator.hasNext()) {
			Token next = iterator.next();
			if(next instanceof OperatorToken) {
				foundOp = true;
				setTo++;
			} else if(next.getValue().equals("(") && foundOp) {
				count++;
			} else if(next.getValue().equals(")") && foundOp) {
				count--;
				if(count == 0) {
					setTo = 0;
				}
			}  else if(next instanceof BinaryOperatorToken) {
				((BinaryOperatorToken) next).changeInOperator(setTo);
			}
		}
	}
	
	/**
	 * Iterates through the token list saved in this MathLexer-instance,
	 * placing for example multiplication-operators between consecutive symbol tokens.
	 */
	private void repairMultiplication() {
		ListIterator<Token> iterator = this.tokenList.listIterator();
		Token next;
		if(iterator.hasNext()) {
			next = iterator.next();
		} else {
			return;
		}
		while(iterator.hasNext()) {
			Token last = next;
			if(iterator.hasNext()) {
				next = iterator.next();
			} else {
				return;
			}
			
			if((last instanceof SymbolToken && next instanceof SymbolToken)
					|| (last instanceof SymbolToken && next instanceof OperatorToken)
					|| (last instanceof NumberToken && next instanceof SymbolToken)
					|| (last instanceof NumberToken && next instanceof OperatorToken)
					|| (last instanceof SymbolToken && next instanceof BracketToken && ((BracketToken) next).getValue() == "(")
					|| (last instanceof NumberToken && next instanceof BracketToken && ((BracketToken) next).getValue() == "(")
					|| (last instanceof BracketToken
							&& ((BracketToken) last).getValue() == ")"
							&& next instanceof BracketToken
							&& ((BracketToken) next).getValue() == "(")) {
				iterator.previous();
				iterator.add(new BinaryOperatorToken("*"));
				iterator.previous();
				((BinaryOperatorToken) iterator.next()).changePriority(20);
				iterator.next();
			}
		}
	}
	
	/**
	 * Checks if the first Token is a BinaryOperatorToken, making it the first Number or Symobl's sign if it is a plus or minus.
	 * Throws an Error if the first Operator is anything but a plus or minus.
	 */
	private void fixFirstSign() throws IllegalStateException {
		if(this.tokenList.size()>1) {
			Token first = this.tokenList.get(0);
			Token second = this.tokenList.get(1);
			if(first instanceof BinaryOperatorToken
					&& (second instanceof NumberToken
					|| second instanceof SymbolToken
					|| second instanceof OperatorToken)) {
				if(first.getValue().equals("-")) {
					this.tokenList.remove(0);
					((SignedToken) second).invertSign();
				} else if(first.getValue().equals("+")) {
					this.tokenList.remove(0);
				} else {
					throw new IllegalStateException("Erstes Zeichen vor Symbol oder Zahl kann kein anderer Operator als + oder - sein.");
				}
			} else if(first instanceof BinaryOperatorToken && second instanceof BracketToken) {
				if(first.getValue().equals("-")) {
					this.tokenList.remove(0);
					ListIterator<Token> iter = this.tokenList.listIterator();
					while(iter.hasNext()) {
						Token next = iter.next();
						if(next instanceof OperatorToken || next instanceof NumberToken || next instanceof SymbolToken) {
							((SignedToken) next).invertSign();
						}
					}
				} else if(first.getValue().equals("+")) {
					this.tokenList.remove(0);
				} else {
					throw new IllegalStateException("Erstes Zeichen vor Symbol oder Zahl kann kein anderer Operator als + oder - sein.");
				}
			}
		} else {
			return;
		}
	}
	
	/**
	 * Iterates through the token list saved in this MathLexer-instance,
	 * fixing occurrences of multiple BinaryOperators following each other.
	 */
	private void removeMultiplePlusMinus() throws IllegalStateException {
		int j;
		for (int i = 0; i < this.tokenList.size() - 1; i++) {
			j = i + 1;
			Token tokenAtI = this.tokenList.get(i);
			Token tokenAtJ = this.tokenList.get(j);
            if(tokenAtI instanceof BinaryOperatorToken && tokenAtJ instanceof BinaryOperatorToken) {
            	int iPrio = ((BinaryOperatorToken) tokenAtI).getPriority();
            	int iInOp = ((BinaryOperatorToken) tokenAtI).getInOperator();
            	if(tokenAtI.getValue().equals("-") && tokenAtJ.getValue().equals("-")) {
            		this.tokenList.remove(i);
                	this.tokenList.remove(i);
                	this.tokenList.add(i, new BinaryOperatorToken("+", iPrio, iInOp));
                	i--;
            	} else if((tokenAtI.getValue().equals("+") && tokenAtJ.getValue().equals("-"))
            			|| (tokenAtI.getValue().equals("-") && tokenAtJ.getValue().equals("+"))) {
            		this.tokenList.remove(i);
                	this.tokenList.remove(i);
                	this.tokenList.add(i, new BinaryOperatorToken("-", iPrio, iInOp));
                	i--;
            	} else if(tokenAtI.getValue().equals("+") && tokenAtJ.getValue().equals("+")) {
            		this.tokenList.remove(j);
                	i--;
            	} else if(tokenAtI.getValue().equals("*") && tokenAtJ.getValue().equals("+")) {
            		this.tokenList.remove(j);
                	i--;
            	}else if(tokenAtI.getValue().equals("*") && tokenAtJ.getValue().equals("-")) {
            		Token nextToken = this.tokenList.get(j + 1);
            		if(nextToken instanceof NumberToken
            				|| nextToken instanceof OperatorToken 
            				|| nextToken instanceof SymbolToken) {
            			((NumberToken) nextToken).setNegative(true);
            			this.tokenList.remove(j);
                    	i--;
            		} else if(nextToken.getValue().equals("-")){
            			this.tokenList.remove(j);
                    	this.tokenList.remove(j);
                    	this.tokenList.add(j, new BinaryOperatorToken("+", iPrio, iInOp));
                    	i--;
            		} else if(nextToken.getValue().equals("+")){
            			this.tokenList.remove(j + 1);
                    	i--;
            		}
            		
            	} else {
            		throw new IllegalStateException("Can't have multiple binary operators after each other, except + and -.");
            	}
            	
            }
        }
	}
	

	/**
	 * Iterates through the token list saved in this MathLexer-instance,
	 * removing all brackets.
	 */
	private void removeBrackets() {
		ListIterator<Token> iterator = this.tokenList.listIterator();
		while(iterator.hasNext()) {
			Token next = iterator.next();
			if(next instanceof BracketToken) {
				iterator.remove();
			}
		}
	}
	
	/**
	 * Prints the list of tokens.
	 */
	public void printTokenList() {
		Iterator<Token> iterator = this.tokenList.iterator();
		while(iterator.hasNext()) {
			System.out.println(iterator.next().toString());
		}
	}
	
	/**
	 * Returns the token-list this lexer holds.
	 */
	public LinkedList<Token> getTokenList(){
		return this.tokenList;
	}
	
	/**
	 * Searches for operator names in a given string.
	 * 
	 * @param value	String to be searched for operator names.
	 * 
	 * @return An array containing the index of the found operator name and its length
	 * 			or {-1, 0} if no operator is found.
	 */
	private int[] searchOperator(String value) {
		String[] operators = {
				"arcsin",		//arcsine
				"arccos",		//arccosine
				"arctan", 		//arctangent
				"arccot",		//arccotangent
				"arcsec",		//arcsecant
				"arccsc",		//arccosecant
				"sinh",			//hyperbolic sine
				"cosh",			//hyperbolic cosine
				"tanh",			//hyperbolic tangent
				"csch",			//hyperbolic cosecant
				"sech",			//hyperbolic secant
				"coth",			//hyperbolic cotangent
				"arsinh",		//area hyperbolic sine
				"arcosh",		//area hyperbolic cosine
				"sin",			//sine
				"cos",			//cosine
				"tan",			//tangent
				"cot",			//cotangent
				"csc",			//cosecant
				"sec",			//secant
				"sqrt",			//square root
				"ln"};
		int lowest = value.length();
		String lastFound = "";
		for(String operator : operators) {
			int index = value.indexOf(operator);
			if (index < lowest && index != -1) {
				lowest = index;
				lastFound = operator;
			}
		}
		if(lastFound != "") {
			return(new int[] {lowest, lastFound.length()});
		} else {
			return(new int[] {-1, 0});
		}
	}
}