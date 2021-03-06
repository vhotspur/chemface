package chemface.placing;

import chemface.*;

public class GravitationalPlacer implements Placer {
	
/// Initial random shift (fine tunning node placement)
private final double randomShiftCoefficient = 1.0;
/// Hooke's constant (fine tunning node placement)
private final double hookeConstant = 0.01;
/// Constant for Coulomb's law (fine tunning node placement)
private final double coloumbConstant = 100;
/// Constant for node movement simulation (fine tunning node placement)
private final double timeStep = 0.9;
/// Damping constant to stop movement (fine tunning node placement)
private final double damping = 0.9;
/// Limit of kinetic energy when the movement - simulation - stops (fine tunning node placement)
private final double energyEpsilon = 0.0001;
/// Reactant to be positioned
private Reactant graph_ = null;
	
public GravitationalPlacer() {
	
}

public String getName() {
	return "gravitational";
}

public void setReactant(final Reactant r) {
	graph_ = (Reactant)r.clone();
}

public Reactant getReactant() {
	return graph_;
}

/**
 * Counts the Coulomb repulsion.
 * 
 * @param r Nodes distance
 * @param q1 Charge of the first node
 * @param q2 Charge of the second node
 * 
 */
public double getCoulombRepulsion(double r, double q1, double q2) {
	return coloumbConstant * q1 * q2 / (r*r);
}

/**
 * Counts the force vector of a Coulomb repulsion.
 * 
 * @param fixed The "fixed" node (it really does not matter which one it is)
 * @param moving The "moving" node
 * 
 */
public java.awt.geom.Point2D.Double getCoulombForceVector(PositionedNode fixed, 
		PositionedNode moving) {
	double dist = fixed.distance(moving);
	double repuls = getCoulombRepulsion(dist, fixed.charge, moving.charge);
	double dx = moving.getX() - fixed.getX();
	double dy = moving.getY() - fixed.getY();
	return new java.awt.geom.Point2D.Double(repuls*dx/dist, repuls*dy/dist);
}

/**
 * Counts the Hooke attraction.
 * 
 * @param currentDistance Current distance of the nodes
 * @param optimalDistance Optimal distance of the nodes
 * 
 */
public double getHookeAttraction(double currentDistance, double optimalDistance) {
	return hookeConstant * (optimalDistance - currentDistance);
}

/**
 * Counts the force vector of a Hooke attraction.
 * 
 * @param fixed The "fixed" node (it really does not matter which one it is)
 * @param moving The "moving" node
 * @param optimalDistance Optimal distance of the two nodes
 *
 */
public java.awt.geom.Point2D.Double getHookeForceVector(PositionedNode fixed, 
		PositionedNode moving, double optimalDistance) {
	double dist = fixed.distance(moving);
	double hooke = getHookeAttraction(dist, optimalDistance);
	double dx = moving.getX() - fixed.getX();
	double dy = moving.getY() - fixed.getY();
	return new java.awt.geom.Point2D.Double(hooke*dx/dist, hooke*dy/dist);
}

/**
 * Finds optimal placement for the nodes in the graph.
 * 
 */
public boolean placeOptimally() {
	// http://en.wikipedia.org/wiki/Force-based_algorithms#Pseudocode
	
	for (PositionedNode node : graph_.vertexSet()) {
		node.setVelocity(new java.awt.geom.Point2D.Double(0.0, 0.0));
	}
	shiftAllRandomly(); shiftAllRandomly();
	
	double totalKineticEnergy;
	do {
		// count new forces
		for (PositionedNode node : graph_.vertexSet()) {
			node.resetGravityForces();
			
			for (PositionedNode other : graph_.vertexSet()) {
				if (other == node) {
					continue;
				}
				
				node.addGravityForce(getCoulombForceVector(other, node));
				
				if (graph_.containsEdge(node, other)) {
					node.addGravityForce(getHookeForceVector(
						other, node,
						graph_.getEdge(node, other).optimalLength()));
				}
			} // for each other node
		}
		
		// set the new forces and velocities
		totalKineticEnergy = 0.0;
		for (PositionedNode node : graph_.vertexSet()) {
			//System.out.printf("another node!\n");
			node.recountPositionAndVelocity(damping, timeStep);
			totalKineticEnergy += node.getKineticEnergy();
		}
		//System.out.printf("totalKineticEnergy=%f\n", totalKineticEnergy);
	} while (totalKineticEnergy > energyEpsilon);
	
	// this method works always
	return true;
}

/**
 * Shifts all nodes randomly by a small distance.
 * 
 */
protected void shiftAllRandomly() {
	java.util.Set<PositionedNode> vertices = graph_.vertexSet();
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

/**
 * Dumps nodes positions to stdout.
 * 
 */
public void dumpPositions() {
	java.util.Set<PositionedNode> vertices = graph_.vertexSet();
	StringBuilder result = new StringBuilder("");
	for (PositionedNode node : vertices) {
		result.append(String.format("%-3s (%6.2f, %6.2f)::",
			node.toString(), node.getX(), node.getY()));
		for (PositionedNode other : graph_.vertexSet()) {
			if (!graph_.containsEdge(node, other)) {
				continue;
			}
			double optDist = graph_.getEdge(node, other).optimalLength();
			double dist = node.distance(other);
			result.append(String.format(" %3s(%6.2f %6.2f) ",
				other.toString(),
				dist, optDist));
		}
		result.append("\n");
	}
	System.out.println(result.toString());
}


} // class GravitationalPlacer
