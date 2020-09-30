package graph.impl;

import graph.INode;

public class edge implements Comparable<edge>{
	
	INode n1;
	INode n2;
	int weight;
	
	public edge(INode node1, INode node2, int weight) {
		this.n1=node1;
		this.n2=node2;
		this.weight= weight;
	}
	
	public int compareTo(edge o) {
		return this.weight - o.weight;
	}

}