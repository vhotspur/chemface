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

static int verbose = 0;

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
	options.addOption("v", false, "display extra messages");
	
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
		verbose += line.hasOption("v") ? 1 : 0;
		formulae = normalArgs[0];
		outputFile = new java.io.File(line.getOptionValue("outfile"));
    } catch (ParseException e) {
		System.err.printf("Eror reading command-line (%s).\n", e.getMessage());
		return;
    }
	
	verboseMessageStart(1, "Initializing...");
	SmilesLexer lexer = new SmilesLexer();
	lexer.setSource(formulae);
	
	SmilesParser parser = new SmilesParser(lexer);
	parser.errorVerbose = true;
	
	verboseMessageEnd(1, " [done]");
	
	boolean parseOkay = parser.parse();
	if (!parseOkay) {
		return;
	}
	
	verboseMessageStart(1, "Positioning nodes...");
	NodePlacer placer = parser.getNodes();
	
	placer.findOptimalPlacement();
	placer.copyNodesToEdges();
	verboseMessageEnd(1, " [done]");

	verboseMessageStart(1, "Rendering...");
	Renderer renderer = new Renderer(placer);
	renderer.render();
	verboseMessageEnd(1, " [done]");
	
	javax.imageio.ImageIO.write(renderer.getImage(), "png", outputFile);
	verboseMessage(1, "Terminating...");
}

private static void verboseMessageStart(int level, String message) {
	verboseMessage(level, message, true, false);
}

private static void verboseMessageEnd(int level, String message) {
	verboseMessage(level, message, false, true);
}

private static void verboseMessage(int level, String message) {
	verboseMessage(level, message, true, true);
}

private static void verboseMessage(int level, String message, 
		boolean displayStartingDecorator, boolean terminateLine) {
	if (verbose < level) {
		return;
	}
	System.err.printf("%s%s%s",
		displayStartingDecorator ? "[chemface]: " : "",
		message,
		terminateLine ? "\n" : "");
}

} // class Chemface
