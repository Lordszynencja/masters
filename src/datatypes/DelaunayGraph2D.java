package datatypes;

public class DelaunayGraph2D {
	private DelaunayArea[] areas;

	public DelaunayGraph2D(final DelaunayArea[] areas) {
		this.areas = areas;
	}

	public DelaunayArea[] getAreas() {
		return areas;
	}
}
