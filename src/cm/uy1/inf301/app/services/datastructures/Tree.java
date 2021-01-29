package cm.uy1.inf301.app.services.datastructures;

public class Tree extends Graph {

	public Tree(Vertex... vertices) {
		super(vertices);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
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
		
		System.out.println(g.encode(v1));
	}

}
