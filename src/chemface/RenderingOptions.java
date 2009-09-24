package chemface;

/**
 * Wrapper for rendering options.
 * 
 */
public class RenderingOptions {

/// Font used for rendering
private static java.awt.Font font;

/// Default font name
public static final String defaultFontName = "Arial";
/// Default font size
public static final int defaultFontSize = 90;

/**
 * Loads default settings.
 * 
 */
public static void loadDefaults() {
	font = new java.awt.Font(defaultFontName, java.awt.Font.PLAIN, defaultFontSize);
}

/**
 * Tells the font to use for rendering.
 * 
 */
public static java.awt.Font getFont() {
	return font;
}

/**
 * Sets new font for rendering.
 * 
 * @param name Font name
 * @param size Font size
 * 
 */
public static void setFont(String name, int size) {
	font = new java.awt.Font(name, java.awt.Font.PLAIN, size);
}

/**
 * Tells the bond length.
 * 
 */
public static double getBondLength() {
	return 250;
}

/**
 * Tells the bond width.
 * 
 */
public static double getBondWidth() {
	return 5.0;
}


} // class RenderingOptions
