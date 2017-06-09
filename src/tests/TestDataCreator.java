package tests;

import datatypes.DelaunayInput;

public class TestDataCreator {
	private static double[] point() {
		return new double[] { 1, 1, 1 };
	}

	private static double[] point3() {
		return new double[] { 1, 1, 1, 0, 0, 0, -1, 0, 1, 1, 0, 0 };
	}

	private static double[] range() {
		return new double[] { -1, 1 };
	}

	private static DelaunayInput fromData(final double[] data) {
		return new DelaunayInput(data, range(), range(), range());
	}

	public static DelaunayInput createOnePoint() {
		return fromData(point());
	}

	public static DelaunayInput create3Points() {
		return fromData(point3());
	}
}
