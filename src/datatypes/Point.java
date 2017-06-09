package datatypes;

public class Point {
	double x;
	double y;
	double z;

	public Point() {
		x = 0;
		y = 0;
		z = 0;
	}

	public Point(final double x) {
		this.x = x;
		y = 0;
		z = 0;
	}

	public Point(final double x, final double y) {
		this.x = x;
		this.y = y;
		z = 0;
	}

	public Point(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public double distanceTo(final Point p) {
		final double dx = x - p.x;
		final double dy = y - p.y;
		final double dz = z - p.z;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	@Override
	public String toString() {
		return new StringBuilder("(").append(x).append(",").append(y).append(",").append(z).append(")").toString();
	}
}
