package chemface;

public class Chemface {


public static void main(String[] args) {
	java.awt.image.BufferedImage img = 
		new java.awt.image.BufferedImage(1000, 1000, java.awt.image.BufferedImage.TYPE_4BYTE_ABGR);
	//Node node = new Node("H_{2}^{+I}S^{+VI}O_4^{-II}", new java.awt.Font("Arial", java.awt.Font.PLAIN, 160));
	Node node = new Node("H", new java.awt.Font("Arial", java.awt.Font.PLAIN, 160));
	//node.render();
	Node node2 = new Node("H2", new java.awt.Font("Arial", java.awt.Font.PLAIN, 160));
	//node2.render();
	Node node3 = new Node("N3", new java.awt.Font("Arial", java.awt.Font.PLAIN, 160));
	//node3.render();
	
	RenderingOptions opts = RenderingOptions.getInstance();
	try {
		node2.render();
		javax.imageio.ImageIO.write(node2.getImage(), "png", new java.io.File("tmp.png"));
	} catch (java.io.IOException e) {
	} //*/
	
	
	PositionedNode n = new PositionedNode(node);
	n.fixed = true;
	PositionedNode n2 = new PositionedNode(node2);
	PositionedNode n3 = new PositionedNode(node3);
	
	Bond bond1 = new Bond();
	Bond bond2 = new Bond();
	
	NodePlacer placer = new NodePlacer();
	
	placer.addVertex(n);
	placer.addVertex(n2);
	placer.addVertex(n3);
	placer.addEdge(n, n2, bond1);
	placer.addEdge(n, n3, bond2);
	
	placer.findOptimalPlacement();
	placer.copyNodePositionsToEdges();
	placer.dumpPositions();

	Renderer renderer = new Renderer(placer);
	renderer.render();
	try {
		javax.imageio.ImageIO.write(renderer.getImage(), "png", new java.io.File("out.png"));
	} catch (java.io.IOException e) {
		System.out.println(e.toString());
	} 
}


} // class Chemface
