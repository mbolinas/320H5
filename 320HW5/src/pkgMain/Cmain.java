


package pkgMain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Map.Entry;

public class Cmain {
	
	
	final static String input = "K:\\Downloads\\input.txt";
	final static String output = "K:\\Downloads\\output.txt";
	static double p5;
	
	public static void main(String[] args) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(input));
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		
		String line = reader.readLine();
		
		line = reader.readLine();
		Graph g = new Graph();
		
		while(line != null) {
			String info[] = line.split(" ");
			//System.out.println(info[0]);
			if(g.nodes.containsKey(Integer.parseInt(info[0])) == false) {
				Node n = new Node(Integer.parseInt(info[0]));
				g.nodes.put(n.name, n);
			}
			if(g.nodes.containsKey(Integer.parseInt(info[1])) == false) {
				Node n = new Node(Integer.parseInt(info[1]));
				g.nodes.put(n.name, n);
			}
			g.nodes.get(Integer.parseInt(info[0])).add_edge_u(g.nodes.get(Integer.parseInt(info[1])));
			
			line = reader.readLine();
		}
		
		Graph domset_g = vertexcover_to_domset(g);
		
		
		
		LinkedList<Node> list = part5(0, domset_g);
		writer.write("Resulting hosts from dominating set heuristic from Assignment 4: ");
		writer.newLine();
		for(Node n : list) {
			writer.write(n.name + " ");
		}
		
		writer.newLine();
		writer.newLine();
		writer.write("Resulting hosts for vertex cover of original graph, calculated using hosts from dominating set heuristic: ");
		writer.newLine();
		LinkedList<Node> hosts = domset_to_vertexcover(domset_g, list);
		for(Node n : hosts) {
			writer.write(n.name + " ");
		}
		
		
		
		reader.close();
		writer.close();
	}
	
	
	public static Graph vertexcover_to_domset(Graph g) {		
		
		LinkedList<Node> list = new LinkedList<>();
		for(Entry<Integer, Node> entry : g.nodes.entrySet()) {
			list.add(entry.getValue());
		}
		
		for(int i = 0; i < list.size(); i++) {
			Node current = list.get(i);
			LinkedList<Node> edges = new LinkedList<>();
			for(Node v : current.adjacent_nodes) {
				edges.add(v);
			}
			
			for(int j = 0; j < edges.size(); j++) {
				if(edges.get(j).visited == false) {
					Node n = new Node((1000 * current.name) + edges.get(j).name);
					n.visited = true;
					current.add_edge_u(n);
					edges.get(j).add_edge_u(n);
					g.add(n);
				}
			}
			current.visited = true;
		}
		
//		for(Entry<Integer, Node> entry : g.nodes.entrySet()) {
//			System.out.println("Node: " + entry.getValue().name);
//		}
		
		
		return g;
	}
	
	public static void reset(Graph g) {
		for(Entry<Integer, Node> x : g.nodes.entrySet()) {
			x.getValue().visited = false;
			
		}
	}
	
	public static void full_reset(Graph g) {
		for(Entry<Integer, Node> x : g.nodes.entrySet()) {
			x.getValue().visited = false;
			x.getValue().distance = Integer.MAX_VALUE;
			x.getValue().jumps = 0;
			x.getValue().parent = null;
		}
	}

	public static LinkedList<Node> part5(int hosts, Graph g){
		//Node host = heuristic1(1, g).getFirst()
		
		
		Node host = null;
		
		for(Entry<Integer, Node> entry : g.nodes.entrySet()) {
			if(host == null || host.adjacent_nodes.size() < entry.getValue().adjacent_nodes.size()) {
				host = entry.getValue();
			}
		}
		
		hosts--;
		host.visited = false;
		
		LinkedList<Node> list = new LinkedList<>();
		list.add(host);
		p5 = part2(list, g);
		reset(g);
		while(p5 > 1) {
			Node potential_host = host;
			host.distance = 0;
			host.visited = true;
			host.jumps = 0;
			
			Queue<Node> queue = new LinkedList<>();
			queue.add(host);
			
			while(queue.size() > 0) {
				Node current_node = queue.poll();
				
				
				for(Node adjacency_pair : current_node.adjacent_nodes) {
					if(adjacency_pair.visited == false) {
						adjacency_pair.visited = true;
						if(current_node.distance + 1 < adjacency_pair.distance) {
							adjacency_pair.parent = current_node;
							adjacency_pair.jumps = current_node.jumps + 1;
							adjacency_pair.distance = current_node.distance + 1;
						}
						queue.add(adjacency_pair);
					}
				}

				if(current_node.distance > potential_host.distance) {
					potential_host = current_node;
				}
			}
			
			//System.out.println(potential_host.name);
			for(int i = potential_host.jumps / 2; i > 0; i--) {
				System.out.println("ok");
				potential_host = potential_host.parent;
			}
			
			list.add(potential_host);
			hosts--;
			host = potential_host;
			reset(g);
			p5 = part2(list, g);
			reset(g);
		}
		full_reset(g);
		p5 = part2(list, g);
		return list;
	}
	
	public static double part2(LinkedList<Node> hosts, Graph g) {
		for(Node host : hosts) {
			reset(g);	//reset all "visited" to false, so that we can rerun our algorithm
			host.distance = 0;
			host.visited = true;
			
			//Perform BFS for each host, updating distance if 'adding' a new host causes the distance to decrease
			Queue<Node> queue = new LinkedList<>();
			queue.add(host);
			
			while(queue.size() > 0) {
				Node current_node = queue.poll();
				
				
				for(Node adjacency_pair : current_node.adjacent_nodes) {
					if(adjacency_pair.visited == false) {
						adjacency_pair.visited = true;
						if(current_node.distance + 1 < adjacency_pair.distance) {
							adjacency_pair.distance = current_node.distance + 1;
						}
						queue.add(adjacency_pair);
					}
				}
			}
			
		}
		
		return calculate_avg(hosts.getFirst(), hosts.size());
	}
	
	public static double calculate_avg(Node host, int size) {
		int count = 0;
		double total_weight = 0;
		host.distance = 0;
		host.visited = true;
		
		Queue<Node> queue = new LinkedList<>();
		queue.add(host);
		
		while(queue.size() > 0) {
			count++;
			Node current_node = queue.poll();
			total_weight = total_weight + current_node.distance;
			
			
			for(Node adjacency_pair : current_node.adjacent_nodes) {
				if(adjacency_pair.visited == true) {
					adjacency_pair.visited = false;
					queue.add(adjacency_pair);
				}
			}
		}
		count--;
		count = count - size;
		return total_weight / count;
	}

	
	
	
	
	public static LinkedList<Node> domset_to_vertexcover(Graph g, LinkedList<Node> hosts) {
		
		//System.out.println(hosts.size());
		
		LinkedList<Node> to_remove = new LinkedList<>();
		LinkedList<Node> to_add = new LinkedList<>();
		
		for(Node host : hosts) {
			if(host.name >= 1000) {
				
				to_remove.add(host);
				
				//hosts.remove(host);
				
				for(Node n : host.adjacent_nodes) {
					if(hosts.contains(n) == false && n.name < 1000) {
						to_add.add(n);
						//hosts.add(n);
						break;
					}
				}
			}
		}
		
		
		for(Node n : to_remove) {
			hosts.remove(n);
		}
		for(Node n : to_add) {
			hosts.add(n);
		}
		
		//System.out.println(hosts.size());
		
		return hosts;
	}
	
	
	


}
