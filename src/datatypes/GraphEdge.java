package datatypes;

public class GraphEdge {
	private Point p1;
	private Point p2;

	public GraphEdge(final Point p1, final Point p2) {
		this.p1 = p1;
		this.p2 = p2;
	}

	public Point[] getPoints() {
		return new Point[] { p1, p2 };
	}

	@Override
	public String toString() {
		return p1.toString() + "-" + p2.toString();
	}
}
