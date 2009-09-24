package chemface;

/**
 * Positioned node of a chemical structure.
 * 
 */
public class PositionedNode extends Node {

/// Node mass
public final double mass = 10.0;
/// Node charge
public final double charge = 5.0;

/// Node position
public java.awt.geom.Point2D.Double position;
/// Gravity force vector (used when recounting position)
private java.awt.geom.Point2D.Double gravityForceVector = null;
/// Velocity vector (used when recounting position)
private java.awt.geom.Point2D.Double velocityVector = null;

/// Whether this node has fixed position
public boolean fixed = false;

/**
 * Long constructor.
 * 
 * @param s Node text
 * @param font Font used for rendering
 * 
 */
public PositionedNode(String s, java.awt.Font font) {
	super(s, font);
	position = new java.awt.geom.Point2D.Double(0.0, 0.0);
}

/**
 * Short constructor.
 * 
 * @param n Node to create positioned node from
 * 
 */
public PositionedNode(Node n) {
	super(n);
	position = new java.awt.geom.Point2D.Double(0.0, 0.0);
}

/**
 * Clones the node.
 * 
 */
public Object clone() {
	PositionedNode n = new PositionedNode((Node)super.clone());
	return n;
}

/**
 * Tells whether node has fixed position.
 * 
 */
public boolean isFixed() {
	return fixed;
}

/**
 * Tells distance to other node.
 * 
 * @param other Node to count distance to
 * 
 */
public double distance(PositionedNode other) {
	return position.distance(other.position);
}

/**
 * Tells position of the node on the X axis.
 * 
 */
public double getX() {
	return position.getX();
}

/**
 * Tells position of the node on the Y axis.
 * 
 */
public double getY() {
	return position.getY();
}

/**
 * Sets new position.
 * 
 * @param newPosition Position to set
 * 
 */
public void setPosition(java.awt.geom.Point2D.Double newPosition) {
	this.position = newPosition;
}

/**
 * Sets new velocity.
 * 
 * @param newVelocity Velocity vector 
 * 
 */
public void setVelocity(java.awt.geom.Point2D.Double newVelocity) {
	this.velocityVector = newVelocity;
}

/**
 * Resets the gravity forces to zero.
 * 
 */
public void resetGravityForces() {
	gravityForceVector = new java.awt.geom.Point2D.Double(0.0, 0.0);
}

/**
 * Adds a vector to current gravity force.
 * 
 * @param vector Vector to add
 * 
 */
public void addGravityForce(java.awt.geom.Point2D.Double vector) {
	gravityForceVector.setLocation(
		gravityForceVector.getX() + vector.getX(),
		gravityForceVector.getY() + vector.getY());
}

/**
 * Recounts new position and velocity from gravity forces.
 * 
 * @param damping Damping factor
 * @param timestep Time step of the simulation
 * 
 */
public void recountPositionAndVelocity(double damping, double timestep) {
	if (isFixed()) {
		return;
	}
	this.velocityVector = new java.awt.geom.Point2D.Double(
		(this.velocityVector.getX() + timestep * gravityForceVector.getX()) * damping,
		(this.velocityVector.getY() + timestep * gravityForceVector.getY()) * damping);
	this.position = new java.awt.geom.Point2D.Double(
		this.position.getX() + timestep * this.velocityVector.getX(),
		this.position.getY() + timestep * this.velocityVector.getY());
}

/**
 * Tells kinetic energy of the node.
 * 
 */
public double getKineticEnergy() {
	return 0.5 * this.mass * this.velocityVector.distanceSq(0., 0.);
}


}
