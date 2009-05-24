package chemface;

public class PositionedNode extends Node {


public java.awt.geom.Point2D.Double position;
private java.awt.geom.Point2D.Double newPosition = null;
private java.awt.geom.Point2D.Double gravityForceVector = null;
private java.awt.geom.Point2D.Double velocityVector = null;

public boolean fixed = false;

public PositionedNode(String s, java.awt.Font font) {
	super(s, font);
	position = new java.awt.geom.Point2D.Double(0.0, 0.0);
}

public PositionedNode(Node n) {
	super(n);
	position = new java.awt.geom.Point2D.Double(0.0, 0.0);
}

public boolean isFixed() {
	return fixed;
}

public double distance(PositionedNode other) {
	return position.distance(other.position);
}

public double getX() {
	return position.getX();
}

public double getY() {
	return position.getY();
}

public void setNewPosition(java.awt.geom.Point2D.Double newPosition) {
	this.newPosition = newPosition;
}

public void setVelocity(java.awt.geom.Point2D.Double newVelocity) {
	this.velocityVector = newVelocity;
}

public void updatePosition() {
	if (newPosition == null) {
		return;
	}
	this.position = newPosition;
}


public void resetGravityForces() {
	gravityForceVector = new java.awt.geom.Point2D.Double(0.0, 0.0);
}

public void addGravityForce(java.awt.geom.Point2D.Double vector) {
	gravityForceVector.setLocation(
		gravityForceVector.getX() + vector.getX(),
		gravityForceVector.getY() + vector.getY());
}

public void recountPositionAndVelocity(double damping, double timestep) {
	this.velocityVector = new java.awt.geom.Point2D.Double(
		(this.velocityVector.getX() + timestep * gravityForceVector.getX()) * damping,
		(this.velocityVector.getY() + timestep * gravityForceVector.getY()) * damping);
	//System.out.printf("velocityVector=(%f, %f)\n", this.velocityVector.getX(), this.velocityVector.getY());
	this.position = new java.awt.geom.Point2D.Double(
		this.position.getX() + timestep * this.velocityVector.getX(),
		this.position.getY() + timestep * this.velocityVector.getY());
}

public double getKineticEnergy() {
	return 10.0 * this.velocityVector.distanceSq(0., 0.);
}

public void setNewPositionFromGravity() {
	if (gravityForceVector == null) {
		setNewPosition(position);
	} else {
		setNewPosition(new java.awt.geom.Point2D.Double(
			position.getX() + gravityForceVector.getX(),
			position.getY() + gravityForceVector.getY()));
	}
}


}
