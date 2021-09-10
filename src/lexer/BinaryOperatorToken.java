package lexer;

public class BinaryOperatorToken extends Token {
	
	int priority;
	int inOperator;
	
	public BinaryOperatorToken(String content) {
		super(content);
		switch(content) {
		case "^": this.priority = 3; break;
		case "*": this.priority = 2; break;
		case "/": this.priority = 2; break;
		case "+": this.priority = 1; break;
		case "-": this.priority = 1; break;
		default: this.priority = Integer.MAX_VALUE;
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
