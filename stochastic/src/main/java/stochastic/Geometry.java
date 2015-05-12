package stochastic;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * Created by dex on 01.05.15.
 */
public class Geometry {
    public static Vector3D projectOntoLine(Vector3D point,
                                           Vector3D lineA,
                                           Vector3D lineB) {
        Vector3D ap = point.subtract(lineA);
        Vector3D ab = lineB.subtract(lineA);
        return lineA.add(ab.scalarMultiply(ap.dotProduct(ab) / ab.dotProduct(ab)));
    }

    public static Vector3D raySphereIntersection(Ray ray,
                                                 Vector3D sphereCenter,
                                                 double sphereRadius) {
        assert Utils.almostEqual(ray.dir.getNorm(), 1.0);

        Vector3D rayOrigin = ray.getOrigin();
        Vector3D rayDir = ray.getDir();

        Vector3D sphereToRay = rayOrigin.subtract(sphereCenter);
        double a = rayDir.dotProduct(rayDir);
        double b = rayDir.scalarMultiply(2.0).dotProduct(sphereToRay);
        double c = sphereToRay.dotProduct(sphereToRay) - sphereRadius * sphereRadius;

        double det = b * b - 4.0 * a * c;
        if (det < 0.0) {
            return null;
        } else {
            double t0 = (-b - Math.sqrt(det)) / 2.0;
            double t1 = (-b + Math.sqrt(det)) / 2.0;

            double t = Math.min(t0, t1);
            if (t < 0.0) {
                t = Math.max(t0, t1);
                if (t < 0.0) {
                    return null;
                }
            }

            return rayOrigin.add(t, rayDir);
        }
    }

    public static Vector3D refract(Vector3D rayDir,
                                   Vector3D normal,
                                   double srcRefractiveIndex,
                                   double dstRefractiveIndex) {
        assert Utils.almostEqual(normal.getNorm(), 1.0);

        double refractiveIndexRatio = srcRefractiveIndex / dstRefractiveIndex;
        double cosSrcAngleToNormal = rayDir.normalize().negate().dotProduct(normal);
        double sinSrcAngleToNormal = Math.sqrt(1.0 - cosSrcAngleToNormal * cosSrcAngleToNormal);

        if (sinSrcAngleToNormal > dstRefractiveIndex / srcRefractiveIndex) {
            // total internal reflection
            System.err.println("refractOnSphereSurface: total internal reflection");
            return null;
        }

        double sinDstAngleToNormalSq = refractiveIndexRatio * refractiveIndexRatio * sinSrcAngleToNormal * sinSrcAngleToNormal;

        return rayDir.scalarMultiply(refractiveIndexRatio)
                     .add(refractiveIndexRatio * cosSrcAngleToNormal - Math.sqrt(1.0 - sinDstAngleToNormalSq), normal);
    }
}
