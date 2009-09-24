package chemface;

/**
 * Lexical analyzator of SMILES.
 * 
 */
public class SmilesLexer implements SmilesParser.Lexer {

/**
 * Chemical element enum.
 * 
 */
public enum  Element {
	CARBON("C"),
	OXYGEN("O"),
	NITROGEN("N"),
	IODINE("I"),
	HYDROGEN("H"),
	SULFUR("S"),
	;
	private String name;
	Element(String n) {
		name = n;
	}
	String getName() {
		return name;
	}
}

/**
 * Lexing and parsing context.
 * 
 */
public class Context {
	/// Node storage
	public PositionedNode node = null;
	/// Reference number for cycles
	public int refNo = 0;
	/// (Sub)graph storage
	public Reactant graph = null;
	/// First node of the graph (used when adding nodes)
	public PositionedNode firstNode = null;
	/// Last node of the graph (used when adding nodes)
	public PositionedNode lastNode = null;
	/// Last used bond
	public Bond bond = null;
	/// Bond to hook through to other graph
	public Bond hookEdge = null;
	/// Last added element
	public Element element = null;
	/// Currently processed branches
	public java.util.Vector<Context> branch = null;
	/**
	 * Makes the context independent (kind of cloning).
	 * 
	 */
	public Context makeIndependent() {
		Context cl = new Context();
		if (node != null) {
			cl.node = (PositionedNode)node.clone();
		}
		if (graph != null) {
			cl.graph = (Reactant)graph.clone();
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
		if (branch != null) {
			cl.branch = new java.util.Vector<Context>();
			for (Context c : branch) {
				cl.branch.add(c.makeIndependent());
			}
		}
		cl.element = element;
		cl.refNo = refNo;
		
		return cl;
	}
}

/// Formulae to be parsed
private StringBuffer formulae;

/// Lexing context
private Context ctx;

/**
 * Constructor.
 * 
 */
public SmilesLexer() {
	formulae = new StringBuffer("");
	ctx = new Context();
}

/**
 * Sets the source formulae.
 * 
 * @param formulae Formulae in SMILES notation
 * 
 */
public void setSource(String formulae) {
	this.formulae = new StringBuffer(formulae);
}

/**
 * Tells next lexing element.
 * 
 * @retval 0 End of input
 * 
 */
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
		case 'H' :
			return yylexElement(Element.HYDROGEN, 1);
		case 'S' :
			return yylexElement(Element.SULFUR, 1);
		case '(' :
			eat(1);
			return SmilesParser.TOK_BRANCH_START;
		case ')' :
			eat(1);
			return SmilesParser.TOK_BRANCH_END;
		case '-' :
			eat(1);
			return SmilesParser.TOK_BOND_SINGLE;
		case '=' :
			eat(1);
			return SmilesParser.TOK_BOND_DOUBLE;
		case '#' :
			eat(1);
			return SmilesParser.TOK_BOND_TRIPLE;
		case '\'' : 
			return yylexLiteral();
		case '1' :
		case '2' :
		case '3' :
		case '4' :
		case '5' :
		case '6' :
		case '7' :
		case '8' :
		case '9' :
			return yylexRefNumber(formulae.charAt(0));
		default :
			return SmilesParser.TOK_ERROR;
	}
}

/**
 * Helper function for yylex when returning element.
 * 
 * @param el Element to be returned
 * @param charsEaten How many characters were eaten from the formulae
 * 
 */
private int yylexElement(Element el, int charsEaten) {
	eat(charsEaten);
	ctx.element = el;
	return SmilesParser.TOK_ELEM_ORGANIC;
}

private int yylexRefNumber(char num) {
	eat(1);
	ctx.refNo = Integer.parseInt(Character.toString(num));
	return SmilesParser.TOK_ELEM_REFNUMBER;
}

private int yylexLiteral() {
	eat(1);
	try {
		int i = 0;
		while (formulae.charAt(i) != '\'') {
			i++;
		}
		String s = formulae.substring(0, i);
		if (s.length() == 0) {
			// get the next one
			return yylex();
		}
		ctx.node = new PositionedNode(new Node(s, RenderingOptions.getFont()));
		eat(i+1);
		return SmilesParser.TOK_ELEM_CUSTOM;
	} catch (IndexOutOfBoundsException e) {
		yyerror("syntax error, missing ending apostrophe");
		return SmilesParser.TOK_ERROR;
	}
}

/**
 * Eats characters from the input formulae (truncates it).
 * 
 * @param charsCount How many characters (from the beginning) to dispose
 * 
 */
private void eat(int charsCount) {
	formulae.delete(0, charsCount);
}

/**
 * Tells auxilary values for the parser.
 * 
 */
public Context getLVal() {
	return ctx.makeIndependent();
}

/**
 * Callback on parsing errors.
 * 
 * @param s Error description
 * 
 */
public void yyerror (String s) {
	System.err.println(s);
}


}

