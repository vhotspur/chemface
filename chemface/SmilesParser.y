%{
package chemface;

%}

%language "Java"
%token TOK_BOND_SINGLE

%define parser_class_name "SmilesParser"
%define public

%start smiles

%%

smiles: TOK_BOND_SINGLE {{
	$$ = 56;
}};


%%

