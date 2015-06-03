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
        double b = 2.0 * rayDir.dotProduct(sphereToRay);
        double c = sphereToRay.dotProduct(sphereToRay) - sphereRadius * sphereRadius;

        double det = b * b - 4.0 * a * c;
        if (det < 0.0) {
//            System.err.printf("det = %f\n", det);
//            System.err.println("ray = " + ray + ", sphereCenter = " + sphereCenter + ", sphereRadius = " + sphereRadius);
            return null;
        } else {
            double t0 = (-b - Math.sqrt(det)) / 2.0 / a;
            double t1 = (-b + Math.sqrt(det)) / 2.0 / a;

            double t = Math.min(t0, t1);
            if (t < 0.0) {
                t = Math.max(t0, t1);
                if (t < 0.0) {
//                    System.err.printf("t0 = %f, t1 = %f\n", t0, t1);
//                    System.err.println("ray = " + ray + ", sphereCenter = " + sphereCenter + ", sphereRadius = " + sphereRadius);
                    return null;
                }
            }

            return rayOrigin.add(t, rayDir);
        }
    }

    public static Vector3D reflect(Vector3D rayDir,
                                   Vector3D normal) {
        return rayDir.subtract(normal.scalarMultiply(2.0 * rayDir.dotProduct(normal)));
    }

    public static Vector3D refract(Vector3D rayDir,
                                   Vector3D normal,
                                   double srcRefractiveIndex,
                                   double dstRefractiveIndex) {
        assert Utils.almostEqual(normal.getNorm(), 1.0);
        assert Utils.almostEqual(rayDir.getNorm(), 1.0);

        double refractiveIndexRatio = srcRefractiveIndex / dstRefractiveIndex;
        double refractiveIndexRatioSq = refractiveIndexRatio * refractiveIndexRatio;
        double cosSrcAngleToNormal = rayDir.negate().dotProduct(normal);
        double sinSrcAngleToNormalSq = 1.0 - cosSrcAngleToNormal * cosSrcAngleToNormal;

        if (sinSrcAngleToNormalSq > 1.0 / refractiveIndexRatioSq) {
            // total internal reflection
//            System.err.println("refractOnSphereSurface: total internal reflection");
//            System.err.println("rayDir = " + rayDir + ", normal = " + normal + " srcRefractiveIndex = " + srcRefractiveIndex + ", dstRefractiveIndex = " + dstRefractiveIndex);
            return null;
        }

        double sinDstAngleToNormalSq = refractiveIndexRatioSq * sinSrcAngleToNormalSq;

        return rayDir.scalarMultiply(refractiveIndexRatio)
                     .add(refractiveIndexRatio * cosSrcAngleToNormal - Math.sqrt(1.0 - sinDstAngleToNormalSq), normal);
    }

    public static double pointRayDistance(Ray ray,
                                          Vector3D point) {
        Vector3D a = ray.getOrigin();
        Vector3D b = ray.getDir().add(ray.getOrigin());
        Vector3D aToB = b.subtract(a);

        return aToB.crossProduct(a.subtract(point)).getNorm() / aToB.getNorm();
    }
}
