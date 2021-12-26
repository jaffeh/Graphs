package implementation;

import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;

/**
 * Implements a graph. We use two maps: one map for adjacency properties
 * (adjancencyMap) and one map (dataMap) to keep track of the data associated
 * with a vertex.
 * 
 * @author cmsc132
 * 
 * @param <E>
 */
public class Graph<E> {
	/* You must use the following maps in your implementation */
	private HashMap<String, HashMap<String, Integer>> adjacencyMap;
	private HashMap<String, E> dataMap;

	public Graph() {
		adjacencyMap = new HashMap();
		dataMap = new HashMap();
	}

	public void addDirectedEdge(String startVertexName, String endVertexName, int cost) {
		// Throws exception if start of end vertex is not apart of graph
		if (!dataMap.containsKey(startVertexName) || !dataMap.containsKey(endVertexName)) {
			throw new IllegalArgumentException("Start or end vertex not apart of graph");
		}
		// Else : go ahead and update/add the directed edge
		else {
			// If it doesn't have that key, make a key mapping to an empty map
			if (!adjacencyMap.containsKey(startVertexName)) {
				adjacencyMap.put(startVertexName, new HashMap<String, Integer>());
			}
			// Then, the empty map has the
			Map<String, Integer> retrieve = adjacencyMap.get(startVertexName);
			retrieve.put(endVertexName, cost);
		}
	}

	public void addVertex(String vertexName, E data) {
		// Can't add a vertex that exists, so thus error
		if (dataMap.containsKey(vertexName)) {
			throw new IllegalArgumentException("Vertex already exists in the graph");
		}
		// Else : go ahead and add the vertex
		else {
			// Add vertex to the dataMap & store the data
			dataMap.put(vertexName, data);
			// Make an empty map, to add to adjacencymap(String, EmptyMap)
			HashMap<String, Integer> emptyMap = new HashMap<String, Integer>();
			adjacencyMap.put(vertexName, emptyMap);
		}
	}

	public void doBreadthFirstSearch(String startVertexName, CallBack<E> callback) {
		TreeSet<String> neighbors = new TreeSet<String>(adjacencyMap.get(startVertexName).keySet());
		TreeSet<String> visited = new TreeSet<String>();
		TreeSet<String> queue = new TreeSet<String>();
		// Visit initial start vertex
		callback.processVertex(startVertexName, dataMap.get(startVertexName));
		visited.add(startVertexName);
		if (neighbors.size() == 0) {
			return;
		} else {
			while (neighbors.size() != 0) {
				String s = neighbors.pollFirst();
				if (!visited.contains(s)) {
					visited.add(s);
					queue.add(s);
					callback.processVertex(s, dataMap.get(s));
				}
			}
			while (queue.size() != 0) {
				String v = queue.pollFirst();
				if (!queue.contains(v)) {
					doBreadthFirstSearch(v, callback, visited, queue);
				}
			}
		}
	}

	private void doBreadthFirstSearch(String startVertexName, CallBack<E> callback, TreeSet<String> visited,
			TreeSet<String> queue) {
		TreeSet<String> neighbors = new TreeSet<String>(adjacencyMap.get(startVertexName).keySet());
		if (!visited.contains(startVertexName)) {
			callback.processVertex(startVertexName, dataMap.get(startVertexName));
		}
		if (neighbors.size() == 0) {
			return;
		} else {
			while (neighbors.size() != 0) {
				String s = neighbors.pollFirst();
				if (!visited.contains(s)) {
					visited.add(s);
					queue.add(s);
					callback.processVertex(s, dataMap.get(s));
				}
			}
			while (queue.size() != 0) {
				String v = queue.pollFirst();
				if (!queue.contains(v)) {
					doBreadthFirstSearch(v, callback, visited, queue);
				}
			}
		}
	}

	public void doDepthFirstSearch(String startVertexName, CallBack<E> callback) {
		TreeSet<String> visited = new TreeSet<String>();
		TreeSet<String> discovered = new TreeSet<String>(adjacencyMap.get(startVertexName).keySet());

		callback.processVertex(startVertexName, dataMap.get(startVertexName));
		visited.add(startVertexName);

		while (discovered.size() != 0) {
			String s = discovered.pollLast();
			if (!visited.contains(s)) {
				callback.processVertex(s, dataMap.get(s));
				visited.add(s);
				discovered.addAll(adjacencyMap.get(s).keySet());
			}
		}

	}

	public String findLowestCost(HashMap<String, Integer> mapper) {
		String s = "";
		int min = Integer.MAX_VALUE;
		for (Entry<String, Integer> entry : mapper.entrySet()) {
			if (entry.getValue() < min) {
				min = entry.getValue();
				s = entry.getKey();
			}
		}
		return s;
	}

	public Map<String, Integer> getAdjacentVertices(String vertexName) {
		Map<String, Integer> container = new HashMap<String, Integer>();
		if (adjacencyMap.containsKey(vertexName)) {
			container = adjacencyMap.get(vertexName);
		}
		return container;
	}

	public int getCost(String startVertexName, String endVertexName) {
		return adjacencyMap.get(startVertexName).get(endVertexName);
	}

	public E getData(String vertex) {
		return dataMap.get(vertex);
	}

	public Set<String> getVertices() {
		return dataMap.keySet();
	}

	public int doDijkstras(String startVertexName, String endVertexName, ArrayList<String> shortestPath) {if (!dataMap.containsKey(startVertexName) || !dataMap.containsKey(endVertexName)) {
		throw new IllegalArgumentException("Vertrices provided are invalid");
	} else {
		// Stores visited paths
		HashSet<String> storage = new HashSet<String>();

		// This is C and P as a table in Hashmaps
		HashMap<String, String> predecessor = new HashMap<String, String>();
		HashMap<String, Integer> cost = new HashMap<String, Integer>();
		int finishedSize = dataMap.size();
		// Making Dijkstra table with default infinity cost and default "None"
		// predecessor THEN update start vertex value
		int valueSetter = Integer.MAX_VALUE;
		for (String vertex : dataMap.keySet()) {
			predecessor.put(vertex, "None");
			cost.put(vertex, valueSetter);
		}
		// Initial state of first node
		cost.put(startVertexName, 0);
		int times = 0;
		
		while (times < 50) {
			String temp = "";
			Integer smallestCost = Integer.MAX_VALUE;

			for (Map.Entry<String, Integer> entry : cost.entrySet()) {
				if (entry.getValue() < smallestCost && !storage.contains(entry.getKey())) {
					smallestCost = entry.getValue();
					temp = entry.getKey();
				}
			}
			// Then add temp
			storage.add(temp);

			// HashSet<String> setty = new TreeSet<String>(adjacencyMap.get(temp).keySet());
			
			// HashSet<String>(adjacencyMap.get(vertex).keySet());
			Map<String, Integer> neighbors = getAdjacentVertices(temp);
			
			//Compares and updates values of P/C
			for (Map.Entry<String, Integer> entry : neighbors.entrySet()) {
				String adjacent = entry.getKey();
				if (!storage.contains(adjacent)) {
					if (smallestCost + entry.getValue() < cost.get(adjacent)) {
						cost.put(adjacent, smallestCost + entry.getValue());
						predecessor.put(adjacent, temp);
					}
				}
			}
			times++;
		}
		// Traversing predecessor to get path in reverse and add to the provided arraylist
		String last = endVertexName;
		shortestPath.add(last);
		while (!last.equals("None") && !last.equals(startVertexName)) {
			last = predecessor.get(last);
			shortestPath.add(last);
		}
		//Decides what to do in case of disconnected vertex and when Max Value isn't changed(Not found)
		if (cost.get(endVertexName) == Integer.MAX_VALUE && predecessor.get(endVertexName).equals("None")) {
			shortestPath.clear();
			shortestPath.add("None");
			return -1;
		}
		else {
			Collections.reverse(shortestPath);
		}
		return cost.get(endVertexName);
	}
    }

	public String toString() {
		TreeSet<String> container = new TreeSet<String>(adjacencyMap.keySet());

		String answer = "";
		answer += "Vertices: ";
		answer += container.toString() + "\n";
		answer += "Edges: " + "\n";

		TreeMap<String, HashMap<String, Integer>> parse = new TreeMap<String, HashMap<String, Integer>>(adjacencyMap);
		for (Map.Entry<String, HashMap<String, Integer>> entry : parse.entrySet()) {
			HashMap<String, Integer> parseM = entry.getValue();
			answer += ("Vertex(" + entry.getKey() + ")--->" + entry.getValue() + "\n");
		}

		answer = answer.substring(0, answer.length() - 1);
		return answer;

	}

}