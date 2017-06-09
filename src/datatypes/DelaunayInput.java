package datatypes;

public class DelaunayInput {
	private final double[] xRange;
	private final double[] yRange;
	private final double[] zRange;
	private final int pointsNo;
	private final Point[] points;

	public DelaunayInput(final double[] coords, final double[] xRange, final double[] yRange) {
		this.xRange = xRange;
		this.yRange = yRange;
		zRange = new double[] { 0, 0 };
		pointsNo = coords.length / 2;
		points = new Point[pointsNo];
		for (int i = 0; i < pointsNo; i++) {
			points[i] = new Point(coords[i * 2], coords[i * 2 + 1]);
		}
	}

	public DelaunayInput(final double[] coords, final double[] xRange, final double[] yRange, final double[] zRange) {
		this.xRange = xRange;
		this.yRange = yRange;
		this.zRange = zRange;
		pointsNo = coords.length / 3;
		points = new Point[pointsNo];
		for (int i = 0; i < pointsNo; i++) {
			points[i] = new Point(coords[i * 3], coords[i * 3 + 1], coords[i * 3 + 2]);
		}
	}

	public int getCount() {
		return pointsNo;
	}

	public Point[] getPoints() {
		return points;
	}

	public double[] getXRange() {
		return xRange;
	}

	public double[] getYRange() {
		return yRange;
	}

	public double[] getZRange() {
		return zRange;
	}
}
