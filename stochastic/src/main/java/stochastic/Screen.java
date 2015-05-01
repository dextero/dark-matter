package stochastic;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Screen {
    Range<PhiTheta> angleRange;
    int resolutionX;
    int resolutionY;

    public Screen(Range<PhiTheta> angleRange,
                  int resolutionX,
                  int resolutionY) {
        assert resolutionX > 0;
        assert resolutionY > 0;
        assert angleRange.getMin().getPhi().getRadians() < angleRange.getMax().getPhi().getRadians();
        assert angleRange.getMin().getTheta().getRadians() < angleRange.getMax().getTheta().getRadians();

        this.angleRange = angleRange;
        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;
    }

    public boolean contains(PhiTheta angles) {
        double phi = angles.getPhi().getRadians();
        double minPhi = angleRange.getMin().getPhi().getRadians();
        double maxPhi = angleRange.getMax().getPhi().getRadians();

        double theta = angles.getPhi().getRadians();
        double minTheta = angleRange.getMin().getTheta().getRadians();
        double maxTheta = angleRange.getMax().getTheta().getRadians();

        return minPhi <= phi && phi <= maxPhi
                && minTheta <= theta && theta <= maxTheta;
    }

    public PhiTheta getRayCollisionPosAngles(Ray ray) {
        Vector3D intersection = Geometry.raySphereIntersection(ray, Vector3D.ZERO, 1.0);

        if (intersection == null) {
            return null;
        }

        PhiTheta angles = new PhiTheta(intersection);
        return contains(angles) ? angles : null;
    }

    public Vector2D anglesToPixel(PhiTheta angles) {
        assert contains(angles);

        double minPhi = angleRange.getMin().getPhi().getRadians();
        double maxPhi = angleRange.getMax().getPhi().getRadians();
        double minTheta = angleRange.getMin().getTheta().getRadians();
        double maxTheta = angleRange.getMax().getTheta().getRadians();

        double deltaPhiPerPixel = (maxPhi - minPhi) / (float)(resolutionX - 1);
        double deltaThetaPerPixel = (maxTheta - minTheta) / (float)(resolutionY - 1);

        double deltaPhi = angles.getPhi().getRadians() - minPhi;
        double deltaTheta = angles.getTheta().getRadians() - minTheta;

        return new Vector2D(Math.round(deltaPhi / deltaPhiPerPixel), Math.round(deltaTheta / deltaThetaPerPixel));
    }
}
