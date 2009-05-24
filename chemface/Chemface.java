package chemface;

import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;

/**
 * Main class of the Chemface.
 * 
 */
public class Chemface {

/**
 * Main.
 * 
 */
public static void main(String[] args) throws java.io.IOException {
	Options options = new Options();
	
	Option outfile = 
		OptionBuilder.withArgName("file").hasArg()
		.isRequired()
		.withDescription("output file")
		.withLongOpt("outfile").create('o');
	options.addOption(outfile);
	
	String formulae;
	java.io.File outputFile;
	
	try {
		CommandLineParser parser = new GnuParser();
		CommandLine line = parser.parse(options, args);
		String [] normalArgs = line.getArgs();
		if (normalArgs.length < 1) {
			throw new ParseException("formulae missing");
		} else if (normalArgs.length > 1) {
			throw new ParseException("only one formulae could be processed");
		}
		formulae = normalArgs[0];
		outputFile = new java.io.File(line.getOptionValue("outfile"));
    } catch (ParseException e) {
		System.err.printf("Eror reading command-line (%s).\n", e.getMessage());
		return;
    }
	
	
	SmilesLexer lexer = new SmilesLexer();
	lexer.setSource(formulae);
	
	SmilesParser parser = new SmilesParser(lexer);
	parser.errorVerbose = true;
	
	boolean parseOkay = parser.parse();
	if (!parseOkay) {
		return;
	}
	
	NodePlacer placer = parser.getNodes();
	
	placer.findOptimalPlacement();
	placer.copyNodesToEdges();
	placer.dumpPositions();

	Renderer renderer = new Renderer(placer);
	renderer.render();
	
	javax.imageio.ImageIO.write(renderer.getImage(), "png", outputFile);
}


} // class Chemface
