package pkgMain;

import java.util.HashMap;

public class Graph {
	HashMap<Integer, Node> nodes = new HashMap<>();
	
	public Graph() {
		
	}
	
	public void add(Node n) {
		nodes.put(n.name, n);
	}
}
