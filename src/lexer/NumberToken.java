package lexer;

public class NumberToken extends SignedToken {
	
	String decimals;
	
	public NumberToken(boolean negative, String content, String decimals) {
		super(content);
		this.negative = negative;
		this.decimals = decimals;
	}
	
	public String toString() {
		return String.format("%-20s%s", this.getClass().getSimpleName().replaceFirst(".....$",""), (this.negative?"-":"") + this.content + this.decimals);
	}
	
	public Double toDouble() {
		return Double.parseDouble((this.negative? "-":"") + this.content + this.decimals);
	}
}
