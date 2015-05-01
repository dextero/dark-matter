package stochastic;

/**
 * Created by dex on 01.05.15.
 */
public class Angle implements Comparable<Angle> {
    private double radians;

    private Angle(double radians) {
        this.radians = radians;
    }

    private static double radiansToDegrees(double radians) {
        return 180.0 * radians / Math.PI;
    }

    private static double degreesToRadians(double degrees) {
        return Math.PI * degrees / 180.0;
    }

    public static Angle fromRadians(double radians) {
        return new Angle(radians);
    }

    public static Angle fromDegrees(double degrees) {
        return new Angle(degreesToRadians(degrees));
    }

    public double getRadians() {
        return radians;
    }

    public double getDegrees() {
        return radiansToDegrees(radians);
    }

    @Override
    public int compareTo(Angle o) {
        return (int) Math.signum(radians - o.radians);
    }

    @Override
    public String toString() {
        return radians + " rad (" + getDegrees() + " deg)";
    }
}
