package pkgMain;

import java.util.LinkedList;

public class Node {

	
	final int name;
	int distance = Integer.MAX_VALUE;
	boolean visited = false;
	//Map<Node, Edge> adjacent_nodes = new HashMap<>();
	LinkedList<Node> adjacent_nodes = new LinkedList<>();
	Node parent;
	int jumps = 0;
	
	
	public Node(int n) {
		name = n;
	}
	
	public void add_edge(Node destination) {
		adjacent_nodes.add(destination);
	}
	
	public void add_edge_u(Node destination) {
		adjacent_nodes.add(destination);
		destination.adjacent_nodes.add(this);
	}
	
}
