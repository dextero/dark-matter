package stochastic;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * Created by dex on 01.05.15.
 */
public class PhiTheta implements Comparable<PhiTheta> {
    private final Angle phi;
    private final Angle theta;

    public PhiTheta(Angle phi,
                    Angle theta) {
        assert phi != null;
        assert theta != null;

        this.phi = phi;
        this.theta = theta;
    }

    public PhiTheta(Vector3D pos) {
        assert pos != null;

        this.phi = Angle.fromRadians(Math.atan2(pos.getZ(), pos.getX()));
        this.theta = Angle.fromRadians(Math.asin(pos.getY() / pos.getNorm()));
    }

    public Angle getPhi() {
        return phi;
    }

    public Angle getTheta() {
        return theta;
    }

    public Vector3D toPosition() {
        return new Vector3D(Math.cos(theta.getRadians()) * Math.cos(phi.getRadians()),
                            Math.sin(theta.getRadians()),
                            Math.cos(theta.getRadians()) * Math.sin(phi.getRadians()));
    }

    @Override
    public int compareTo(PhiTheta o) {
        return 2 * phi.compareTo(o.phi) + theta.compareTo(o.theta);
    }

    @Override
    public String toString() {
        return "PhiTheta{" +
                "phi=" + phi +
                ", theta=" + theta +
                '}';
    }
}
