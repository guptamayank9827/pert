package PERTPACKAGE;

import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;

import PERTPACKAGE.Graph.*;

public class DFS extends Graph.GraphAlgorithm<DFS.DFSVertex> {

    public static final int INFINITY = Integer.MAX_VALUE;
    LinkedList<Vertex> postOrderList = new LinkedList<>();
    Vertex src;

    enum Status{ NEW, ACTIVE, FINISHED }

    // Class to store information about vertices during DFS
    public static class DFSVertex implements Factory {
        Status status;
        Vertex parent;

        public DFSVertex(Vertex v) {
            status = Status.NEW;
            parent = null;
        }

        public DFSVertex make(Vertex v) { return new DFSVertex(v); }
    }

    // code to initialize storage for vertex properties is in GraphAlgorithm class
    public DFS(Graph g) {
        super(g, new DFSVertex(null));
    }

    public void initializeDFS(Graph g) {
        for(Vertex vertex : g){
            get(vertex).status = Status.NEW;
            get(vertex).parent = null;
        }
    }

    public boolean isDAG(Vertex src) {
        this.src = src;
        get(src).status = Status.ACTIVE;

        for (Edge edge : g.outEdges(src)) {
            Vertex adjacentVertex = edge.otherEnd(src);
            if (get(adjacentVertex).status == Status.ACTIVE) {
                return false;
            }
            else if (get(adjacentVertex).status == Status.NEW) {
                if (!isDAG(adjacentVertex)) return false;
            } 
        }

        get(src).status = Status.FINISHED;
        return true;
    }

    public boolean isDAGAll(Graph graph) {
        initializeDFS(graph);

        for(Vertex vertex: graph) {
            if(get(vertex).status == Status.NEW) {
                if(!isDAG(vertex))  return false;
            }
        }

        return true;
    }

    public void dfs(Vertex src) {
        this.src = src;
        get(src).status = Status.ACTIVE;

        for (Edge edge : g.outEdges(src)) {
            Vertex v = edge.otherEnd(src);
            if (get(v).status == Status.NEW) {
                get(v).parent = src;
                dfs(v);
            } 
        }

        get(src).status = Status.FINISHED;
        postOrderList.add(src); 
    }

    public void dfsAll(Graph graph){
        initializeDFS(graph);

        for(Vertex vertex: graph)
            if(get(vertex).status == Status.NEW) dfs(vertex);
    }


    // Run depth-first search algorithm on g from source src
    public static DFS depthFirstSearch(Graph graph, Vertex src) {
        DFS dfs = new DFS(graph);
        boolean isDAG = dfs.isDAGAll(graph);

        if(isDAG)  dfs.dfsAll(graph);

        return dfs;
    }
    
    public static DFS depthFirstSearch(Graph graph, int s) {
        return depthFirstSearch(graph, graph.getVertex(s));
    }
    
    public static void main(String[] args) throws Exception {
		String string = "8 11   1 2 1   1 3 1   2 4 1   3 5 1   3 6 1   4 6 1   4 7 1   5 7 1   5 8 1   6 8 1   7 8 1   1";

        Scanner in;
        // If there is a command line argument, use it as file from which
        // input is read, otherwise use input from string.
        in = args.length > 0 ? new Scanner(new File(args[0])) : new Scanner(string);
        // Read graph from input
        Graph graph = Graph.readDirectedGraph(in);
        // last number specifies source s
        int src = in.nextInt();

        // Create an instance of DFS and run dfs from source s
        depthFirstSearch(graph, src);

        graph.printGraph(false);
    }
}