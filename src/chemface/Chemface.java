package chemface;

import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;
import chemface.placing.*;

/**
 * Main class of the Chemface.
 * 
 */
public class Chemface {

static int verbose = 0;

static final String defaultOutfile = "smiles_out.png";

/**
 * Main.
 * 
 */
public static void main(String[] args) throws java.io.IOException {
	Options options = new Options();
	
	Option optOutfile = 
		OptionBuilder.withArgName("file").hasArg()
		.withDescription(
			String.format("output file (default `%s')", defaultOutfile))
		.withLongOpt("outfile").create('o');
	options.addOption(optOutfile);
	
	Option optFont =
		OptionBuilder.withArgName("font").hasArg()
		.withDescription("font name")
		.withLongOpt("font").create('f');
	options.addOption(optFont);
	
	Option optFontSize = 
		OptionBuilder.withArgName("size").hasArg()
		.withDescription("font size")
		.withLongOpt("font-size").create('s');
	options.addOption(optFontSize);
	
	options.addOption("v", false, "display extra messages");
	options.addOption("h", "help", false, "display this help");
	
	String formulae;
	java.io.File outputFile;
	
	RenderingOptions.loadDefaults();
	
	try {
		CommandLineParser parser = new GnuParser();
		CommandLine line = parser.parse(options, args);
		
		if (line.hasOption("h")) {
			HelpFormatter hf = new HelpFormatter();
			hf.printHelp(80, 
				"chemface [opts] SMILES-notation-formulae",
				"Chemface is a converter of SMILE notation to PNG image",
				options,
				"(c) Vojtech Horky");
			return;
		}
		
		String [] normalArgs = line.getArgs();
		if (normalArgs.length < 1) {
			throw new ParseException("formulae missing");
		} else if (normalArgs.length > 1) {
			throw new ParseException("only one formulae could be processed");
		}
		formulae = normalArgs[0];
		
		verbose += line.hasOption("v") ? 1 : 0;
				
		{
			String outfile = line.getOptionValue("o"); 
			outputFile = new java.io.File(outfile == null ? defaultOutfile : outfile);
		}
		
		{
			String fontName = RenderingOptions.defaultFontName;
			int fontSize = RenderingOptions.defaultFontSize;
			if (line.hasOption("f")) {
				fontName = line.getOptionValue("f");
			}
			if (line.hasOption("s")) {
				fontSize = (new Integer(line.getOptionValue("s"))).intValue();
			}
			RenderingOptions.setFont(fontName, fontSize);
		}
	} catch (ParseException e) {
		System.err.printf("Eror reading command-line (%s).\n", e.getMessage());
		return;
	} catch (NumberFormatException e) {
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
	Reactant reactant = parser.getNodes();
	reactant = findBestPlacement(reactant);
	reactant.copyNodesToEdges();
	verboseMessageEnd(1, " [done]");

	verboseMessageStart(1, "Rendering...");
	Renderer renderer = new Renderer(reactant);
	renderer.render();
	verboseMessageEnd(1, " [done]");
	
	javax.imageio.ImageIO.write(renderer.getImage(), "png", outputFile);
	verboseMessage(1, "Terminating...");
}

/// Starts a log message without line ending but with decorator
private static void verboseMessageStart(int level, String message) {
	verboseMessage(level, message, true, false);
}

/// Finishes log message (with line ending but with no decorator)
private static void verboseMessageEnd(int level, String message) {
	verboseMessage(level, message, false, true);
}

/// Prints a simple log message
private static void verboseMessage(int level, String message) {
	verboseMessage(level, message, true, true);
}

/**
 * Prints verbose log message.
 * 
 * @param level Verbosity level of the message
 * @param message Message text (without `\n')
 * @param displayStartingDecorator Whether to preceed the line with chemface signature
 * @param terminateLine Whether to append line ending to the message
 * 
 */
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


private static Reactant findBestPlacement(Reactant r) {
	java.util.Vector<Placer> placers = 
		new java.util.Vector<Placer>();
	// add here all placers available
	// only make sure that the GravitationalPlacer is the last
	// one as this placer always succeeds in placing
	placers.add(new GravitationalPlacer());
	
	for (Placer p : placers) {
		p.setReactant(r);
		boolean placingSuccess = p.placeOptimally();
		if (placingSuccess) {
			return p.getReactant();
		}
	}
	
	// fallback, we shall never get here
	return r;
}

} // class Chemface
