package chemface;

public class SmilesLexer implements SmilesParser.Lexer {
	public int yylex() {
		return SmilesParser.TOK_BOND_SINGLE;
	}
	
	public Object getLVal() {
		return null;
	}
	
	public void yyerror (String s) {
	}
}

