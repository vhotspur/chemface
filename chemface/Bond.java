package chemface;

public class Bond {

private PositionedNode start = null;
private PositionedNode end = null;

public Bond() {
}

public double optimalLength() {
	return 500.0;
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
	gr.setStroke(new java.awt.BasicStroke((float)5.0));
	gr.drawLine(startX, startY, endX, endY);
}

}
