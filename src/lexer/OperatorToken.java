package lexer;

public class OperatorToken extends SignedToken {
	
	public OperatorToken(String content) {
		super(content);
	}
	
	public OperatorToken(boolean negative, String content) {
		super(content);
		this.negative = negative;
	}
	
	public String toString() {
		return String.format("%-20s%s", this.getClass().getSimpleName().replaceFirst(".....$",""), (this.negative?"-":"") + this.content);
	}
}
