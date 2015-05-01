package stochastic;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LensSimulator {
    private static Random random = new Random();

    static class LensConfigurationSettings
    {
        Range<Double> x;
        Range<Double> y;
        Range<Double> z;
        Range<Double> curvatureRadius;
        Range<Double> height;
    }

    public static boolean isZUnique(List<Lens> lenses,
                                    double z) {
        for (Lens lens : lenses) {
            if (Utils.almostEqual(lens.getCenter().getZ(), z)) {
                return false;
            }
        }

        return true;
    }

    public static List<Lens> generateRandomLensConfiguration(int numLenses,
                                                             LensConfigurationSettings settings) {
        List<Lens> lenses = new ArrayList<>();

        while (numLenses-- > 0) {
            double radius = Utils.nextScaledFloat(random, settings.curvatureRadius);
            double height = Utils.nextScaledFloat(random, settings.height);
            double x = Utils.nextScaledFloat(random, settings.x);
            double y = Utils.nextScaledFloat(random, settings.y);
            double z;

            do {
                z = Utils.nextScaledFloat(random, settings.z);
            } while (!isZUnique(lenses, z));

            lenses.add(new Lens(new Vector3D(x, y, z), radius, height));
        }

        return lenses;
    }

	public static void main(String[] args) {
        final int NUM_LENSES = 0;
        final Vector3D LIGHT_SOURCE = new Vector3D(0.0, 0.0, 100.0);
        final int MAX_RAYS = 10000;
        final Range<Angle> PHI_RADIANS = new Range<>(Angle.fromRadians(1.4999 * Math.PI),
                                                     Angle.fromRadians(1.5001 * Math.PI));
        final Range<Angle> THETA_RADIANS = new Range<>(Angle.fromRadians(-0.0001 * Math.PI),
                                                       Angle.fromRadians(0.0001 * Math.PI));
        final Range<PhiTheta> SCREEN_ANGLE_RANGE = new Range<>(new PhiTheta(Angle.fromRadians(0.25 * Math.PI),
                                                                            Angle.fromRadians(-0.25 * Math.PI)),
                                                               new PhiTheta(Angle.fromRadians(0.75 * Math.PI),
                                                                            Angle.fromRadians(0.25 * Math.PI)));
        final double SCREEN_SPHERE_RADIUS = 10.0;

        Screen screen = new Screen(SCREEN_ANGLE_RANGE, SCREEN_SPHERE_RADIUS, 800, 600);

        LensConfigurationSettings settings = new LensConfigurationSettings();
        settings.x = new Range<>(-10.0, 10.0);
        settings.y = new Range<>(-10.0, 10.0);
        settings.z = new Range<>(1.0, 50.0);
        settings.curvatureRadius = new Range<>(50.0, LIGHT_SOURCE.getZ());
        settings.height = new Range<>(1.0, 10.0);

        System.err.println("generating lens simulation");
        LensSimulation sim = new LensSimulation(generateRandomLensConfiguration(NUM_LENSES, settings));

        List<Vector3D> imagePositionsOnScreen = new ArrayList<>();

        for (int i = 0; i < MAX_RAYS; i++) {
            Angle phi = Utils.nextScaledAngle(random, PHI_RADIANS);
            Angle theta = Utils.nextScaledAngle(random, THETA_RADIANS);
            phi = Angle.fromRadians(1.5 * Math.PI);
            theta = Angle.fromRadians(0.0);
            Vector3D rayDir = new PhiTheta(phi, theta).toPosition();

            Ray input = new Ray(LIGHT_SOURCE, rayDir);
            Ray output = sim.simulate(input);

            System.err.println("input " + input);
            System.err.println("output " + output);

            PhiTheta screenRayCollisionPosAngles = screen.getRayCollisionPosAngles(output);
            if (screenRayCollisionPosAngles != null) {
                imagePositionsOnScreen.add(screenRayCollisionPosAngles.toPosition());
            }
        }

        for (Vector3D pos : imagePositionsOnScreen) {
            System.out.println("hit: " + pos);
        }
    }
}