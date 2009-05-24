package chemface;

/**
 * Representation of a single node in the chemical structure.
 * 
 */
public class Node {

/// Node description
private String descr_;
/// Font used for drawing text
private java.awt.Font font_;
/// Rendered image
private java.awt.image.BufferedImage image_;

public static final int alphaChannelFullyTransparent = 0;
protected static final int imageMargin = 15;

/**
 * Constructs node from a descriptive string.
 * @param s Node description (no bonds are expected)
 * @param font Font to use when rendering
 * 
 */
public Node(String s, java.awt.Font font) {
	descr_ = s;
	font_ = font;
	image_ = null;
}

public Node(Node n) {
	descr_ = n.descr_;
	font_ = n.font_;
	image_ = null;
}

public String toString() {
	return descr_;
}

/**
 * Renders the node into internal buffer.
 * 
 */
public void render() {
	/*
	 * Count the dimensions
	 * 
	 */
	int height, width;
	int letterHeight;
	{
		java.awt.image.BufferedImage image = 
			new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_4BYTE_ABGR);
		java.awt.Graphics2D gr = image.createGraphics();
		gr.setFont(font_);
		java.awt.FontMetrics metrics = gr.getFontMetrics();
		letterHeight = height = metrics.getHeight();
		width = metrics.stringWidth(descr_);
	}
	System.out.printf("Counted dimensions as %dx%d\n", width, height);
	
	/*
	 * Start drawing
	 * 
	 */
	java.awt.image.BufferedImage image = 
		new java.awt.image.BufferedImage(
			width + imageMargin*2 + 2, 
			height*3 + imageMargin*2 + 2, 
			java.awt.image.BufferedImage.TYPE_4BYTE_ABGR);
	java.awt.Graphics2D gr = image.createGraphics();
	
	/*
	 * Prepare the canvas
	 * 
	 */
	gr.setBackground(java.awt.Color.GRAY);
	/*
	 * Set antialising (http://mindprod.com/jgloss/antialiasing.html)
	 * 
	 */
	gr.addRenderingHints(new java.awt.RenderingHints(
		java.awt.RenderingHints.KEY_ANTIALIASING,
		java.awt.RenderingHints.VALUE_ANTIALIAS_ON));
	gr.setRenderingHint(
		java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
		java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

	java.awt.font.FontRenderContext frc = gr.getFontRenderContext();
	
	
	/*
	 * Get do the business
	 * 
	 */
	int i = 0;
	int len = descr_.length();
	// y-axis of the text
	int baseline = height*2 + imageMargin;
	// x-axis of the text
	int shift = imageMargin;
	// extra shift, used when drawing subscripts
	int aboutToShift = 0;
	// remember these for correct count of image height
	boolean usedSuperscript = false;
	boolean usedSubscript = false;
	
	while (i < len) {
		/*
		 * Reset colors and fonts
		 * 
		 */
		gr.setFont(font_);	
		gr.setColor(java.awt.Color.BLUE);	
		char c = descr_.charAt(i);
		
		/*
		 * Handle normal text
		 * 
		 */
		if (Character.isLetter(c)) {
			StringBuffer text = new StringBuffer();			
			while ((i < len) && Character.isLetter(descr_.charAt(i))) {				
				text.append(descr_.charAt(i));				
				i++;
			}
			shift += aboutToShift;
			shift += renderNormalText(text.toString(), gr, shift, baseline);
			aboutToShift = 0;
			continue;
		}
		
		/*
		 * Handle subscripts
		 * 
		 */
		if ((c == '_') || Character.isDigit(c)) {
			StringBuffer number = new StringBuffer();
			if (c == '_') {
				int pos = getBracedText(descr_, i+1, number);
				if (pos > 0) {
					i = pos;
				} else {
					// FIXME - issue a warning
					break;
				}
			} else {
				while ((i<len) && Character.isDigit(descr_.charAt(i))) {
					number.append(descr_.charAt(i));
					i++;
				}				
			}
			// now, 'number' holds what shall be rendered as a subscript
			int w = renderSubscript(number.toString(), gr, shift, baseline);
			if (w > aboutToShift) {
				aboutToShift = w;
			}
			usedSubscript = true;
			continue;
		}
		
		/*
		 * Handle superscripts
		 * 
		 */
		if (c == '^') {
			StringBuffer supscript = new StringBuffer();
			i++;
			int pos = getBracedText(descr_, i, supscript);
			if (pos > 0) {
				i = pos;
			} else {
				// FIXME - issue a warning
				break;
			}
			int w = renderSuperscript(supscript.toString(), gr, shift, baseline);
			if (w > aboutToShift) {
				aboutToShift = w;
			}
			usedSuperscript = true;
			continue;
		}
		i++;
	}
	
	
	/*
	 * Trim the image
	 * - if we got so far, the 'image' holds rendered image and
	 *   in 'shift' we have the real width 
	 * - all we got to do is to create a new image and copy only part
	 *   'shift' wide
	 *   - and with really used height
	 * 
	 */
	// shift after last sub(super) script if necessary
	shift += aboutToShift;
	
	// count vertical crop coordinates
	int bottom = baseline;
	int top = baseline + font_.getSize();
	if (usedSubscript) {
		bottom = baseline + getSubscriptBaselineShift(getSubscriptFont(font_));
	}
	if (usedSuperscript) {
		java.awt.Font supfont = getSuperscriptFont(font_);
		top = baseline + getSuperscriptBaselineShift(supfont) - supfont.getSize();
	}
	
	// FIXME - these formulaes for counting font height are not very accurate
	// and could be rethinked (if time allows)
	java.awt.Rectangle trim = getTrimRectangle(image);
	image_ = image.getSubimage(
		trim.x - imageMargin, 
		trim.y - imageMargin,
		trim.width + 2*imageMargin, 
		trim.height + 2*imageMargin);
}

/**
 * Renders normal text into the canvas.
 * @param text Text to render
 * @param graphics Canvas where to render
 * @param x Start of the text
 * @param y Baseline of the text 
 * @return Width of the rendered text
 * 
 */
protected int renderNormalText(String text, java.awt.Graphics2D graphics, int x, int y) {
	java.awt.FontMetrics metrics = graphics.getFontMetrics();
	graphics.drawString(text, x, y);
	return metrics.stringWidth(text);
}

/**
 * Renders subscript into the canvas.
 * @param text Text to render
 * @param graphics Canvas where to render
 * @param x Start of the text
 * @param y Baseline of the text 
 * @return Width of the rendered text
 * 
 */
protected int renderSubscript(String text, java.awt.Graphics2D graphics, int x, int y) {
	graphics.setFont(getSubscriptFont(graphics.getFont()));
	graphics.setColor(java.awt.Color.RED);
	java.awt.FontMetrics metrics = graphics.getFontMetrics();
	graphics.drawString(text, x, y + getSubscriptBaselineShift(graphics.getFont()));
	return metrics.stringWidth(text);	
}

/**
 * Renders superscript text into the canvas.
 * @param text Text to render
 * @param graphics Canvas where to render
 * @param x Start of the text
 * @param y Baseline of the text 
 * @return Width of the rendered text
 * 
 */
protected int renderSuperscript(String text, java.awt.Graphics2D graphics, int x, int y) {
	graphics.setFont(getSuperscriptFont(graphics.getFont()));
	graphics.setColor(java.awt.Color.ORANGE);
	java.awt.FontMetrics metrics = graphics.getFontMetrics();
	graphics.drawString(text, x, y + getSuperscriptBaselineShift(graphics.getFont()));
	return metrics.stringWidth(text);	
}

/**
 * Returns font prepared to be used for subscript.
 * @param font Font to derive from
 * @return Subscript font
 * 
 */
protected java.awt.Font getSubscriptFont(java.awt.Font font) {
	java.awt.Font subfont = font.deriveFont((float)(font.getSize2D() * 0.66));
	if (subfont == null) {
		// FIXME - issue a warning
		subfont = font;
	}
	return subfont;
}


/**
 * Returns font prepared to be used for superscript.
 * @param font Font to derive from
 * @return Superscript font
 * 
 */
protected java.awt.Font getSuperscriptFont(java.awt.Font font) {
	java.awt.Font superfont = font.deriveFont((float)(font.getSize2D() * 0.61));
	if (superfont == null) {
		// FIXME - issue a warning
		superfont = font;
	}
	return superfont;
}

/**
 * Counts vertical shift of a baseline for a subscript.
 * @param font Used font
 * @return Baseline shift
 * 
 */
protected int getSubscriptBaselineShift(java.awt.Font font) {
	return font.getSize() / 3;
}

/**
 * Counts vertical shift of a baseline for a superscript.
 * @param font Used font
 * @return Baseline shift
 * 
 */
protected int getSuperscriptBaselineShift(java.awt.Font font) {
	return - 4 * font.getSize() / 5;
}

/**
 * Get the text between curly braces. If at the opening position is not
 * opening curly brace, single character is returned (thus, that is not
 * an error, it is a - though a poor - simulation of approach used in
 * TeX).
 * @return Position of first character after closing parentheses
 * @retval -1 Error (unclosed parenthesis)
 * 
 */
protected int getBracedText(String text, int openParen, StringBuffer result) {
	try {
		char c = text.charAt(openParen);
		if (c == '{') {
			while ((c=descr_.charAt(++openParen)) != '}') {
				result.append(c);
			}
			return openParen;
		} else {
			result.append(c);
			return openParen + 1;
		}
	} catch (java.lang.IndexOutOfBoundsException e) {
		return -1;
	}
}

/**
 * Returns rendered image.
 * @return Rendered image
 * @retval null No rendering was performed, no image is ready
 * 
 */
public java.awt.image.BufferedImage getImage() {
	return image_;
}

public java.awt.Dimension getImageDimension() {
	return new java.awt.Dimension(image_.getWidth(), image_.getHeight());
}

public static java.awt.Rectangle getTrimRectangle(java.awt.image.BufferedImage image) {
	int width = image.getWidth();
	int height = image.getHeight();
	
	int left = 0;
	int right = width - 1;
	int top = 0;
	int bottom = height - 1;
	
	for (int x = 0; x < width; x++) {
		boolean hasOpaque = false;
		for (int y = 0; y<height; y++) {
			if (!isPixelFullyTransparent(image, x, y)) {
				hasOpaque = true;
				break;
			}
		}
		if (hasOpaque) {
			left = Math.max(left, x);
			break;
		}
	}
	for (int x = width-1; x >= 0; x--) {
		boolean hasOpaque = false;
		for (int y = 0; y<height; y++) {
			if (!isPixelFullyTransparent(image, x, y)) {
				hasOpaque = true;
				break;
			}
		}
		if (hasOpaque) {
			right = Math.min(right, x);
			break;
		}
	}
	
	for (int y = 0; y < height; y++) {
		boolean hasOpaque = false;
		for (int x = 0; x<width; x++) {
			if (!isPixelFullyTransparent(image, x, y)) {
				hasOpaque = true;
				break;
			}
		}
		if (hasOpaque) {
			top = Math.max(top, y);
			break;
		}
	}
	for (int y = height-1; y >= 0; y--) {
		boolean hasOpaque = false;
		for (int x = 0; x<width; x++) {
			if (!isPixelFullyTransparent(image, x, y)) {
				hasOpaque = true;
				break;
			}
		}
		if (hasOpaque) {
			bottom = Math.min(bottom, y);
			break;
		}
	}
	System.out.printf("%d\n", left);
	return new java.awt.Rectangle(left, top, right - left + 1, bottom - top + 1);
}

public static boolean isPixelFullyTransparent(
		java.awt.image.BufferedImage image, 
		int x, int y) {
	java.awt.image.ColorModel cm = image.getColorModel();
	int rgb = image.getRGB(x, y);
	Object pixel = cm.getDataElements(rgb, null);
	int alpha = cm.getAlpha(pixel);
	return alpha == alphaChannelFullyTransparent;
}

} // class Node


