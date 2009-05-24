package chemface;

/**
 * Wrapper for rendering options. This class is a singleton.
 * 
 */
public class RenderingOptions {

/// Pointer to the (single) existing instance of RenderingOptions
private static volatile RenderingOptions instance_ = null;
/// Number of references to the instance
private static int instanceX_ = 0;

/**
 * Returns instance of RenderingOptions.
 * @return Instance of RenderingOptions.
 * 
 */
public static synchronized RenderingOptions getInstance() {
	if (instanceX_++ == 0) {
		instance_ = new RenderingOptions();
	}
	return instance_;
}

public static java.awt.Font getFont() {
	return new java.awt.Font("Arial", java.awt.Font.PLAIN, 90);
}

public static double getBondLength() {
	return 250;
}

/**
 * Constructor, currently does nothing.
 * 
 */
protected RenderingOptions() {
	
}


} // class RenderingOptions
