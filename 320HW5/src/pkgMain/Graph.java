package pkgMain;

import java.util.HashMap;
import java.util.LinkedList;

public class Graph {
	HashMap<Integer, Node> nodes = new HashMap<>();
	LinkedList<Edge> edges = new LinkedList<>();
	
	public Graph() {
		
	}
	
	public void add(Node n) {
		nodes.put(n.name, n);
	}
}
