%{
%}



%language "Java"
%token TOK_BOND_SINGLE
%token TOK_BOND_DOUBLE
%token TOK_BOND_TRIPLE
%token<SmilesLexer.Context> TOK_ELEM_ORGANIC
%token<SmilesLexer.Context> TOK_ELEM_OTHER
%token<SmilesLexer.Context> TOK_ELEM_CUSTOM
%token<SmilesLexer.Context> TOK_ELEM_REFNUMBER
%token TOK_BRANCH_START
%token TOK_BRANCH_END
%token TOK_ERROR

%type<SmilesLexer.Context> formula bondedsubformula formula_without_branches branch branches element bare_element bond;

%define parser_class_name "SmilesParser"
%define stype "SmilesLexer.Context"
%define public
%code imports {
	package chemface;
	
	/**
	 * Automatically generated parser of the SMILES grammar.
	 * 
	 */
}

%code {
	private Reactant graph;
	
	private PositionedNode lastNode;
	
	public Reactant getNodes() {
		return graph;
	}
	
	
	/**
	 * Joins two graphs.
	 * 
	 * @param a First graph (the one that takes over the other)
	 * @param b Second graph
	 * @param joinerA Node used for joining from first graph)
	 * @param joinerB Node used for joining from second graph)
	 * @param bond Bond used for joining
	 * 
	 */
	public void joinGraphs(Reactant a, Reactant b,
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
		// join cycles
		for (PositionedNode v1 : graph.vertexSet()) {
			for (PositionedNode v2 : graph.vertexSet()) {
				if (v2 == v1) {
					continue;
				}
				if (v1.cycleReference == 0) {
					continue;
				}
				if (v1.cycleReference == v2.cycleReference) {
					graph.addEdge(v1, v2, new Bond(Bond.Kind.SINGLE));
				}
			}
		}
	}}


formula:
	formula_without_branches {{
		$$.graph = $1.graph;
		$$.firstNode = $1.firstNode;
		$$.lastNode = $1.lastNode;
		$$.hookEdge = $1.hookEdge;
		$$.node = $1.node;
	}}
	| formula branches bond formula_without_branches {{
		for (SmilesLexer.Context c : $2.branch) {
			joinGraphs($1.graph, c.graph,
				$1.lastNode, c.firstNode,
				c.hookEdge);
		}
		joinGraphs($1.graph, $4.graph,
			$1.lastNode, $4.firstNode,
			$3.bond);
		$$.graph = $1.graph;
		$$.firstNode = $1.firstNode;
		$$.lastNode = $3.lastNode;
	}}
	| formula branches formula_without_branches {{
		for (SmilesLexer.Context c : $2.branch) {
			joinGraphs($1.graph, c.graph,
				$1.lastNode, c.firstNode,
				c.hookEdge);
		}
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
		$$.graph = new Reactant();
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
	
branches:
	branches branch {{
		$1.branch.add($2);
		$$.branch = $1.branch;
	}}
	| branch {{
		$$.branch = new java.util.Vector<SmilesLexer.Context>();
		$$.branch.add($1);
	}}
	;

branch:
	TOK_BRANCH_START bondedsubformula TOK_BRANCH_END {{
		$$.graph = $2.graph;
		$$.hookEdge = $2.hookEdge;
		$$.firstNode = $2.firstNode;
		$$.lastNode = $2.lastNode;
	}}
	;
	

element:
	bare_element {{
		$$.node = $1.node;
	}}
	| bare_element TOK_ELEM_REFNUMBER {{
		$$.node = $1.node;
		$$.node.cycleReference = $2.refNo;
	}}
	;
	
bare_element:
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
	| TOK_ELEM_CUSTOM {{
		$$.node = $1.node;
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

