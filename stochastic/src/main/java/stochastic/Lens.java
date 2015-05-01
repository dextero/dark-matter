package stochastic;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Lens {
	private Vector3D center;
	/**
	 * Radius of the sphere the lens shares surface with
	 */
	private double radius;
	private double lensHeight;

    public Lens(Vector3D center, double radius, double lensHeight) {
        this.center = center;
        this.radius = radius;
        this.lensHeight = lensHeight;
    }

    public Vector3D getCenter() {
        return center;
    }

    private double getSphereCenterOffset() {
        return Math.sqrt(radius * radius - lensHeight * lensHeight / 4.0);
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
        return lensPlaneCollisionPoint.distance(center) <= lensHeight / 2.0;

    }

    private Ray refractOnSphereSurface(Ray ray,
                                       Vector3D sphereNormalAtIntersectionPoint,
                                       Vector3D intersectionPoint,
                                       double srcRefractiveIndex,
                                       double dstRefractiveIndex) {
        double refractiveIndexRatio = srcRefractiveIndex / dstRefractiveIndex;
        double srcAngleToNormal = Math.acos(ray.getDir().normalize().negate().dotProduct(sphereNormalAtIntersectionPoint));

        if (Math.sin(srcAngleToNormal) > dstRefractiveIndex / srcRefractiveIndex) {
            // total internal reflection
            System.err.println("refractOnSphereSurface: total internal reflection");
            return null;
        }

        double cosSrcAngleToNormal = Math.cos(srcAngleToNormal);
        double sinDstAngleToNormalSq = refractiveIndexRatio * refractiveIndexRatio * (1.0 - cosSrcAngleToNormal * cosSrcAngleToNormal);

        Vector3D refractedDir = ray.getDir()
                .scalarMultiply(refractiveIndexRatio)
                .add(sphereNormalAtIntersectionPoint.scalarMultiply(refractiveIndexRatio * cosSrcAngleToNormal - Math.sqrt(1.0 - sinDstAngleToNormalSq)));

        return new Ray(intersectionPoint, refractedDir);
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
        Ray insideRay = refractOnSphereSurface(ray, nearSphereNormal, nearIntersectionPoint, AIR_REFRACTIVE_INDEX, GLASS_REFRACTIVE_INDEX);
        if (insideRay == null) {
            return null;
        }

        Vector3D farSphereIntersection = Geometry.raySphereIntersection(insideRay, farSphereCenter, radius);
        assert farSphereIntersection != null;

        // intentionally points towards the sphere center
        Vector3D farSphereNormal = farSphereCenter.subtract(farSphereIntersection).normalize();
        return refractOnSphereSurface(ray, farSphereNormal, farSphereIntersection, GLASS_REFRACTIVE_INDEX, AIR_REFRACTIVE_INDEX);
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

    @Override
    public String toString() {
        return "Lens{" +
                "center=" + center +
                ", radius=" + radius +
                ", lensHeight=" + lensHeight +
                '}';
    }
}
