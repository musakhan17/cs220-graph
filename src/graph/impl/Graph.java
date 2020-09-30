package graph.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import graph.IGraph;
import graph.INode;
import graph.NodeVisitor;

/**
 * A basic representation of a graph that can perform BFS, DFS, Dijkstra,
 * and Prim-Jarnik's algorithm for a minimum spanning tree.
 * 
 * @author jspacco
 *
 */
public class Graph implements IGraph
{
    private Map<String, INode> hmap = new HashMap<>();
    /**
     * Return the {@link Node} with the given name.
     * 
     * If no {@link Node} with the given name exists, create
     * a new node with the given name and return it. Subsequent
     * calls to this method with the same name should
     * then return the node just created.
     * 
     * @param name
     * @return
     */
    public INode getOrCreateNode(String name) {
    	if(hmap.containsKey(name)) {
        	return hmap.get(name);
    	}
        INode n = new Node(name);
        this.hmap.put(name,n);
        return n;
    }

    /**
     * Return true if the graph contains a node with the given name,
     * and false otherwise.
     * 
     * @param name
     * @return
     */
    public boolean containsNode(String name) {
    	if(hmap.containsKey(name)) {
    		return true;
    	}
    	return false;
    }

    /**
     * Return a collection of all of the nodes in the graph.
     * 
     * @return
     */
    public Collection<INode> getAllNodes() {
    	return hmap.values();
    }
    
    /**
     * Perform a breadth-first search on the graph, starting at the node
     * with the given name. The visit method of the {@link NodeVisitor} should
     * be called on each node the first time we visit the node.
     * 
     * 
     * @param startNodeName
     * @param v
     */
    public void breadthFirstSearch(String startNodeName, NodeVisitor v)
    {
    	Set<INode> visited = new HashSet<>();
    	LinkedList<INode> queue = new LinkedList<INode>(); 

    	queue.offer(getOrCreateNode(startNodeName));
    	visited.add(hmap.get(startNodeName));
    	while (queue.size() != 0) 
    	{
    		INode x = queue.poll(); 
    		Collection<INode> c = x.getNeighbors();
    		Iterator<INode> i = c.iterator(); 
    		while (i.hasNext()) 
    		{ 
    			INode n = i.next(); 
    			if (!queue.contains(n)) 
    			{
    				queue.add(n);
    				visited.add(n);
    				v.visit(n);
    			}
    		}
    	}
    }

    /**
     * Perform a depth-first search on the graph, starting at the node
     * with the given name. The visit method of the {@link NodeVisitor} should
     * be called on each node the first time we visit the node.
     * 
     * 
     * @param startNodeName
     * @param v
     */
    public void depthFirstSearch(String startNodeName, NodeVisitor v)
    {
    	Set<INode> visited = new HashSet<>();
    	LinkedList<INode> queue = new LinkedList<INode>(); 

    	queue.push(getOrCreateNode(startNodeName));
    	visited.add(hmap.get(startNodeName));
    	while (queue.size() != 0) 
    	{
    		INode x = queue.pop(); 
    		Collection<INode> c = x.getNeighbors();
    		Iterator<INode> i = c.iterator(); 
    		while (i.hasNext()) 
    		{ 
    			INode n = i.next(); 
    			if (!queue.contains(n)) 
    			{
    				queue.add(n);
    				visited.add(n);
    				v.visit(n);
    			}
    		}
    	}
    }

    /**
     * Perform Dijkstra's algorithm for computing the cost of the shortest path
     * to every node in the graph starting at the node with the given name.
     * Return a mapping from every node in the graph to the total minimum cost of reaching
     * that node from the given start node.
     * 
     * <b>Hint:</b> Creating a helper class called Path, which stores a destination
     * (String) and a cost (Integer), and making it implement Comparable, can be
     * helpful. Well, either than or repeated linear scans.
     * 
     * @param startName
     * @return
     */
    public Map<INode,Integer> dijkstra(String startName) {
    	
    	Map<INode,Integer> fin = new HashMap<>();
        PriorityQueue<Path> queue = new PriorityQueue<>();
        queue.add(new Path(getOrCreateNode(startName), 0));
        
        while ( fin.size() < getAllNodes().size()) {
        	Path n = queue.poll();
        	INode Node1 = n.name;
        	if (fin.containsKey(Node1))
        		continue;
        	int weight = n.weight;
        	fin.put(Node1, weight);
        	for(INode temp : Node1.getNeighbors()) {
        		queue.add(new Path(temp, Node1.getWeight(temp)+weight));
        	}	
        }
        return fin;
    }
    
    
    
    /**
     * Perform Prim-Jarnik's algorithm to compute a Minimum Spanning Tree (MST).
     * 
     * The MST is itself a graph containing the same nodes and a subset of the edges 
     * from the original graph.
     * 
     * @return
     */
    public IGraph primJarnik() {
    	IGraph f = new Graph();
		INode initiate = (INode) this.getAllNodes().toArray()[0];
		PriorityQueue<edge> queue = new PriorityQueue<>();
		for(INode temp : initiate.getNeighbors()) {
			queue.add(new edge(initiate,temp, initiate.getWeight(temp)));
		}
		while(f.getAllNodes().size()!=this.getAllNodes().size()) {
			edge e = queue.poll();
			INode node1 = e.n1;
			INode node2 = e.n2;
			if(f.containsNode(node1.getName()) && f.containsNode(node2.getName()))
				continue;
			INode x = f.getOrCreateNode(node1.getName());
			INode y = f.getOrCreateNode(node2.getName());
			x.addUndirectedEdgeToNode(y,e.weight);
			for(INode temp : node2.getNeighbors()) {
        		if (!temp.equals(node1)) {
        		queue.add(new edge(node2,temp,node2.getWeight(temp)));
        		}
        	}
		}
		return f;
    }
}