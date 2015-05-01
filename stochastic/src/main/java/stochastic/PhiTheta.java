package stochastic;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * Created by dex on 01.05.15.
 */
public class PhiTheta {
    private Angle phi;
    private Angle theta;

    public PhiTheta(Angle phi,
                    Angle theta) {
        assert phi != null;
        assert theta != null;

        this.phi = phi;
        this.theta = theta;
    }

    public PhiTheta(Vector3D pos) {
        assert pos != null ;

        this.phi = Angle.fromRadians(Math.atan2(pos.getZ(), pos.getX()));
        this.theta = Angle.fromRadians(Math.acos(pos.getY() / Math.sqrt(pos.getX() * pos.getX() + pos.getZ() * pos.getZ())));
    }

    public Angle getPhi() {
        return phi;
    }

    public Angle getTheta() {
        return theta;
    }

    public Vector3D toPosition() {
        return new Vector3D(Math.sin(phi.getRadians()) * Math.cos(theta.getRadians()),
                            Math.cos(theta.getRadians()),
                            Math.cos(phi.getRadians()) * Math.cos(theta.getRadians()));
    }
}
