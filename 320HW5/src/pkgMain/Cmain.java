/*
 * @Marc Bolinas
 * 5/15/18
 * CISC320
 * HW5
 * 
 */


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
	
	final static String directory = "K:\\Downloads\\";
	final static String input = directory + "input.txt";
	final static String output_part1 = directory + "output_after_vertexcover_to_dominatingset.txt";
	final static String output_final = directory + "output_final.txt";
	
	static double p5;	//legacy code from assignment 4
	
	public static void main(String[] args) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(input));
		BufferedWriter writer = new BufferedWriter(new FileWriter(output_final));
		BufferedWriter writerp1 = new BufferedWriter(new FileWriter(output_part1));
		
		int cases = Integer.parseInt(reader.readLine());
		writerp1.write(cases);
		writerp1.newLine();
		int count = 1;
		
		while(cases > 0) {
			count++;
			int edge_count = Integer.parseInt(reader.readLine().split(" ")[1]);
			Graph g = new Graph();
			
			while(edge_count > 0) {
				String line = reader.readLine();
				String info[] = line.split(" ");
				//Ensure nodes already exist in Graph g, then add the edge
				if(g.nodes.containsKey(Integer.parseInt(info[0])) == false) {
					Node n = new Node(Integer.parseInt(info[0]));
					g.nodes.put(n.name, n);
				}
				if(g.nodes.containsKey(Integer.parseInt(info[1])) == false) {
					Node n = new Node(Integer.parseInt(info[1]));
					g.nodes.put(n.name, n);
				}
				//edges between nodes are stored within each individual node
				g.nodes.get(Integer.parseInt(info[0])).add_edge_u(g.nodes.get(Integer.parseInt(info[1])));
				//g.edges is used mainly to count how many edges there are total in the graph, important for file IO in part1
				g.edges.add(new Edge(g.nodes.get(Integer.parseInt(info[0])), g.nodes.get(Integer.parseInt(info[1]))));
				edge_count--;
			}
			
			
			
			Graph domset_g = vertexcover_to_domset(g);
			reset(domset_g);	//these reset functions are used for setting visited = false
			
			//write the graph to file, after converting it in part 1
			writerp1.write(domset_g.nodes.size() + " " + domset_g.edges.size() + " 0 0");
			writerp1.newLine();
			for(Edge e : domset_g.edges) {
				writerp1.write(e.start.name + " " + e.end.name);
				writerp1.newLine();
			}
			
			//write the list of hosts calculated using the heuristic from assignment 4
			LinkedList<Node> list = part5(0, domset_g);
			writer.write("Test case #" + count);
			writer.newLine();
			writer.write("Resulting hosts from dominating set heuristic from Assignment 4: ");
			writer.newLine();
			for(Node n : list) {
				writer.write(n.name + " ");
			}
			reset(domset_g);
			
			//now write the list of hosts that are valid for the original vertex cover
			writer.newLine();
			writer.write("Resulting hosts for vertex cover of original graph, calculated using hosts from dominating set heuristic: ");
			writer.newLine();
			LinkedList<Node> hosts = domset_to_vertexcover(domset_g, list);
			for(Node n : hosts) {
				writer.write(n.name + " ");
			}
			writer.newLine();
			writer.newLine();
			
			
			reset(domset_g);
			cases--;
		}
		

		reader.close();
		writer.close();
		writerp1.close();
	}
	
	
	public static Graph vertexcover_to_domset(Graph g) {		
		
		LinkedList<Node> list = new LinkedList<>();
		for(Entry<Integer, Node> entry : g.nodes.entrySet()) {
			list.add(entry.getValue());
		}
		
		//for each node originally in g...
		for(int i = 0; i < list.size(); i++) {
			Node current = list.get(i);
			LinkedList<Node> edges = new LinkedList<>();
			for(Node v : current.adjacent_nodes) {
				edges.add(v);
			}
			
			
			//take each edge and convert it into a node and two outgoing edges
			for(int j = 0; j < edges.size(); j++) {
				if(edges.get(j).visited == false) {
					Node n = new Node((1000 * current.name) + edges.get(j).name);
					n.visited = true;
					current.add_edge_u(n);
					edges.get(j).add_edge_u(n);
					g.add(n);
					g.edges.add(new Edge(edges.get(j), n));
					g.edges.add(new Edge(current, n));
				}
			}
			current.visited = true;
		}
		
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

	
	//heuristic from assignment 4
	public static LinkedList<Node> part5(int hosts, Graph g){
		
		
		Node host = null;
		
		//set initial host equal to the node with the highest degree
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
			
			//go through all nodes, potential host equals the node farthest away from hosts
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
			
			//instead of having the farthest away node the next host, the node halfway between potential_host and host should become the new host
			//it's a better way of decreasing the total weight
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
	
	//from assignment 4, imported because part5 uses part2
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
		
		LinkedList<Node> to_remove = new LinkedList<>();
		LinkedList<Node> to_add = new LinkedList<>();
		
		for(Node host : hosts) {
			//if a host is not part of the original graph, remove it from the hosts list and make one of it's connected nodes the host
			if(host.name >= 1000) {
				
				to_remove.add(host);
				
				for(Node n : host.adjacent_nodes) {
					if(hosts.contains(n) == false && n.name < 1000) {
						to_add.add(n);
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
		
		return hosts;
	}
	
	
	


}
