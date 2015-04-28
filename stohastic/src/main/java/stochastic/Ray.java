package stochastic;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Ray {
	Vector3D origin;
	Vector3D dir;

	public Ray(Vector3D origin, Vector3D dir) {
		this.origin = origin;
		this.dir = dir;
	}
	
	public Vector3D getOrigin() {
		return origin;
	}
	
	public Vector3D getDir() {
		return dir;
	}
}
