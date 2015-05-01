import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.junit.Test;
import stochastic.PhiTheta;
import stochastic.Utils;

import static org.junit.Assert.*;

/**
 * Created by dex on 02.05.15.
 */
public class PhiThetaTest {
    @Test
    public void testConstructorFromPosition() throws Exception {
        PhiTheta angles = new PhiTheta(new Vector3D(1.0, 0.0, 0.0));
        assertEquals(0.0, angles.getPhi().getRadians(), Utils.EPSILON);
        assertEquals(0.0, angles.getTheta().getRadians(), Utils.EPSILON);

        angles = new PhiTheta(new Vector3D(-1.0, 0.0, 0.0));
        assertEquals(Math.PI, angles.getPhi().getRadians(), Utils.EPSILON);
        assertEquals(0.0, angles.getTheta().getRadians(), Utils.EPSILON);

        angles = new PhiTheta(new Vector3D(0.0, 1.0, 0.0));
        assertEquals(0.5 * Math.PI, angles.getTheta().getRadians(), Utils.EPSILON);

        angles = new PhiTheta(new Vector3D(0.0, -1.0, 0.0));
        assertEquals(-0.5 * Math.PI, angles.getTheta().getRadians(), Utils.EPSILON);

        angles = new PhiTheta(new Vector3D(0.0, 0.0, 1.0));
        assertEquals(0.5 * Math.PI, angles.getPhi().getRadians(), Utils.EPSILON);
        assertEquals(0.0, angles.getTheta().getRadians(), Utils.EPSILON);

        angles = new PhiTheta(new Vector3D(0.0, 0.0, -1.0));
        assertEquals(-0.5 * Math.PI, angles.getPhi().getRadians(), Utils.EPSILON);
        assertEquals(0.0, angles.getTheta().getRadians(), Utils.EPSILON);
    }
}