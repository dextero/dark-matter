package stochastic;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class ThinLens {
	private Vector3D center;
	private double radius;
	private double lensRadius;
	private double reflectiveIndex_n;
	private double lenseHeight_h;
	
	public double getFocalLength() {
		return radius/2;
	}
	
	public void setRadius(double radius) {
		this.radius = radius;
	}

	public double getRadius() {
		return radius;
	}

	public double getReflectiveIndex_n() {
		return reflectiveIndex_n;
	}

	public void setReflectiveIndex_n(double reflectiveIndex_n) {
		this.reflectiveIndex_n = reflectiveIndex_n;
	}

	public double getLenseWidth_d() {
		return lenseHeight_h;
	}

	public void setLenseWidth_d(double lenseWidth_d) {
		this.lenseHeight_h = lenseWidth_d;
	}
	
	public Vector3D getCenter() {
		return center;
	}

	public void setCenter(Vector3D center) {
		this.center = center;
	}

	public boolean isColliding(Ray ray) {
		Vector3D rayOrigin = ray.getOrigin();
		Vector3D rayOriginOnLensPlane = new Vector3D(rayOrigin.getX(), rayOrigin.getY(), center.getZ());
		Vector3D rayOriginToLensPlane = rayOriginOnLensPlane.subtract(rayOrigin);
		double cosAngle = rayOriginToLensPlane.normalize().dotProduct(ray.getDir().normalize());
		double lensPlaneCollisionDistance = center.distance(ray.getOrigin()) * cosAngle;
		Vector3D lensPlaneCollisionPoint = rayOrigin.add(ray.getDir().scalarMultiply(lensPlaneCollisionDistance));
		return lensPlaneCollisionPoint.distance(center) < lensRadius;
	}
	
//	public Point getCollisionPoint(Ray ray, boolean isEarlier){	
//		return null;
//	}
//	
//	public Ray getRayAfterCollision(Ray ray) {
//		return null;
//	}
	
}
