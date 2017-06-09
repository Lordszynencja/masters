package datatypes;

import java.util.List;

public class Graph {
	Point[] points;
	List<GraphEdge> edges;

	public Graph(final Point[] points, final List<GraphEdge> edges) {
		this.points = points;
		this.edges = edges;
	}

	public Point[] getPoints() {
		return points;
	}

	public List<GraphEdge> getEdges() {
		return edges;
	}
}
