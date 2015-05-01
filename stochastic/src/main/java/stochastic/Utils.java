package stochastic;

import java.util.Random;

/**
 * Created by dex on 01.05.15.
 */
public class Utils {
    public static final double EPSILON = 1e-5;

    public static boolean almostEqual(double a, double b) {
        return Math.abs(a - b) < EPSILON;
    }

    public static double nextScaledFloat(Random random,
                                         Range<Double> range)
    {
        return range.getMin() + (range.getMax() - range.getMin()) * random.nextFloat();
    }

    public static Angle nextScaledAngle(Random random,
                                        Range<Angle> range)
    {
        return Angle.fromRadians(nextScaledFloat(random, new Range<Double>(range.getMin().getRadians(),
                                                                           range.getMax().getRadians())));
    }
}
