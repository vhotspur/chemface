package chemface;

public class SmilesLexer implements SmilesParser.Lexer {

public enum  Element {
	CARBON("C"),
	OXYGEN("O")
	;
	private String name;
	Element(String n) {
		name = n;
	}
	String getName() {
		return name;
	}
}

public class Context {
	public PositionedNode node;
	public PositionedNode connectingNode;
	public NodePlacer subgraph;
	public Element element;
}

private StringBuffer formulae;

private Context ctx;

public SmilesLexer() {
	formulae = new StringBuffer("");
	ctx = new Context();
}

public void setSource(String formulae) {
	this.formulae = new StringBuffer(formulae);
}

public int yylex() {
	if (formulae.length() == 0) {
		return 0;
	}
	
	switch (formulae.charAt(0)) {
		case 'C' : 
			return yylexElement(Element.CARBON, 1);
		case 'O' :
			return yylexElement(Element.OXYGEN, 1);
		default :
			return SmilesParser.TOK_ERROR;
	}
}

private int yylexElement(Element el, int charsEaten) {
	formulae.delete(0, charsEaten);
	ctx.element = el;
	return SmilesParser.TOK_ELEM_ORGANIC;
}


public Context getLVal() {
	return ctx;
}

public void yyerror (String s) {
}


}

