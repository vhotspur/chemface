package chemface;

/**
 * Wrapper for rendering options.
 * 
 */
public class RenderingOptions {


/**
 * Tells the font to use for rendering.
 * 
 */
public static java.awt.Font getFont() {
	return new java.awt.Font("Arial", java.awt.Font.PLAIN, 90);
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
