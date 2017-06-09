package delaunay2D;

import datatypes.DelaunayGraph2D;
import datatypes.Graph;

public interface Delaunay2D {
	public Graph getTriangulation();

	public DelaunayGraph2D getDelaunay();
}
