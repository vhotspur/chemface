package chemface;

public class NodePlacer extends org.jgrapht.graph.SimpleGraph<PositionedNode, Bond> {

private final double randomShiftCoefficient = 5.0;
private final double gravityCoefficient = 0.5;

public NodePlacer() {
	super(Bond.class);
}

public double getCoulombRepulsion(double r, double q1, double q2) {
	return 0.1 * q1 * q2 / (r*r);
}

public java.awt.geom.Point2D.Double getCoulombForceVector(PositionedNode fixed, 
		PositionedNode moving) {
	double dist = fixed.distance(moving);
	double repuls = getCoulombRepulsion(dist, 10., 10.);
	double dx = moving.getX() - fixed.getX();
	double dy = moving.getY() - fixed.getY();
	return new java.awt.geom.Point2D.Double(repuls*dx/dist, repuls*dy/dist);
}

public double getHookeAttraction(double currentDistance, double optimalDistance) {
	return - 0.01 * (optimalDistance - currentDistance);
}

public java.awt.geom.Point2D.Double getHookeForceVector(PositionedNode fixed, 
		PositionedNode moving, double optimalDistance) {
	double dist = fixed.distance(moving);
	double hooke = getHookeAttraction(dist, optimalDistance);
	double dx = moving.getX() - fixed.getX();
	double dy = moving.getY() - fixed.getY();
	return new java.awt.geom.Point2D.Double(hooke*dx/dist, hooke*dy/dist);
}

public void findOptimalPlacement() {
	// http://en.wikipedia.org/wiki/Force-based_algorithms#Pseudocode
	
	for (PositionedNode node : vertexSet()) {
		node.setVelocity(new java.awt.geom.Point2D.Double(0.0, 0.0));
	}
	shiftAllRandomly(); shiftAllRandomly();
	dumpPositions();
	
	double totalKineticEnergy;
	do {
		// count new forces
		for (PositionedNode node : vertexSet()) {
			node.resetGravityForces();
			
			for (PositionedNode other : vertexSet()) {
				if (other == node) {
					continue;
				}
				
				node.addGravityForce(getCoulombForceVector(other, node));
				
				if (containsEdge(node, other)) {
					node.addGravityForce(getHookeForceVector(
						other, node,
						getEdge(node, other).optimalLength()));
				}
			} // for each other node
		}
		
		// set the new forces and velocities
		totalKineticEnergy = 0.0;
		for (PositionedNode node : vertexSet()) {
			//System.out.printf("another node!\n");
			node.recountPositionAndVelocity(0.2, 0.01);
			totalKineticEnergy += node.getKineticEnergy();
		}
		//System.out.printf("totalKineticEnergy=%f\n", totalKineticEnergy);
	} while (totalKineticEnergy > 0.00001);
}

protected void shiftAllRandomly() {
	java.util.Set<PositionedNode> vertices = vertexSet();
	java.util.Random rnd = new java.util.Random(0); // make it deterministic between runs
	for (PositionedNode node : vertices) {
		if (node.fixed) {
			continue;
		}
		node.position.setLocation(
			node.position.getX() + (rnd.nextDouble()-0.5)*randomShiftCoefficient,
			node.position.getY() + (rnd.nextDouble()-0.5)*randomShiftCoefficient);
	}
}

public void dumpPositions() {
	java.util.Set<PositionedNode> vertices = vertexSet();
	StringBuilder result = new StringBuilder("");
	for (PositionedNode node : vertices) {
		result.append(String.format("[%6.2f:%6.2f] %s\n",
			node.position.getX(), node.position.getY(),
			node.toString()));
	}
	System.out.println(result.toString());
}

public double getGravity(java.awt.geom.Point2D.Double a, java.awt.geom.Point2D.Double b) {
	double d = a.distance(b);
	return 0.00001/(d*d);
}


}
