package cm.uy1.inf301.app.services.datastructures;

public class Vertex implements Comparable<Vertex> {
	
	public class VertexColorStates {
		public static final String WHITE = "WHITE";
		public static final String GREY = "GREY";
		public static final String BLACK = "BLACK";
	}

	// Label of this vertex
	String label;
	
	// Index
	int index = -1;
	
	// Color defining the state during BFS
	String color;
	
	// Distance to root vertex, for BFS
	int distance;
	
	// Predecessors during BFS
	Vertex predecessor;
	
	// Is the vertex discovered yet in BFS ?
	boolean discovered;
	
	public Vertex(String pLabel) {
		
		if(pLabel.isBlank() || pLabel.isEmpty()) throw new AssertionError("Empty vertex label");
		this.label = pLabel;
		this.distance = -1;
		this.color = VertexColorStates.WHITE;
		this.predecessor = null;
		this.discovered = false;
	}
	
	public Vertex(String pLabel, int index) {
		
		if(pLabel.isBlank() || pLabel.isEmpty()) throw new AssertionError("Empty vertex label");
		this.label = pLabel;
		this.distance = -1;
		this.color = VertexColorStates.WHITE;
		this.predecessor = null;
		this.discovered = false;
		this.index = index;
	}
	
	public String getLabel() {
		
		return this.label;
	}
	
	public int getIndex() {
		
		return this.index;
	}
	
	public String getColor() {
		
		return this.color;
	}
	
	public int getDistance() {
		
		return this.distance;
	}
	
	public Vertex predecessor() {
		
		return this.predecessor;
	}
	
	public boolean isDiscovered() {
		
		return this.discovered;
	}
	
	public void setLabel(String pLabel) {
		
		this.label = pLabel;
	}
	
	public void setIndex(int pIndex) {
		
		this.index = pIndex;
	}
	
	public void setColor(String pColor) {
		
		assert(pColor == "GREY" || pColor == "WHITE" || pColor == "BLACK");
		this.color = pColor;
	}
	
	public void setDistance(int pDistance) {
		
		this.distance = pDistance;
	}
	
	public void setPredecessor(Vertex pPredecessor) {
		
		this.predecessor = pPredecessor;
	}
	
	public void discover() {
		
		this.discovered = true;
	}
	
	public String toString() {
		
		return "(" + this.label + ", " + this.color + ")"; 
	}
	
	public boolean equals(Vertex another) {
		
		return this.label == another.getLabel();
	}

	@Override
	public int compareTo(Vertex o) {
		
		return this.label.compareTo(o.getColor());
	}
	
	@Override
    public int hashCode() {
    	return this.getLabel().hashCode();
    }
}
