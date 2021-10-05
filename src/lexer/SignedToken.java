package lexer;

public abstract class SignedToken extends Token {

	boolean negative;
	
	public SignedToken(String content) {
		super(content);
		this.negative = false;
	}
	
	public void invertSign() {
		this.negative = !this.negative;
	}
	
	public void setNegative(boolean negative) {
		this.negative = negative;
	}
	
	public boolean getNegative() {
		return this.negative;
	}
	
}
