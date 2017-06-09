package delaunay2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import datatypes.DelaunayGraph2D;
import datatypes.DelaunayInput;
import datatypes.Graph;
import datatypes.GraphEdge;
import datatypes.Point;

public class SimpleDelaunay2D implements Delaunay2D {
	static private final float EPSILON = 0.000001f;
	static private final int INSIDE = 0;
	static private final int COMPLETE = 1;
	static private final int INCOMPLETE = 2;

	private final Stack<Integer> quicksortStack = new Stack<Integer>();
	private final List<Integer> triangles = new ArrayList<Integer>();
	private final List<Integer> edges = new ArrayList<Integer>();
	private final List<Boolean> complete = new ArrayList<Boolean>();
	private final float[] superTriangle = new float[6];

	private DelaunayInput input;
	private Graph graph = null;
	private DelaunayGraph2D delaunayGraph = null;

	public SimpleDelaunay2D(final DelaunayInput input) {
		this.input = input;
	}

	public List<Integer> computeTriangles(final float[] points, final boolean sorted) {
		return computeTriangles(points, 0, points.length, sorted);
	}

	public List<Integer> computeTrianglesForPolygon(final float[] polygon, final boolean sorted) {
		return computeTriangles(polygon, 0, polygon.length, sorted);
	}

	/**
	 * Triangulates the given point cloud to a list of triangle indices that
	 * make up the Delaunay triangulation.
	 *
	 * @param points
	 *            x,y pairs describing points. Duplicate points will result in
	 *            undefined behavior.
	 * @param sorted
	 *            If false, the points will be sorted by the x coordinate, which
	 *            is required by the triangulation algorithm.
	 * @return triples of indexes into the points that describe the triangles in
	 *         clockwise order. Note the returned array is reused for later
	 *         calls to the same method.
	 */
	public List<Integer> computeTriangles(final float[] points, final int offset, final int count,
			final boolean sorted) {
		final int end = offset + count;

		if (!sorted) {
			quicksortPairs(points, offset, end - 1);
		}

		// Determine bounds for super triangle.
		float xmin = points[0], ymin = points[1];
		float xmax = xmin, ymax = ymin;
		for (int i = offset + 2; i < end; i++) {
			float value = points[i];
			if (value < xmin) {
				xmin = value;
			}
			if (value > xmax) {
				xmax = value;
			}
			i++;
			value = points[i];
			if (value < ymin) {
				ymin = value;
			}
			if (value > ymax) {
				ymax = value;
			}
		}
		final float dx = xmax - xmin, dy = ymax - ymin;
		final float dmax = (dx > dy ? dx : dy) * 20f;
		final float xmid = (xmax + xmin) / 2f, ymid = (ymax + ymin) / 2f;

		// Setup the super triangle, which contains all points.
		final float[] superTriangle = this.superTriangle;
		superTriangle[0] = xmid - dmax;
		superTriangle[1] = ymid - dmax;
		superTriangle[2] = xmid;
		superTriangle[3] = ymid + dmax;
		superTriangle[4] = xmid + dmax;
		superTriangle[5] = ymid - dmax;

		final List<Integer> edges = this.edges;
		final List<Boolean> complete = this.complete;
		final List<Integer> triangles = this.triangles;

		// Add super triangle.
		triangles.add(end);
		triangles.add(end + 2);
		triangles.add(end + 4);
		complete.add(false);

		// Include each point one at a time into the existing mesh.
		for (int pointIndex = offset; pointIndex < end; pointIndex += 2) {
			final float x = points[pointIndex], y = points[pointIndex + 1];

			// If x,y lies inside the circumcircle of a triangle, the edges are
			// stored and the triangle removed.
			final Integer[] trianglesArray = triangles.toArray(new Integer[triangles.size()]);
			final Boolean[] completeArray = complete.toArray(new Boolean[complete.size()]);
			for (int triangleIndex = triangles.size() - 1; triangleIndex >= 0; triangleIndex -= 3) {
				final int completeIndex = triangleIndex / 3;
				if (completeArray[completeIndex]) {
					continue;
				}
				final int p1 = trianglesArray[triangleIndex - 2];
				final int p2 = trianglesArray[triangleIndex - 1];
				final int p3 = trianglesArray[triangleIndex];
				float x1, y1, x2, y2, x3, y3;
				if (p1 >= end) {
					final int i = p1 - end;
					x1 = superTriangle[i];
					y1 = superTriangle[i + 1];
				} else {
					x1 = points[p1];
					y1 = points[p1 + 1];
				}
				if (p2 >= end) {
					final int i = p2 - end;
					x2 = superTriangle[i];
					y2 = superTriangle[i + 1];
				} else {
					x2 = points[p2];
					y2 = points[p2 + 1];
				}
				if (p3 >= end) {
					final int i = p3 - end;
					x3 = superTriangle[i];
					y3 = superTriangle[i + 1];
				} else {
					x3 = points[p3];
					y3 = points[p3 + 1];
				}
				switch (circumCircle(x, y, x1, y1, x2, y2, x3, y3)) {
				case COMPLETE:
					completeArray[completeIndex] = true;
					break;
				case INSIDE:
					edges.add(p1);
					edges.add(p2);
					edges.add(p2);
					edges.add(p3);
					edges.add(p3);
					edges.add(p1);

					triangles.remove(triangleIndex);
					triangles.remove(triangleIndex - 1);
					triangles.remove(triangleIndex - 2);
					complete.remove(completeIndex);
					break;
				}
			}

			final Integer[] edgesArray = edges.toArray(new Integer[edges.size()]);
			for (int i = 0, n = edges.size(); i < n; i += 2) {
				// Skip multiple edges. If all triangles are anticlockwise then
				// all interior edges are opposite pointing in direction.
				final int p1 = edgesArray[i];
				if (p1 == -1) {
					continue;
				}
				final int p2 = edgesArray[i + 1];
				boolean skip = false;
				for (int ii = i + 2; ii < n; ii += 2) {
					if (p1 == edgesArray[ii + 1] && p2 == edgesArray[ii]) {
						skip = true;
						edgesArray[ii] = -1;
					}
				}
				if (skip) {
					continue;
				}

				// Form new triangles for the current point. Edges are arranged
				// in clockwise order.
				triangles.add(p1);
				triangles.add(edgesArray[i + 1]);
				triangles.add(pointIndex);
				complete.add(false);
			}
			edges.clear();
		}
		complete.clear();

		// Remove triangles with super triangle vertices.
		final Integer[] trianglesArray = triangles.toArray(new Integer[triangles.size()]);
		for (int i = triangles.size() - 1; i >= 0; i -= 3) {
			if (trianglesArray[i] >= end || trianglesArray[i - 1] >= end || trianglesArray[i - 2] >= end) {
				triangles.remove(i);
				triangles.remove(i - 1);
				triangles.remove(i - 2);
			}
		}
		return triangles;
	}

	/**
	 * Returns INSIDE if point xp,yp is inside the circumcircle made up of the
	 * points x1,y1, x2,y2, x3,y3. Returns COMPLETE if xp is to the right of the
	 * entire circumcircle. Otherwise returns INCOMPLETE. Note: a point on the
	 * circumcircle edge is considered inside.
	 */
	private int circumCircle(final float xp, final float yp, final float x1, final float y1, final float x2,
			final float y2, final float x3, final float y3) {
		float xc, yc;
		final float y1y2 = Math.abs(y1 - y2);
		final float y2y3 = Math.abs(y2 - y3);
		if (y1y2 < EPSILON) {
			if (y2y3 < EPSILON) {
				return INCOMPLETE;
			}
			final float m2 = -(x3 - x2) / (y3 - y2);
			final float mx2 = (x2 + x3) / 2f;
			final float my2 = (y2 + y3) / 2f;
			xc = (x2 + x1) / 2f;
			yc = m2 * (xc - mx2) + my2;
		} else {
			final float m1 = -(x2 - x1) / (y2 - y1);
			final float mx1 = (x1 + x2) / 2f;
			final float my1 = (y1 + y2) / 2f;
			if (y2y3 < EPSILON) {
				xc = (x3 + x2) / 2f;
				yc = m1 * (xc - mx1) + my1;
			} else {
				final float m2 = -(x3 - x2) / (y3 - y2);
				final float mx2 = (x2 + x3) / 2f;
				final float my2 = (y2 + y3) / 2f;
				xc = (m1 * mx1 - m2 * mx2 + my2 - my1) / (m1 - m2);
				yc = m1 * (xc - mx1) + my1;
			}
		}

		float dx = x2 - xc;
		float dy = y2 - yc;
		final float rsqr = dx * dx + dy * dy;

		dx = xp - xc;
		dx *= dx;
		dy = yp - yc;
		if (dx + dy * dy - rsqr <= EPSILON) {
			return INSIDE;
		}
		return xp > xc && dx > rsqr ? COMPLETE : INCOMPLETE;
	}

	/**
	 * Sorts x,y pairs of values by the x value.
	 *
	 * @param lower
	 *            Start x index.
	 * @param upper
	 *            End x index.
	 */
	private void quicksortPairs(final float[] values, int lower, int upper) {
		final Stack<Integer> stack = quicksortStack;
		stack.add(lower);
		stack.add(upper - 1);
		while (stack.size() > 0) {
			upper = stack.pop();
			lower = stack.pop();
			if (upper <= lower) {
				continue;
			}
			final int i = quicksortPartition(values, lower, upper);
			if (i - lower > upper - i) {
				stack.add(lower);
				stack.add(i - 2);
			}
			stack.add(i + 2);
			stack.add(upper);
			if (upper - i >= i - lower) {
				stack.add(lower);
				stack.add(i - 2);
			}
		}
	}

	private int quicksortPartition(final float[] values, final int lower, final int upper) {
		final float value = values[lower];
		int up = upper;
		int down = lower;
		float temp;
		while (down < up) {
			while (values[down] <= value && down < up) {
				down = down + 2;
			}
			while (values[up] > value) {
				up = up - 2;
			}
			if (down < up) {
				temp = values[down];
				values[down] = values[up];
				values[up] = temp;

				temp = values[down + 1];
				values[down + 1] = values[up + 1];
				values[up + 1] = temp;
			}
		}
		values[lower] = values[up];
		values[up] = value;

		temp = values[lower + 1];
		values[lower + 1] = values[up + 1];
		values[up + 1] = temp;
		return up;
	}

	@Override
	public Graph getTriangulation() {
		if (graph == null) {
			final Point[] points = input.getPoints();
			final float[] pointsF = new float[points.length * 2];
			for (int i = 0; i < points.length; i++) {
				final Point p = points[i];
				pointsF[i * 2] = (float) p.getX();
				pointsF[i * 2 + 1] = (float) p.getY();
			}
			System.out.println(pointsF.length);
			final List<Integer> triangles = this.computeTriangles(pointsF, false);
			System.out.println(triangles.size());

			final List<GraphEdge> edges = new ArrayList<GraphEdge>(triangles.size());
			for (int i = 0; i < triangles.size() / 3; i++) {
				final Point p1 = points[triangles.get(3 * i)];
				final Point p2 = points[triangles.get(3 * i + 1)];
				final Point p3 = points[triangles.get(3 * i + 2)];
				edges.add(new GraphEdge(p1, p2));
				edges.add(new GraphEdge(p1, p3));
				edges.add(new GraphEdge(p2, p3));
			}
			graph = new Graph(points, edges);
		}
		return graph;
	}

	@Override
	public DelaunayGraph2D getDelaunay() {
		// TODO Auto-generated method stub
		return delaunayGraph;
	}
}
