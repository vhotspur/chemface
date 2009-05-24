package chemface;

public class SmilesLexer implements SmilesParser.Lexer {

public enum  Element {
	CARBON("C"),
	OXYGEN("O"),
	NITROGEN("N"),
	IODINE("I"),
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
	public PositionedNode node = null;
	public NodePlacer graph = null;
	public PositionedNode firstNode = null;
	public PositionedNode lastNode = null;
	public Bond bond = null;
	public Bond hookEdge = null;
	public Element element = null;
	public Context makeIndependent() {
		Context cl = new Context();
		if (node != null) {
			cl.node = (PositionedNode)node.clone();
		}
		if (graph != null) {
			cl.graph = (NodePlacer)graph.clone();
		}
		if (firstNode != null) {
			cl.firstNode = (PositionedNode)firstNode.clone();
		}
		if (lastNode != null) {
			cl.lastNode = (PositionedNode)lastNode.clone();
		}
		if (bond != null) {
			cl.bond = (Bond)bond.clone();
		}
		cl.element = element;
		
		return cl;
	}
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
		case 'N' :
			return yylexElement(Element.NITROGEN, 1);
		case 'I' :
			return yylexElement(Element.IODINE, 1);
		case '(' :
			formulae.delete(0, 1);
			return SmilesParser.TOK_BRANCH_START;
		case ')' :
			formulae.delete(0, 1);
			return SmilesParser.TOK_BRANCH_END;
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
	return ctx.makeIndependent();
}

public void yyerror (String s) {
}


}

