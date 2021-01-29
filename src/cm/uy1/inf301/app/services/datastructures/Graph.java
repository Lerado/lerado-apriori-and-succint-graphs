package cm.uy1.inf301.app.services.datastructures;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;

public class Graph {
	
	// Array of adjacent vertices contained in the graph
	Hashtable<Vertex, LinkedList<Vertex>> adjacency;
	
	// Encoding
	String code = "";
	
	public Graph(Vertex ...vertices) {
		
		this.adjacency = new Hashtable<Vertex, LinkedList<Vertex>>();
		for(Vertex vertex: vertices) {
			vertex.setIndex(this.adjacency.size()+1);
			this.adjacency.put(vertex, new LinkedList<Vertex>());
		}
	}
	
	public Graph(String ...vertexLabels) {
		
		this.adjacency = new Hashtable<Vertex, LinkedList<Vertex>>();
		for(String label: vertexLabels)
			this.adjacency.put(new Vertex(label, this.adjacency.size()+1), new LinkedList<Vertex>());
	}
	
	public Hashtable<Vertex, LinkedList<Vertex>> getAdjacency() {
		
		return this.adjacency;
	}
	
	public int numberOfVertices() {
		
		return this.adjacency.size();
	}
	
	public String getCode() {
		
		return this.code;
	}
	
	public void add(Vertex vertex) {
		
		if(!this.adjacency.containsKey(vertex)) {
			vertex.setIndex(this.adjacency.size()+1);
			this.adjacency.put(vertex, new LinkedList<Vertex>());
		}
		else
			throw new AssertionError("Duplicated vertex");
	}
	
	public void add(Vertex vertex, Vertex adjacentVertex) {
		
		if(!this.adjacency.get(vertex).contains(adjacentVertex))
			this.adjacency.get(vertex).add(adjacentVertex);
		else
			throw new AssertionError("Duplicated vertex");
	}
	
	public void add(Vertex vertex, Vertex ...adjacentVertexList) {
		
		for(Vertex adjacentVertex: adjacentVertexList) {
			this.add(vertex, adjacentVertex);
		}
	}
	
	public void remove(Vertex vertex) {
		
		this.adjacency.remove(vertex);
		
		for(Vertex vertexLabel: this.adjacency.keySet()) {
			this.adjacency.get(vertexLabel).remove(vertex);
		}
	}
	
	public void remove(Vertex vertex, Vertex vertexToRemove) {
		
		this.adjacency.get(vertex).remove(vertexToRemove);
	}
	
	protected int rank(int bit, int position) {
		
		int counter = 0;
		
		String filteredCode = this.code.trim().replace(" ", "");
		if(!filteredCode.isBlank() && position < filteredCode.length()) {
			for(int i = 0; i<=position; i++)
				if(Integer.valueOf(String.valueOf(filteredCode.charAt(i))) == bit)
					counter++;
		}
		
		return counter;
	}
	
	protected int select(int bit, int count) {
		
		int result = -1;
		
		String filteredCode = this.code.trim().replace(" ", "");
		if(!filteredCode.isBlank()) {
			int counter = 0;
			for(int i = 0; i<filteredCode.length(); i++) {
				counter = Integer.valueOf(String.valueOf(filteredCode.charAt(i))) == bit ? counter + 1 : counter;
				if(counter == count) {
					result = i;
					break;
				}
			}
		}
		
		return result;
	}
	
	public int parent(int vertexToCompute) {
		
		return this.rank(0, this.select(1, vertexToCompute));
	}
	
	public ArrayList<Integer> children(int vertexToCompute) {
		
		ArrayList<Integer> children = new ArrayList<Integer>();
		int startingPosition = this.select(0, vertexToCompute) + 1;
		String filteredCode = this.code.trim().replace(" ", "");
		while(startingPosition < filteredCode.length()
				&& Integer.valueOf(String.valueOf(filteredCode.charAt(startingPosition))) == 1) {
			children.add(this.rank(1, startingPosition));
			startingPosition++;
		}
		return children;
	}
	
	protected void discover(Vertex v, int index) {
		for(Vertex v_: this.adjacency.keySet()) {
			if(v_.equals(v)) { 
				v_.setIndex(index); v.setIndex(index);
				v.discover(); v_.discover();
			}
		}
	}
	
	public String encode(Vertex rootLabel) {
		
		// Resulting code
		code = "";
		
		// Check if rootLabel exists
		if(!this.adjacency.containsKey(rootLabel)) throw new AssertionError("Unknown root label");
		
		// FIFO queue for BFS
		Queue<Vertex> file = new LinkedList<Vertex>();
		file.add(rootLabel);
		code += "10 ";
		
		int discoveryIndex = 0;
		
		while(!file.isEmpty()) {

			Vertex v = file.remove(); if(!v.isDiscovered()) this.discover(v, ++discoveryIndex); // A vertex has been reached
			v.setColor(Vertex.VertexColorStates.GREY); // The vertex adjacency is being analyzed
			
			for(Vertex neighbour: this.adjacency.get(v)) {
				if(!neighbour.isDiscovered()) {
					file.add(neighbour);
					this.discover(neighbour, ++discoveryIndex);
					
					code += "1"; // One child found
				}
				else
					code += "2"; // Another child found but it is a child of another vertex
			}
			
			code += "0 "; // End of children
			
			// Vertex treated
			v.setColor(Vertex.VertexColorStates.BLACK);
		}
		
		return code;
	}
	
	public static void main(String[] args) {
		
//		Vertex v1 = new Vertex("1");
//		Vertex v2 = new Vertex("2");
//		Vertex v3 = new Vertex("3");
//		Vertex v4 = new Vertex("4");
//		Vertex v5 = new Vertex("5");
//		Vertex v6 = new Vertex("6");
//		Vertex v7 = new Vertex("7");
//		Vertex v8 = new Vertex("8");
//		Vertex v9 = new Vertex("9");
//		Vertex v10 = new Vertex("10");
//		
//		Graph g = new Graph(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10);
//		g.add(v1, v2, v3, v4);
//		g.add(v2, v1, v5, v6);
//		g.add(v3, v1);
//		g.add(v4, v1, v7, v8, v9);
//		g.add(v5, v2);
//		g.add(v6, v2);
//		g.add(v7, v4);
//		g.add(v8, v4, v10);
//		g.add(v9, v4);
//		g.add(v10, v8);
//		
//		System.out.println(g.encode(v1));
		Vertex v1 = new Vertex("1");
		Vertex v2 = new Vertex("2");
		Vertex v3 = new Vertex("3");
		Vertex v4 = new Vertex("4");
		Vertex v5 = new Vertex("5");
		Vertex v6 = new Vertex("6");
		Vertex v7 = new Vertex("7");
		Vertex v8 = new Vertex("8");
		Vertex v9 = new Vertex("9");
		Vertex v10 = new Vertex("10");
		
		Tree g = new Tree(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10);
		g.add(v1, v2, v3, v4);
		g.add(v2, v5, v6);
		g.add(v4, v7, v8, v9);
		g.add(v8, v10);
		
		System.out.println(g.encode(v1).trim().replace(" ", ""));
		System.out.println(g.children(4));
		System.out.println(g.getAdjacency());
	}

}
