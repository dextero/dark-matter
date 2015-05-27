package stochastic;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;
import java.util.List;

public class Lens {
	private Vector3D center;
	/**
	 * Radius of the sphere the lens shares surface with
	 */
	public final double radius;
	public final double height;
    private List<Ray> intermediateRays = new ArrayList<>();

    public Lens(Vector3D center, double radius, double height) throws InvalidArgumentException {
        if (radius * 2.0 <= height) {
            throw new InvalidArgumentException("lens height must more than 2 times as large as its radius");
        }

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

        double rayOriginToLensPlaneZ = center.getZ() - rayOrigin.getZ();
        if (rayOriginToLensPlaneZ * rayDirNormalized.getZ() <= 0.0) {
            return false;
        }

        double rayDistanceUntilLensPlane = rayOriginToLensPlaneZ / rayDirNormalized.getZ();
        Vector3D lensPlaneCollisionPoint = rayOrigin.add(rayDistanceUntilLensPlane, rayDirNormalized);
        return lensPlaneCollisionPoint.distance(center) <= height / 2.0;
    }

    private static final double AIR_REFRACTIVE_INDEX = 1.0;
    private static final double GLASS_REFRACTIVE_INDEX = 1.5;

    private Ray refractInsideLens(Ray ray,
                                  Vector3D nearSphereCenter,
                                  Vector3D farSphereCenter) {
        intermediateRays.add(ray);

        Vector3D farSphereIntersection = Geometry.raySphereIntersection(ray, farSphereCenter, radius);
        assert farSphereIntersection != null;

        // intentionally points towards the sphere center
        Vector3D farSphereNormal = farSphereCenter.subtract(farSphereIntersection).normalize();
        Vector3D refractedDir = Geometry.refract(ray.getDir(),
                                                 farSphereNormal,
                                                 GLASS_REFRACTIVE_INDEX,
                                                 AIR_REFRACTIVE_INDEX);
        if (refractedDir == null) {
            // total internal reflection
            System.err.println("total internal reflection (2)");
            Ray reflected = new Ray(farSphereIntersection, Geometry.reflect(ray.getDir(), farSphereNormal));
            return refractInsideLens(reflected, farSphereCenter, nearSphereCenter);
        }

        return new Ray(farSphereIntersection, refractedDir);
    }

    private Ray refract(Ray ray,
                        Vector3D nearSphereCenter,
                        Vector3D nearIntersectionPoint,
                        Vector3D farSphereCenter) {
        assert ray != null;
        assert nearSphereCenter != null;
        assert nearIntersectionPoint != null;
        assert farSphereCenter != null;

        intermediateRays.add(ray);

        Vector3D nearSphereNormal = nearIntersectionPoint.subtract(nearSphereCenter).normalize();
        Vector3D refractedDir = Geometry.refract(ray.getDir(),
                                                 nearSphereNormal,
                                                 AIR_REFRACTIVE_INDEX,
                                                 GLASS_REFRACTIVE_INDEX);
        if (refractedDir == null) {
            // total internal reflection
            System.err.println("total internal reflection (1)");
            return new Ray(nearIntersectionPoint,
                           Geometry.reflect(ray.getDir(), nearSphereNormal));
        }

        Ray insideRay = new Ray(nearIntersectionPoint, refractedDir);
        return refractInsideLens(insideRay, nearSphereCenter, farSphereCenter);
    }

    public Ray refract(Ray ray) {
        assert ray != null;

        intermediateRays.clear();

        if (!rayHitsLens(ray)) {
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

    public List<Ray> getIntermediateRays() {
        assert intermediateRays != null;
        return intermediateRays;
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
