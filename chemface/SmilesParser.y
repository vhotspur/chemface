%{
%}



%language "Java"
%token TOK_BOND_SINGLE
%token<SmilesLexer.Context> TOK_ELEM_ORGANIC
%token<SmilesLexer.Context> TOK_ELEM_OTHER
%token TOK_ELEM_BORON
%token TOK_ELEM_CARBON
%token TOK_ELEM_NITROGEN
%token TOK_ELEM_OXYGEN
%token TOK_ELEM_PHOSPORUS
%token TOK_ELEM_SULFUR
%token TOK_ELEM_FLUORINE
%token TOK_ELEM_CHLORINE
%token TOK_ELEM_BROMINE
%token TOK_ELEM_IODINE
%token TOK_ERROR

%type<SmilesLexer.Context> formula subformula element;

%define parser_class_name "SmilesParser"
%define stype "SmilesLexer.Context"
%define public
%code imports {
	package chemface;
}

%code {
	private NodePlacer graph;
	
	private PositionedNode lastNode;
	
	public NodePlacer getNodes() {
		return graph;
	}

}

%start smiles

%%

smiles: formula {{
		graph = $1.subgraph;
	}}


formula: subformula {{
		$$.subgraph = $1.subgraph;
	}}
	;
	
subformula:
	subformula element {{
		$$.subgraph.addVertex($1.node);
		$$.subgraph.addEdge($1.node, $$.connectingNode);
		$$.connectingNode = $1.node;
	}}
	| subformula bond element
	| element {{
		$$.subgraph = new NodePlacer();
		$$.subgraph.addVertex($1.node);
		$$.connectingNode = $1.node;
	}}
	;

element:
	TOK_ELEM_ORGANIC {{
		Node node = new Node(
			$1.element.getName(),
			new java.awt.Font("Arial", java.awt.Font.PLAIN, 160));
		$$.node = new PositionedNode(node);
	}}
	| TOK_ELEM_OTHER {{
		Node node = new Node(
			$1.element.getName(),
			new java.awt.Font("Arial", java.awt.Font.PLAIN, 160));
		$$.node = new PositionedNode(node);
	}}
	;

bond:
	TOK_BOND_SINGLE;

%%

