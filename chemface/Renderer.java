package chemface;

/**
 * Renderer of the chemical formulaes.
 * 
 */
class Renderer {

/// Nodes of the formulae
private NodePlacer nodes;
/// Result image
private java.awt.image.BufferedImage image_ = null;

/**
 * Constructor.
 * 
 * @param placedNodes Already positioned nodes of the chemical structure
 * 
 */
public Renderer(NodePlacer placedNodes) {
	nodes = placedNodes;
}

/**
 * Tells rendered image.
 * 
 * @retval null When image was not yet rendered
 * 
 */
public java.awt.image.BufferedImage getImage() {
	return image_;
}

/**
 * Renders the image.
 * 
 */
public void render() {
	for (PositionedNode node : nodes.vertexSet()) {
		node.render();
	}
	java.awt.Dimension imageDimension = new java.awt.Dimension();
	normalizePositions(imageDimension);
	nodes.dumpPositions();
	
	image_ = 
		new java.awt.image.BufferedImage(
			(int)imageDimension.getWidth(), (int)imageDimension.getHeight(),
			java.awt.image.BufferedImage.TYPE_4BYTE_ABGR);
	java.awt.Graphics2D gr = image_.createGraphics();
	gr.addRenderingHints(new java.awt.RenderingHints(
		java.awt.RenderingHints.KEY_ANTIALIASING,
		java.awt.RenderingHints.VALUE_ANTIALIAS_ON));
	gr.setRenderingHint(
		java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
		java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	gr.setBackground(new java.awt.Color(0, 0, 0, Node.alphaChannelFullyTransparent));
	
	// draw the bonds
	for (Bond bond : nodes.edgeSet()) {
		bond.render(gr);
	}
	
	// draw the nodes
	for (PositionedNode node : nodes.vertexSet()) {
		int width = (int)node.getImageDimension().getWidth();
		int height = (int)node.getImageDimension().getHeight();
		int x = (int)(node.getX() - width/2.0);
		int y = (int)(node.getY() - height/2.0);
		gr.clearRect(x, y, width, height);
		gr.drawImage(node.getImage(), x, y, null);
	}
}

/**
 * Tells needed rectangle for the canvas.
 * 
 */
protected java.awt.geom.Rectangle2D.Double getCanvasRectangle() {
	java.awt.geom.Rectangle2D.Double result = 
		new java.awt.geom.Rectangle2D.Double();
	
	for (PositionedNode node : nodes.vertexSet()) {
		// coordinates of the center
		double cx = node.getX();
		double cy = node.getY();
		java.awt.Dimension dim = node.getImageDimension();
		double left = cx - dim.getWidth()/2;
		double top = cy - dim.getHeight()/2;
		result = (java.awt.geom.Rectangle2D.Double)result.createUnion(
			new java.awt.geom.Rectangle2D.Double(
			left, top, dim.getWidth(), dim.getHeight()));
	}
	
	return result;
}

/**
 * Normalize nodes position.
 * 
 * By normalization is meant shifting all nodes position to use only
 * first quadrant (both coordinates positive).
 * 
 * @param totalDimension Canvas size
 * 
 */
protected void normalizePositions(java.awt.Dimension totalDimension) {
	java.awt.geom.Rectangle2D.Double canvasRect = getCanvasRectangle();
	totalDimension.setSize(canvasRect.getWidth(), canvasRect.getHeight());
	double shiftX = - canvasRect.getX();
	double shiftY = - canvasRect.getY();
	
	for (PositionedNode node : nodes.vertexSet()) {
		node.setPosition(new java.awt.geom.Point2D.Double(
			node.getX() + shiftX, node.getY() + shiftY));
	}
}

}

