package chemface;

public class Bond implements Cloneable {

private PositionedNode start = null;
private PositionedNode end = null;

private static final float lineWidth = (float)5.0;

public enum Kind {
	SINGLE,
	DOUBLE,
	TRIPLE,
	AROMATIC
};

private Kind kind;

// http://www.java2s.com/Code/Java/2D-Graphics-GUI/CustomStrokes.htm
class DoubleStroke implements java.awt.Stroke {
	java.awt.BasicStroke stroke1, stroke2; // the two strokes to use

	public DoubleStroke(float width1, float width2) {
		stroke1 = new java.awt.BasicStroke(width1); 
		stroke2 = new java.awt.BasicStroke(width2);
	}

	public java.awt.Shape createStrokedShape(java.awt.Shape s) {
		// Use the first stroke to create an outline of the shape
		java.awt.Shape outline = stroke1.createStrokedShape(s);
		// Use the second stroke to create an outline of that outline.
		// It is this outline of the outline that will be filled in
		return stroke2.createStrokedShape(outline);
	}
}

class TripleStroke implements java.awt.Stroke {
	java.awt.BasicStroke stroke1, stroke2, stroke3;

	public TripleStroke(float width1, float width2, float width3) {
		stroke1 = new java.awt.BasicStroke(width1); 
		stroke2 = new java.awt.BasicStroke(width2);
		stroke3 = new java.awt.BasicStroke(width3);
	}

	public java.awt.Shape createStrokedShape(java.awt.Shape s) {
		java.awt.Shape outline = stroke1.createStrokedShape(s);
		java.awt.Shape outline2 = stroke2.createStrokedShape(outline);
		return stroke3.createStrokedShape(outline2);
	}
}

public Bond(Kind kind) {
	this.kind = kind;
}

public Object clone() {
	return new Bond(kind);
}

public double optimalLength() {
	return 400.0;
}

public void setNodes(PositionedNode start, PositionedNode end) {
	this.start = start;
	this.end = end;
}

public void render(java.awt.Graphics2D gr) {
	int startX = (int) start.getX();
	int startY = (int) start.getY();
	int endX = (int) end.getX();
	int endY = (int) end.getY();
	gr.setColor(new java.awt.Color(0, 0, 0));
	
	java.awt.Stroke stroke;
	switch (kind) {
		case SINGLE:
			stroke = new java.awt.BasicStroke(lineWidth);
			break;
		case DOUBLE:
			stroke = new DoubleStroke((float)(lineWidth * 3), lineWidth);
			break;
		case TRIPLE:
			stroke = new TripleStroke(
				(float)(lineWidth * 3), 
				(float)(lineWidth * 3),
				lineWidth);
			break;
		default:
			stroke = new java.awt.BasicStroke((float)0.0);
	}
	gr.setStroke(stroke);
	gr.drawLine(startX, startY, endX, endY);
	
}

}
