%{
%}



%language "Java"
%token TOK_BOND_SINGLE
%token TOK_BOND_DOUBLE
%token TOK_BOND_TRIPLE
%token<SmilesLexer.Context> TOK_ELEM_ORGANIC
%token<SmilesLexer.Context> TOK_ELEM_OTHER
%token TOK_BRANCH_START
%token TOK_BRANCH_END
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

%type<SmilesLexer.Context> formula bondedsubformula formula_without_branches branch element bond;

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

	public void joinGraphs(NodePlacer a, NodePlacer b,
			PositionedNode joinerA, PositionedNode joinerB,
			Bond bond) {
		for (PositionedNode vertex : b.vertexSet()) {
			a.addVertex(vertex);
		}
		for (PositionedNode vertex : b.vertexSet()) {
			for (PositionedNode vertex2 : b.vertexSet()) {
				if (b.containsEdge(vertex, vertex2)) {
					a.addEdge(vertex, vertex2, b.getEdge(vertex, vertex2));
				}
			}
		}
		a.addEdge(joinerA, joinerB, bond);
	}
}

%start smiles

%%

smiles: formula {{
		graph = $1.graph;
	}}


formula:
	formula_without_branches {{
		$$.graph = $1.graph;
		$$.firstNode = $1.firstNode;
		$$.lastNode = $1.lastNode;
		$$.hookEdge = $1.hookEdge;
		$$.node = $1.node;
	}}
	| formula branch bond formula_without_branches {{
		joinGraphs($1.graph, $2.graph,
			$1.lastNode, $2.firstNode,
			$2.hookEdge);
		joinGraphs($1.graph, $4.graph,
			$1.lastNode, $4.firstNode,
			$3.bond);
		$$.graph = $1.graph;
		$$.firstNode = $1.firstNode;
		$$.lastNode = $3.lastNode;
	}}
	| formula branch formula_without_branches {{
		joinGraphs($1.graph, $2.graph,
			$1.lastNode, $2.firstNode,
			$2.hookEdge);
		joinGraphs($1.graph, $3.graph,
			$1.lastNode, $3.firstNode,
			new Bond(Bond.Kind.SINGLE));
		$$.graph = $1.graph;
		$$.firstNode = $1.firstNode;
		$$.lastNode = $3.lastNode;
	}}
	;

formula_without_branches:
	formula_without_branches element {{
		$1.graph.addVertex($2.node);
		$1.graph.addEdge($2.node, $1.lastNode, new Bond(Bond.Kind.SINGLE));
		$$.graph = $1.graph;
		$$.lastNode = $2.node;
		$$.firstNode = $1.firstNode;
	}}
	| formula_without_branches bond element {{
		$1.graph.addVertex($3.node);
		$1.graph.addEdge($3.node, $1.lastNode, $2.bond);
		$$.graph = $1.graph;
		$$.lastNode = $3.node;
		$$.firstNode = $1.firstNode;
	}} 
	| element {{
		$$.graph = new NodePlacer();
		$$.graph.addVertex($1.node);
		$$.firstNode = $1.node;
		$$.lastNode = $1.node;
	}}
	;


bondedsubformula:
	formula {{
		$$.graph = $1.graph;
		$$.hookEdge = new Bond(Bond.Kind.SINGLE);
		$$.firstNode = $1.firstNode;
		$$.lastNode = $1.lastNode;
	}}
	| bond formula {{
		$$.graph = $2.graph;
		$$.hookEdge = $1.bond;
		$$.firstNode = $2.firstNode;
		$$.lastNode = $2.lastNode;
	}}
	

branch:
	TOK_BRANCH_START bondedsubformula TOK_BRANCH_END {{
		$$.graph = $2.graph;
		$$.hookEdge = $2.hookEdge;
		$$.firstNode = $2.firstNode;
		$$.lastNode = $2.lastNode;
	}}
	;
	

element:
	TOK_ELEM_ORGANIC {{
		Node node = new Node(
			$1.element.getName(),
			RenderingOptions.getFont());
		$$.node = new PositionedNode(node);
	}}
	| TOK_ELEM_OTHER {{
		Node node = new Node(
			$1.element.getName(),
			RenderingOptions.getFont());
		$$.node = new PositionedNode(node);
	}}
	;

bond:
	TOK_BOND_SINGLE {{
		$$.bond = new Bond(Bond.Kind.SINGLE);
	}}
	| TOK_BOND_DOUBLE {{
		$$.bond = new Bond(Bond.Kind.DOUBLE);
	}}
	| TOK_BOND_TRIPLE {{
		$$.bond = new Bond(Bond.Kind.TRIPLE);
	}}
	;

%%

