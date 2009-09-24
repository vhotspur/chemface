package chemface;


public class Reactant extends org.jgrapht.graph.SimpleGraph<PositionedNode, Bond> implements Cloneable {


/**
 * Default constructor.
 * 
 */
public Reactant() {
	super(Bond.class);
}

/**
 * Clones the graph.
 * 
 * @warning The parent class makes a shallow copy!
 * 
 */
public Object clone() {
	return super.clone();
}

/**
 * Copy nodes to edges to allow proper bond rendering.
 * 
 */
public void copyNodesToEdges() {
	for (PositionedNode node : vertexSet()) {
		for (PositionedNode other : vertexSet()) {
			if (!containsEdge(node, other)) {
				continue;
			}
			getEdge(node, other).setNodes(node, other);
		}
	}
}

} // class Reactant

