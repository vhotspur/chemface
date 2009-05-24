package chemface;

public class Chemface {


public static void main(String[] args) throws java.io.IOException {
	SmilesLexer lexer = new SmilesLexer();
	lexer.setSource("CC(C)(C)C");
	
	SmilesParser parser = new SmilesParser(lexer);
	parser.errorVerbose = true;
	//parser.setDebugLevel(1);
	boolean parseOkay = parser.parse();
	if (!parseOkay) {
		System.out.println("Parsing went gaga!");
		return;
	}
	
	System.out.println("Parsing went fine!");
	NodePlacer placer = parser.getNodes();
	
	placer.findOptimalPlacement();
	placer.copyNodePositionsToEdges();
	placer.dumpPositions();

	Renderer renderer = new Renderer(placer);
	renderer.render();
	try {
		javax.imageio.ImageIO.write(renderer.getImage(), "png", new java.io.File("out.png"));
	} catch (java.io.IOException e) {
		System.out.println(e.toString());
	}
	
}


} // class Chemface
