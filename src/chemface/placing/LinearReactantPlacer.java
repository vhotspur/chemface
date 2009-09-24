package chemface.placing;

import chemface.*;

/**
 * Takes care of placing reactant with no branches or cycles.
 * 
 */
public class LinearReactantPlacer implements Placer {

private Reactant reactant_;

public String getName() {
	return "linear";
}

public void setReactant(final Reactant r) {
	reactant_ = (Reactant)r.clone();
}

public Reactant getReactant() {
	return reactant_;
}


public boolean placeOptimally() {
	// first, take care of special case - only one node is there
	if (reactant_.vertexSet().size() == 1) {
		return true;
	}
	
	PositionedNode firstNode = getFirstNode();
	PositionedNode lastNode = getLastNode(firstNode);
	
	if (firstNode == null || lastNode == null) {
		return false;
	}
	
	
	// FIXME
	return false;
}

/**
 * @retval null First node could not be found.
 * 
 */
protected PositionedNode getFirstNode() {
	for (PositionedNode i : reactant_.vertexSet()) {
		if (reactant_.degreeOf(i) == 1) {
			return i;
		}
	}
	
	return null;
}

/**
 * @retval null Last node could not be found.
 * 
 */
protected PositionedNode getLastNode(PositionedNode firstNode) {
	// FIXME
	return null;
}


} // class LinearReactantPlacer
