package graph.impl;

import graph.INode;

public class Path implements Comparable<Path>{
	 
	  INode name;
	  int weight;

	  public Path(INode name, int weight){
	    this.name = name;
	    this.weight = weight;
	  }

	  public int compareTo(Path other){
	    return this.weight - other.weight;
	  }
}
