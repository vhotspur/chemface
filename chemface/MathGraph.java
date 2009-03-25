package chemface;

/**
 * Representation of a mathematical graph.
 * 
 */
public class MathGraph {

/// List of vertices
private Vertex[] vertices_;
/// Unique id generator
private int idCounter = 0;

/**
 * Constructor.
 * 
 */
public MathGraph() {
	vertices_ = null;
}

/**
 * Adds a vertex with no adjacent vertices.
 * @return Vertex id
 * 
 */
public int addVertex() {
	return addVertex(null);
}

/**
 * Adds a vertex with adjacent vertices specified.
 * @return Vertex id
 * 
 */
public int addVertex(int[] adjacents) {
	if (vertices_ == null) {
		vertices_ = new Vertex[1];
	} else {
		Vertex tmp[] = new Vertex[vertices_.length + 1];
		for (int i=0; i<vertices_.length; i++) {
			tmp[i] = vertices_[i];
		}
		vertices_ = tmp;
	}
	
	Vertex newvertex = new Vertex(idCounter++, adjacents);
	vertices_[vertices_.length - 1] = newvertex;
	return newvertex.getId();
}

/**
 * Removes edge orientation. After this, graph would not be oriented any
 * more.
 * 
 */
public void removeEdgeOrientation() {
	// no vertices present, nothing to do
	if (vertices_ == null) {
		return;
	}
	
	for (int i=0; i<vertices_.length; i++) {
		int[] adjs = vertices_[i].getAdjacents();
		for (int j=0; j<adjs.length; j++) {
			vertices_[ adjs[j] ].addAdjacentVertex(i);
		}
	}
}


public void dump() {
	System.out.println("Dump of MathGraph class:");
	if (vertices_ != null) {
		for (int i=0; i<vertices_.length; i++) {
			int[] adjs = vertices_[i].getAdjacents();
			System.out.printf("Vertex %2d: ", i);
			for (int j=0; j<adjs.length; j++) {
				System.out.printf("%2d ", adjs[j]);
			}
			System.out.println();
		}
	}
	System.out.println();
}

} // class MathGraph















/**
 * Graph vertex representation.
 * 
 */
class Vertex {

/// Vertex id
private int id_;
/// List of adjacent vertices
private int[] adjacents_;

/**
 * Constructor.
 * @param id Vertex id
 * @param adjacents List of adjacent vertices
 * 
 */
public Vertex(int id, int[] adjacents) {
	id_ = id;
	adjacents_ = adjacents;
}

/**
 * Tells vertex id.
 * @return Vertex id
 * 
 */
public int getId() {
	return id_;
}

/**
 * Adds adjacent vertex.
 * @param id Adjacent vertex id
 * 
 */
public void addAdjacentVertex(int id) {
	if (adjacents_ == null) {
		adjacents_ = new int[1];
		adjacents_[0] = id;
	} else {
		int l = adjacents_.length;
		// look whether it is not here already
		for (int i=0; i<l; i++) {
			if (adjacents_[i] == id) {
				return;
			}
		}
		adjacents_ = java.util.Arrays.copyOf(adjacents_, l + 1);
		adjacents_[l] = id;
	}
}

public int[] getAdjacents() {
	if (adjacents_ == null) {
		return new int[0];
	} else {
		return adjacents_;
	}
}

} // class Vertex