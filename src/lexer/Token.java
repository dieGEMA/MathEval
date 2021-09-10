package lexer;

public abstract class Token {
	String content;
	
	public Token() {}
	
	public Token(String content) {
		this.content = content;
	}
	
	public String getValue() {
		return this.content;
	}
	
	public String getType() {
		return this.getClass().getName();
	}
	
	public String toString() {
		return String.format("%-20s%s", this.getClass().getSimpleName().replaceFirst(".....$",""), this.content);
	}
}
