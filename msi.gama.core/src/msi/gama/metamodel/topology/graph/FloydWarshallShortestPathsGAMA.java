package msi.gama.metamodel.topology.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import msi.gama.util.graph.GamaGraph;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.GraphPathImpl;
import org.jgrapht.util.VertexPair;


//Copy of the jgrapht algorithm: just make it usable with GAMA undirected graph
public class FloydWarshallShortestPathsGAMA<V, E> {


	   //~ Instance fields --------------------------------------------------------

    private Graph<V, E> graph;
    private List<V> vertices;
    private int nShortestPaths = 0;
    private double diameter = 0.0;
    private double [][] d = null;
    private int [][] backtrace = null;
    private Map<VertexPair<V>, GraphPath<V, E>> paths = null;

    //~ Constructors -----------------------------------------------------------

    public FloydWarshallShortestPathsGAMA(Graph<V, E> graph)
    {
        this.graph = graph;
        this.vertices = new ArrayList<V>(graph.vertexSet());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * @return the graph on which this algorithm operates
     */
    public Graph<V, E> getGraph()
    {
        return graph;
    }

    /**
     * @return total number of shortest paths
     */
    public int getShortestPathsCount()
    {
        lazyCalculatePaths();
        return nShortestPaths;
    }

    /**
     * Calculates the matrix of all shortest paths, along with the diameter, but
     * does not populate the paths map.
     */
    private void lazyCalculateMatrix()
    {
        if (d != null) {
            // already done
            return;
        }

        int n = vertices.size();

        // init the backtrace matrix
        backtrace = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(backtrace[i], -1);
        }

        // initialize matrix, 0
        d = new double[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(d[i], Double.POSITIVE_INFINITY);
        }

        // initialize matrix, 1
        for (int i = 0; i < n; i++) {
            d[i][i] = 0.0;
        }

        // initialize matrix, 2
        Set<E> edges = graph.edgeSet();
        for (E edge : edges) {
            V v1 = graph.getEdgeSource(edge);
            V v2 = graph.getEdgeTarget(edge);

            int v_1 = vertices.indexOf(v1);
            int v_2 = vertices.indexOf(v2);

            d[v_1][v_2] = graph.getEdgeWeight(edge);
            if (!((GamaGraph<V, E>) graph).isDirected()) {
                d[v_2][v_1] = graph.getEdgeWeight(edge);
            }
        }

        // run fw alg
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    double ik_kj = d[i][k] + d[k][j];
                    if (ik_kj < d[i][j]) {
                        d[i][j] = ik_kj;
                        backtrace[i][j] = k;
                        diameter = (diameter > d[i][j]) ? diameter : d[i][j];
                    }
                }
            }
        }
    }

    /**
     * Get the length of a shortest path.
     *
     * @param a first vertex
     * @param b second vertex
     *
     * @return shortest distance between a and b
     */
    public double shortestDistance(V a, V b)
    {
        lazyCalculateMatrix();

        return d[vertices.indexOf(a)][vertices.indexOf(b)];
    }

    /**
     * @return the diameter (longest of all the shortest paths) computed for the
     * graph
     */
    public double getDiameter()
    {
        lazyCalculateMatrix();
        return diameter;
    }

    private void shortestPathRecur(List<E> edges, int v_a, int v_b)
    {
        int k = backtrace[v_a][v_b];
        if (k == -1) {
            E edge = graph.getEdge(vertices.get(v_a), vertices.get(v_b));
            if (edge != null) {
                edges.add(edge);
            }
        } else {
            shortestPathRecur(edges, v_a, k);
            shortestPathRecur(edges, k, v_b);
        }
    }

    /**
     * Get the shortest path between two vertices. Note: The paths are
     * calculated using a recursive algorithm. It *will* give problems on paths
     * longer than the stack allows.
     *
     * @param a From vertice
     * @param b To vertice
     *
     * @return the path, or null if none found
     */
    public GraphPath<V, E> getShortestPath(V a, V b)
    {
        lazyCalculatePaths();
        return getShortestPathImpl(a, b);
    }

    private GraphPath<V, E> getShortestPathImpl(V a, V b)
    {
        int v_a = vertices.indexOf(a);
        int v_b = vertices.indexOf(b);

        List<E> edges = new ArrayList<E>();
        shortestPathRecur(edges, v_a, v_b);

        // no path, return null
        if (edges.size() < 1) {
            return null;
        }

        GraphPathImpl<V, E> path =
            new GraphPathImpl<V, E>(graph, a, b, edges, edges.size());

        return path;
    }

    /**
     * Calculate the shortest paths (not done per default)
     */
    private void lazyCalculatePaths()
    {
        // already we have calculated it once.
        if (paths != null) {
            return;
        }

        lazyCalculateMatrix();

        Map<VertexPair<V>, GraphPath<V, E>> sps =
            new HashMap<VertexPair<V>, GraphPath<V, E>>();
        int n = vertices.size();

        nShortestPaths = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // don't count this.
                if (i == j) {
                    continue;
                }

                V v_i = vertices.get(i);
                V v_j = vertices.get(j);

                GraphPath<V, E> path = getShortestPathImpl(v_i, v_j);

                // we got a path
                if (path != null) {
                    sps.put(new VertexPair<V>(v_i, v_j), path);
                    nShortestPaths++;
                }
            }
        }

        this.paths = sps;
    }

    /**
     * Get shortest paths from a vertex to all other vertices in the graph.
     *
     * @param v the originating vertex
     *
     * @return List of paths
     */
    public List<GraphPath<V, E>> getShortestPaths(V v)
    {
        lazyCalculatePaths();
        List<GraphPath<V, E>> found = new ArrayList<GraphPath<V, E>>();
        for (VertexPair<V> pair : paths.keySet()) {
            if (pair.hasVertex(v)) {
                found.add(paths.get(pair));
            }
        }

        return found;
    }
}

// End FloydWarshallShortestPaths.java