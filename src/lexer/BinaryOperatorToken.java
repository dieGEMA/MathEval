package lexer;

public class BinaryOperatorToken extends Token {
	
	int priority;
	int inOperator;
	
	public BinaryOperatorToken(String content) {
		super(content);
		this.priority = findPriority(content);
	}
	
	public BinaryOperatorToken(String content, int priority, int inOperator) {
		super(content);
		this.priority = findPriority(content);
		this.setPriority(priority);
		this.setInOperator(inOperator);
	}
	
	private int findPriority(String content) {
		switch(content) {
		case "^": return 3;
		case "*": return 2;
		case "/": return 2;
		case "+": return 1;
		case "-": return 1;
		default: return Integer.MAX_VALUE;
		}
	}
	
	public int getPriority() {
		return this.priority;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public int getInOperator() {
		return this.inOperator;
	}
	
	public void setInOperator(int inOperator) {
		this.inOperator = inOperator;
	}
	
	public void changeInOperator(int inOperatorDelta) {
		this.inOperator = this.inOperator + inOperatorDelta;
	}
	
	public void changePriority(int priorityDelta) {
		this.priority = this.priority + priorityDelta;
	}
}
