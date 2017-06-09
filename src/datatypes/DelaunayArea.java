package datatypes;

public class DelaunayArea {
	private final Point center;
	private final Point[] vertices;

	public DelaunayArea(final Point center, final Point[] vertices) {
		this.center = center;
		this.vertices = vertices;
	}

	public static DelaunayArea fromConnected(final DelaunayInput input, final Point center, final Point[] connected) {
		if (connected.length == 0) {
			return new DelaunayArea(center, null);
		}
		return new DelaunayArea(center, null);
	}

	public Point getCenter() {
		return center;
	}

	public Point[] getVertices() {
		return vertices;
	}
}