import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.junit.ComparisonFailure;
import org.junit.Test;
import stochastic.Geometry;
import stochastic.Ray;
import stochastic.Utils;

import static org.junit.Assert.*;

/**
 * Created by dex on 02.05.15.
 */
public class GeometryTest {
    private static void assertVectorEquals(Vector3D expected, Vector3D actual) {
        if (!Utils.almostEqual(expected.getX(), actual.getX())
                || !Utils.almostEqual(expected.getY(), actual.getY())
                || !Utils.almostEqual(expected.getZ(), actual.getZ())) {
            throw new ComparisonFailure("Vectors not equal", expected.toString(), actual.toString());
        }
    }

    @Test
    public void testProjectOntoLine() throws Exception {
        assertVectorEquals(new Vector3D(1.0, 0.0, 0.0), Geometry.projectOntoLine(new Vector3D(1.0, 0.0, 0.0),
                                                                                 new Vector3D(0.0, 0.0, 0.0),
                                                                                 new Vector3D(2.0, 0.0, 0.0)));
        assertVectorEquals(new Vector3D(0.0, 1.0, 0.0), Geometry.projectOntoLine(new Vector3D(0.0, 1.0, 0.0),
                                                                                 new Vector3D(0.0, 0.0, 0.0),
                                                                                 new Vector3D(0.0, 2.0, 0.0)));
        assertVectorEquals(new Vector3D(0.0, 0.0, 1.0), Geometry.projectOntoLine(new Vector3D(0.0, 0.0, 1.0),
                                                                                 new Vector3D(0.0, 0.0, 0.0),
                                                                                 new Vector3D(0.0, 0.0, 2.0)));

        assertVectorEquals(new Vector3D(1.0, 1.0, 0.0), Geometry.projectOntoLine(new Vector3D(2.0, 0.0, 0.0),
                                                                                 new Vector3D(0.0, 0.0, 0.0),
                                                                                 new Vector3D(2.0, 2.0, 0.0)));
        assertVectorEquals(new Vector3D(1.0, 0.0, 1.0), Geometry.projectOntoLine(new Vector3D(2.0, 0.0, 0.0),
                                                                                 new Vector3D(0.0, 0.0, 0.0),
                                                                                 new Vector3D(2.0, 0.0, 2.0)));
        assertVectorEquals(new Vector3D(0.0, 1.0, 1.0), Geometry.projectOntoLine(new Vector3D(0.0, 2.0, 0.0),
                                                                                 new Vector3D(0.0, 0.0, 0.0),
                                                                                 new Vector3D(0.0, 2.0, 2.0)));

        assertVectorEquals(new Vector3D(1.0, 1.0, 1.0), Geometry.projectOntoLine(new Vector3D(0.0, 3.0, 0.0),
                                                                                 new Vector3D(0.0, 0.0, 0.0),
                                                                                 new Vector3D(3.0, 3.0, 3.0)));
    }

    @Test
    public void testRaySphereIntersection() throws Exception {
        // ray along axis
        assertVectorEquals(new Vector3D(1.0, 0.0, 0.0),
                           Geometry.raySphereIntersection(new Ray(new Vector3D(2.0, 0.0, 0.0),
                                                                  new Vector3D(-1.0, 0.0, 0.0)),
                                                          Vector3D.ZERO, 1.0));
        assertVectorEquals(new Vector3D(0.0, 1.0, 0.0),
                           Geometry.raySphereIntersection(new Ray(new Vector3D(0.0, 2.0, 0.0),
                                                                  new Vector3D(0.0, -1.0, 0.0)),
                                                          Vector3D.ZERO, 1.0));
        assertVectorEquals(new Vector3D(0.0, 0.0, 1.0),
                           Geometry.raySphereIntersection(new Ray(new Vector3D(0.0, 0.0, 2.0),
                                                                  new Vector3D(0.0, 0.0, -1.0)),
                                                          Vector3D.ZERO, 1.0));

        // ray along plane
        assertVectorEquals(new Vector3D(Math.sqrt(2.0) / 2.0, Math.sqrt(2.0) / 2.0, 0.0),
                           Geometry.raySphereIntersection(new Ray(new Vector3D(2.0, 2.0, 0.0),
                                                                  new Vector3D(-1.0, -1.0, 0.0).normalize()),
                                                          Vector3D.ZERO, 1.0));
        assertVectorEquals(new Vector3D(Math.sqrt(2.0) / 2.0, 0.0, Math.sqrt(2.0) / 2.0),
                           Geometry.raySphereIntersection(new Ray(new Vector3D(2.0, 0.0, 2.0),
                                                                  new Vector3D(-1.0, 0.0, -1.0).normalize()),
                                                          Vector3D.ZERO, 1.0));
        assertVectorEquals(new Vector3D(0.0, Math.sqrt(2.0) / 2.0, Math.sqrt(2.0) / 2.0),
                           Geometry.raySphereIntersection(new Ray(new Vector3D(0.0, 2.0, 2.0),
                                                                  new Vector3D(0.0, -1.0, -1.0).normalize()),
                                                          Vector3D.ZERO, 1.0));

        // 3d ray
        assertVectorEquals(new Vector3D(Math.sqrt(3.0) / 3.0, Math.sqrt(3.0) / 3.0, Math.sqrt(3.0) / 3.0),
                           Geometry.raySphereIntersection(new Ray(new Vector3D(1.0, 1.0, 1.0),
                                                                  new Vector3D(-1.0, -1.0, -1.0).normalize()),
                                                          Vector3D.ZERO, 1.0));

        // ray origin inside sphere
        assertVectorEquals(new Vector3D(1.0, 0.0, 0.0),
                           Geometry.raySphereIntersection(new Ray(Vector3D.ZERO, new Vector3D(1.0, 0.0, 0.0)),
                                                          Vector3D.ZERO, 1.0));

        // ray shooting away from the sphere
        assertNull(Geometry.raySphereIntersection(new Ray(new Vector3D(2.0, 0.0, 0.0), new Vector3D(1.0, 1.0, 1.0)),
                                                  Vector3D.ZERO, 1.0));

        // ray completely missing the sphere
        assertNull(Geometry.raySphereIntersection(new Ray(new Vector3D(0.0, 2.0, 0.0), new Vector3D(1.0, 1.0, 1.0)),
                                                  Vector3D.ZERO, 1.0));
    }
}