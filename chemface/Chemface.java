package chemface;

public class Chemface {


public static void main(String[] args) {
	java.awt.image.BufferedImage img = 
		new java.awt.image.BufferedImage(1000, 1000, java.awt.image.BufferedImage.TYPE_4BYTE_ABGR);
	Node node = new Node("H_{2}^{+I}S^{+VI}O_4^{-II}", new java.awt.Font("Arial", java.awt.Font.PLAIN, 160));
	node.render();
	RenderingOptions opts = RenderingOptions.getInstance();
	try {
		javax.imageio.ImageIO.write(node.getImage(), "png", new java.io.File("tmp.png"));
	} catch (java.io.IOException e) {
	}
	
	MathGraph graph = new MathGraph();
	int tmp = graph.addVertex();
	graph.addVertex(new int[]{tmp});
	graph.addVertex(new int[]{tmp});
	
	graph.dump();
	graph.removeEdgeOrientation();	
	graph.dump();
}


} // class Chemface
