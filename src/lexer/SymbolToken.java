package lexer;

public class SymbolToken extends SignedToken {
	
	boolean negative;
	
	public SymbolToken(String content) {
		super(content);
		this.negative = false;
	}
	
	public SymbolToken(boolean negative, String content) {
		super(content);
		this.negative = negative;
	}
	
	public String toString() {
		return String.format("%-20s%s", this.getClass().getSimpleName().replaceFirst(".....$",""), (this.negative?"-":"") + this.content);
	}
}
