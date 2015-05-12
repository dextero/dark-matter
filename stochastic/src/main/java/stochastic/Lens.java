package stochastic;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import stochastic.visualizer.SimulationVisualizer;

public class Lens {
	private Vector3D center;
	/**
	 * Radius of the sphere the lens shares surface with
	 */
	public final double radius;
	public final double height;
    private Ray intermediateRay;

    public Lens(Vector3D center, double radius, double height) {
        this.center = center;
        this.radius = radius;
        this.height = height;
    }

    public Vector3D getCenter() {
        return center;
    }

    public double getSphereCenterOffset() {
        return Math.sqrt(radius * radius - height * height / 4.0);
    }

    private boolean rayHitsLens(Ray ray) {
        assert(ray != null);

        Vector3D rayOrigin = ray.getOrigin();
        Vector3D rayDirNormalized = ray.getDir();
        Vector3D rayOriginToLensPlane = new Vector3D(0.0, 0.0, center.getZ() - rayOrigin.getZ());
        Vector3D rayOriginToLensCenter = center.subtract(rayOrigin);
        double cosAngle = rayOriginToLensPlane.normalize().dotProduct(rayOriginToLensCenter.normalize());
        double lensPlaneCollisionDistance = center.distance(rayOrigin) * cosAngle;
        if (lensPlaneCollisionDistance < 0.0) {
            return false;
        }

        Vector3D lensPlaneCollisionPoint = rayOrigin.add(lensPlaneCollisionDistance, rayDirNormalized);
        return lensPlaneCollisionPoint.distance(center) <= height / 2.0;

    }

    private Ray refract(Ray ray,
                        Vector3D nearSphereCenter,
                        Vector3D nearIntersectionPoint,
                        Vector3D farSphereCenter) {
        assert ray != null;
        assert nearSphereCenter != null;
        assert nearIntersectionPoint != null;
        assert farSphereCenter != null;

        final double AIR_REFRACTIVE_INDEX = 1.0;
        final double GLASS_REFRACTIVE_INDEX = 1.5;

        Vector3D nearSphereNormal = nearIntersectionPoint.subtract(nearSphereCenter).normalize();
        Vector3D refractedDir = Geometry.refract(ray.getDir(),
                                                 nearSphereNormal,
                                                 AIR_REFRACTIVE_INDEX,
                                                 GLASS_REFRACTIVE_INDEX);
        if (refractedDir == null) {
            return null;
        }

        Ray insideRay = new Ray(nearIntersectionPoint, refractedDir);
        intermediateRay = insideRay;
        Vector3D farSphereIntersection = Geometry.raySphereIntersection(insideRay, farSphereCenter, radius);
        System.err.println("outside: " + ray + "\ninside: " + insideRay + "\nfarSphereCenter = " + farSphereCenter + "\nradius = " + radius + "\nintersection = " + farSphereIntersection);
        assert farSphereIntersection != null;

        // intentionally points towards the sphere center
        Vector3D farSphereNormal = farSphereCenter.subtract(farSphereIntersection).normalize();
        return new Ray(farSphereIntersection,
                       Geometry.refract(ray.getDir(),
                                        farSphereNormal,
                                        GLASS_REFRACTIVE_INDEX,
                                        AIR_REFRACTIVE_INDEX));
    }

    public Ray refract(Ray ray) {
        assert(ray != null);

        if (!rayHitsLens(ray)) {
//            System.err.println("ray " + ray + " does not hit lens " + this);
            return ray;
        }

        double sphereCenterOffset = getSphereCenterOffset();
        Vector3D firstSphereCenter = new Vector3D(center.getX(), center.getY(), center.getZ() + sphereCenterOffset);
        Vector3D secondSphereCenter = new Vector3D(center.getX(), center.getY(), center.getZ() - sphereCenterOffset);

        Vector3D firstIntersectionPoint = Geometry.raySphereIntersection(ray, firstSphereCenter, radius);
        Vector3D secondIntersectionPoint = Geometry.raySphereIntersection(ray, secondSphereCenter, radius);

        assert firstIntersectionPoint != null;
        assert secondIntersectionPoint != null;

        if (firstIntersectionPoint.distanceSq(ray.getOrigin()) < secondIntersectionPoint.distanceSq(ray.getOrigin())) {
            return refract(ray, firstSphereCenter, firstIntersectionPoint, secondSphereCenter);
        } else {
            return refract(ray, secondSphereCenter, secondIntersectionPoint, firstSphereCenter);
        }
    }

    public Ray getIntermediateRay() {
        assert intermediateRay != null;
        return intermediateRay;
    }

    @Override
    public String toString() {
        return "Lens{" +
                "center=" + center +
                ", radius=" + radius +
                ", height=" + height +
                '}';
    }
}
