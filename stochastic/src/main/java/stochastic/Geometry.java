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

        Vector3D rayToSphere = sphereCenter.subtract(rayOrigin);
        if (rayToSphere.dotProduct(rayDir) < 0) {
            // ray inside the sphere
            double rayToSphereDistance = rayToSphere.getNorm();
            if (rayToSphereDistance > sphereRadius) {
                return null;
            } else if (rayToSphereDistance == sphereRadius) {
                return rayOrigin;
            } else {
                Vector3D centerProjectionOntoRayLine = projectOntoLine(sphereCenter, rayOrigin, rayDir);
                double projectionToCenterDistance = centerProjectionOntoRayLine.distance(sphereCenter);
                double projectionToIntersectionDistance = Math.sqrt(sphereRadius * sphereRadius - projectionToCenterDistance);
                double rayOriginToIntersectionDistance = projectionToIntersectionDistance - centerProjectionOntoRayLine.distance(rayOrigin);
                return rayOrigin.add(rayOriginToIntersectionDistance, rayDir);
            }
        } else {
            Vector3D centerProjectionOntoRayLine = projectOntoLine(sphereCenter, rayOrigin, rayDir);
            if (centerProjectionOntoRayLine.distance(sphereCenter) > sphereRadius) {
                return null;
            } else {
                double projectionToCenterDistance = centerProjectionOntoRayLine.distance(sphereCenter);
                double projectionToIntersectionDistance = Math.sqrt(sphereRadius * sphereRadius - projectionToCenterDistance);
                double rayOriginToIntersectionDistance = centerProjectionOntoRayLine.subtract(rayOrigin).getNorm();
                if (rayToSphere.getNorm() > sphereRadius) {
                    rayOriginToIntersectionDistance -= projectionToIntersectionDistance;
                } else {
                    rayOriginToIntersectionDistance += projectionToIntersectionDistance;
                }

                return rayOrigin.add(rayOriginToIntersectionDistance, rayDir);
            }
        }
    }
}
