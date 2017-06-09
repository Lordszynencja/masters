package tests;

import java.util.List;

import org.junit.Test;

import datatypes.DelaunayInput;
import datatypes.Graph;
import datatypes.GraphEdge;
import delaunay2D.Delaunay2D;
import delaunay2D.SimpleDelaunay2D;

public class SimpleDelaunay2DTest {
	private void testFor(final DelaunayInput input) {
		final Delaunay2D d = new SimpleDelaunay2D(TestDataCreator.createOnePoint());
		final Graph g = d.getTriangulation();
		final List<GraphEdge> edges = g.getEdges();
		if (edges.size() > 0) {
			System.out.println("edges:");
			for (final GraphEdge edge : edges) {
				System.out.println(edge.toString());
			}
		} else {
			System.out.println("no edges");
		}
	}

	@Test
	public void simpleCaseTest() {
		testFor(TestDataCreator.createOnePoint());
	}

	@Test
	public void simpleCaseTest2() {
		testFor(TestDataCreator.create3Points());
	}
}
