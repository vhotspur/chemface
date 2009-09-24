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
	java.util.Vector<PositionedNode> path = getPath(firstNode);
	
	if (firstNode == null || path == null) {
		return false;
	}
	
	double fromLeft = 0;
	for (PositionedNode node : path) {
		node.setPosition(
			new java.awt.geom.Point2D.Double(fromLeft, 0.)
		);
		fromLeft += RenderingOptions.getBondLength();
	}
	
	return true;
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
 * @retval null Reactant is not a path.
 * 
 */
protected java.util.Vector<PositionedNode> getPath(PositionedNode firstNode) {
	if (firstNode == null) {
		return null;
	}
	java.util.Vector<PositionedNode> result = new java.util.Vector<PositionedNode>();
	result.add(firstNode);
	
	java.util.Set<Bond> edges = reactant_.edgesOf(firstNode);
	assert(edges.size() == 1);
	
	// move to the second in line
	Bond usedEdge = (Bond)edges.iterator().next();
	PositionedNode lastNode = getOtherEdgeEnd(usedEdge, firstNode);
	while (reactant_.degreeOf(lastNode) == 2) {
		result.add(lastNode);
		
		java.util.Set<Bond> allEdges = reactant_.edgesOf(lastNode);
		Bond newEdge = getOtherEdge(allEdges, usedEdge);
		lastNode = getOtherEdgeEnd(newEdge, lastNode);
		usedEdge = newEdge;
	}
	if (reactant_.degreeOf(lastNode) == 1) {
		result.add(lastNode);
		return result;
	} else {
		return null;
	}
}

protected PositionedNode getOtherEdgeEnd(Bond edge, PositionedNode start) {
	PositionedNode a = reactant_.getEdgeSource(edge);
	PositionedNode b = reactant_.getEdgeTarget(edge);
	if (a == start) {
		return b;
	} else {
		return a;
	}
}

protected Bond getOtherEdge(java.util.Set<Bond> edges, Bond notThisEdge) {
	for (Bond b : edges) {
		if (b != notThisEdge) {
			return b;
		}
	}
	return null;
}

} // class LinearReactantPlacer
