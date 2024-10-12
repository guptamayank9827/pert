package src;

import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;

import src.Graph.*;

public class PERT extends GraphAlgorithm<PERT.PERTVertex> {
    LinkedList<Vertex> finishList;
	
    public static class PERTVertex implements Factory {
		int es;	//earliest start
		int ef;	//earliest finish
		int ls;	//latest start
		int lf;	//latest finish
		int duration;
		int slack;
		
		public PERTVertex(Vertex u) {
			es = 0;
			ef = 0;
			ls = 0;
			lf = 0;
			duration = 0;
			slack = 0;
		}

		public PERTVertex make(Vertex u) { return new PERTVertex(u); }
    }

    // Constructor for PERT is private. Create PERT instances with static method pert().
    private PERT(Graph g) {
		super(g, new PERTVertex(null));
    }

    public void setDuration(Vertex u, int d) {
		get(u).duration = d;
	}

	// Implement the PERT algorithm. Returns false if the graph g is not a DAG.
	public boolean pert(Graph g) {

		// Check for DAG
		DFS dfs = new DFS(g);
		if(!dfs.isDAGAll(g)) return false;

		finishList = topologicalOrder();

		//initialize ES
		for (Vertex vertex : finishList)
			get(vertex).es = 0;

		for (Vertex u : finishList) {
			get(u).ef = get(u).es + get(u).duration;

			for (Edge e : g.outEdges(u)) {
				Vertex v = e.otherEnd(u);
				if(get(v).es < get(u).ef)	get(v).es = get(u).ef;	//maximize successor's ES
			}
		}

		int criticalPathTime = criticalPath();

		//initialize LF
		for (Vertex vertex : finishList)
			get(vertex).lf = criticalPathTime;

		for (Vertex u : finishList.reversed()) {

			get(u).ls = get(u).lf - get(u).duration;
			get(u).slack = get(u).lf - get(u).ef;

			for (Edge e : g.inEdges(u)) {
				Vertex v = e.otherEnd(u);
				if(get(v).lf > get(u).ls)	get(v).lf = get(u).ls;	//minimize predecessor's LF
			}
		}

		return true;
	}

    // Find a topological order of g using DFS
    public LinkedList<Vertex> topologicalOrder() {
		DFS dfs = new DFS(g);
		dfs.dfsAll(g);

		return dfs.postOrderList.reversed();
    }


    // The following methods are called after calling pert().

    // Earliest time at which task u can be completed
    public int ec(Vertex u) {
		return get(u).ef;
    }

    // Latest completion time of u
    public int lc(Vertex u) {
		return get(u).lf;
    }

    // Slack of u
    public int slack(Vertex u) {
		return get(u).slack;
    }

    // Length of a critical path (time taken to complete project)
    public int criticalPath() {
		int criticalPathTime = 0;

		for (Vertex vertex : finishList)
			if(get(vertex).ef > criticalPathTime)	criticalPathTime = get(vertex).ef;

		return criticalPathTime;
    }

    // Is u a critical vertex?
    public boolean critical(Vertex u) {
		//processes with 0 slack are critical
		return get(u).slack == 0 ? true : false;
    }

    // Number of critical vertices of g
    public int numCritical() {
		int criticalCount = 0;

		//processes with 0 slack are critical
		for (Vertex vertex : finishList)
			if(critical(vertex))	criticalCount++;

		return criticalCount;
    }

    /* Create a PERT instance on g, runs the algorithm.
     * Returns PERT instance if successful. Returns null if G is not a DAG.
    */
    public static PERT pert(Graph g, int[] duration) {
		PERT pert = new PERT(g);
		
		for(Vertex u: g)
			pert.setDuration(u, duration[u.getIndex()]);

		if(pert.pert(g))	return pert;

		return null;
    }

    public static void main(String[] args) throws Exception {
		String graph = "10 13   1 2 1   2 4 1   2 5 1   3 5 1   3 6 1   4 7 1   5 7 1   5 8 1   6 8 1   6 9 1   7 10 1   8 10 1   9 10 1      0 3 2 3 2 1 3 2 4 1";
		Scanner in;
		// If there is a command line argument, use it as file from which
		// input is read, otherwise use input from string.
		in = args.length > 0 ? new Scanner(new File(args[0])) : new Scanner(graph);
		Graph g = Graph.readDirectedGraph(in);
		g.printGraph(false);

		int[] duration = new int[g.size()];
		for(int i=0; i<g.size(); i++) {
			duration[i] = in.nextInt();
		}
		PERT p = pert(g, duration);
		if(p == null) {
			System.out.println("Invalid graph: not a DAG");
		}
		else {
			System.out.println("Number of critical vertices: " + p.numCritical());
			System.out.println("u\tEC\tLC\tSlack\tCritical");
			for(Vertex u: g) {
				System.out.println(u + "\t" + p.ec(u) + "\t" + p.lc(u) + "\t" + p.slack(u) + "\t" + p.critical(u));
			}
		}
    }
}