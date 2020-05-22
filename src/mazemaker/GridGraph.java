package mazemaker;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import graph.IGraph;
import graph.INode;
import graph.NodeVisitor;
import graph.impl.Graph;

/**
 * Pretty cool class for creating "grid graphs", which are graphs with the nodes
 * arranged on a grid of rows and columns. Performing a DFS on this graph creates
 * a maze.
 *
 * @author jspacco
 *
 */
public class GridGraph
{
    public static IGraph makeGridGraph(int rows, int cols) {
        IGraph graph = new Graph();
        // Each node is named something like r2c1, which means "row 2, column 1".
        // Nodes have edges to other nodes that are to their left or right, and
        // above or below.
        for (int r = 0; r < rows; r++){
            for (int c = 0; c < cols; c++){
                if (r > 0) {
                    INode current = graph.getOrCreateNode(String.format("r%dc%d", r, c));
                    INode other = graph.getOrCreateNode(String.format("r%dc%d", r-1, c));
                    current.addUndirectedEdgeToNode(other, 1);
                }
                if (r < rows-1) {
                    INode current = graph.getOrCreateNode(String.format("r%dc%d", r, c));
                    INode other = graph.getOrCreateNode(String.format("r%dc%d", r+1, c));
                    current.addUndirectedEdgeToNode(other, 1);
                }
                if (c > 0) {
                    INode current = graph.getOrCreateNode(String.format("r%dc%d", r, c));
                    INode other = graph.getOrCreateNode(String.format("r%dc%d", r, c-1));
                    current.addUndirectedEdgeToNode(other, 1);
                }
                if (c < cols-1) {
                    INode current = graph.getOrCreateNode(String.format("r%dc%d", r, c));
                    INode other = graph.getOrCreateNode(String.format("r%dc%d", r, c+1));
                    current.addUndirectedEdgeToNode(other, 1);
                }
            }
        }
        return graph;
    }

    public static IGraph createMaze(IGraph g, String start){
        // g should be a GridGraph
        IGraph result = new Graph();

        g.depthFirstSearch(start, new NodeVisitor() {
            // stack of nodes we've already seen in the order in which we've seen them
            Stack<INode> nodes = new Stack<INode>();
            @Override
            public void visit(INode node) {
                // Go through the stack of nodes we've already seen,
                // and connect to it.
                for (INode n : nodes){
                    if (node.hasEdge(n)){
                        INode src = result.getOrCreateNode(n.getName());
                        INode dst = result.getOrCreateNode(node.getName());
                        src.addUndirectedEdgeToNode(dst, 1);
                        break;
                    }
                }
                nodes.push(node);
            }
        });
        return result;
    }

    public static String generateGraphVizGridGraph(IGraph g, int numRows, int numCols, String name){
        StringBuilder result = new StringBuilder();
        result.append(String.format("graph %s {\n", name));

        StringBuilder locations = new StringBuilder();

        for (int r=0; r<numRows; r++){
            List<String> list = new LinkedList<String>();
            for (int c=0; c<numCols; c++){
                String label = String.format("r%dc%d", r, c);
                list.add(label);
                locations.append(String.format("%s [\n\tlabel = %s\npos = \"%d,%d\" ];\n", label, label, r, c));
            }
            String rank = String.join(", ", list);
            // remove last comma
            result.append(String.format("{ rank=same; %s }\n", rank));
        }
        //result.append(locations);
        // TODO: generate edges
        Set<String> done = new HashSet<>();
        for (INode node : g.getAllNodes()){
            for (INode n : node.getNeighbors()){
                String srcName = node.getName();
                String dstName = n.getName();
                if (dstName.compareTo(srcName) < 0){
                    String tmp = srcName;
                    srcName = dstName;
                    dstName = tmp;
                }
                String key = srcName + "--" + dstName;
                if (done.contains(key)){
                    continue;
                }
                done.add(key);
                result.append(String.format("%s -- %s;\n", srcName, dstName));
                //result.append("%s [\n\tlabel = %s\npos = \"%d,%d\" ];\n", label, label, r, c);
            }
        }

        result.append("}");
        return result.toString();
    }

    public static void writeToFile(String filename, String text){
        try {
            FileWriter writer = new FileWriter(filename);
            writer.write(text);
            writer.flush();
            writer.close();
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args){
        int rows = 5;
        int cols = 5;

        IGraph g = makeGridGraph(rows, cols);

        writeToFile("files/grid.dot", generateGraphVizGridGraph(g, rows, cols, "grid1"));

        // r0c0 is always the top-left node
        // but we could make the start any node on the edges
        IGraph maze = createMaze(g, "r0c0");

        String graphviz = generateGraphVizGridGraph(maze, rows, cols, "maze1");

        writeToFile("files/maze.dot", graphviz);
    }

    public static void main2(String[] args){
        int rows = 5;
        int cols = 5;

        IGraph g = makeGridGraph(rows, cols);

        // This code generates a maze with the given number of rows and cols
        // using DFS, and prints it out in Graphviz format.
        System.out.printf("graph gr {\n");


        g.depthFirstSearch("r0c0", new NodeVisitor() {
            Stack<INode> nodes = new Stack<INode>();
            @Override
            public void visit(INode node) {
                // Go through the stack of nodes we've already seen,
                // and connect to it.
                for (INode n : nodes){
                    if (node.hasEdge(n)){
                        System.out.printf("%s -- %s;\n", n.getName(), node.getName());
                        break;
                    }
                }
                nodes.push(node);
            }
        });

        // This creates a bunch of "rank=same" directives that tell graphviz to lay out
        // the graph a certain way.
        for (int r=0; r<rows; r++){
            String rank = "";
            for (int c=0; c<cols; c++){
                String label = String.format("r%dc%d", r, c);
                //System.out.printf("%s [\n\tlabel = %s\npos = \"%d,%d\" ];\n", label, label, r, c);
                rank += label + ", ";
            }
            // remove last comma
            rank = rank.substring(0, rank.length()-2);
            System.out.printf("{ rank=same; %s }\n", rank);
        }

        System.out.printf("}\n");
    }

}
