import org.junit.Test;
import stochastic.Angle;
import stochastic.PhiTheta;
import stochastic.Range;
import stochastic.Screen;

import static org.junit.Assert.*;

/**
 * Created by dex on 02.05.15.
 */
public class ScreenTest {
    @Test
    public void testContains() throws Exception {
        Range<PhiTheta> range = new Range<>(new PhiTheta(Angle.fromRadians(-1.0), Angle.fromRadians(0.0)),
                                            new PhiTheta(Angle.fromRadians(-0.5), Angle.fromRadians(0.5)));
        Screen screen = new Screen(range, 1.0, 800, 600);

        assertTrue(screen.contains(new PhiTheta(Angle.fromRadians(-1.0), Angle.fromRadians(0.0))));
        assertTrue(screen.contains(new PhiTheta(Angle.fromRadians(-1.0), Angle.fromRadians(0.5))));
        assertTrue(screen.contains(new PhiTheta(Angle.fromRadians(-0.5), Angle.fromRadians(0.0))));
        assertTrue(screen.contains(new PhiTheta(Angle.fromRadians(-0.5), Angle.fromRadians(0.5))));
        assertTrue(screen.contains(new PhiTheta(Angle.fromRadians(-0.75), Angle.fromRadians(0.25))));

        assertFalse(screen.contains(new PhiTheta(Angle.fromRadians(-1.1), Angle.fromRadians(0.0))));
        assertFalse(screen.contains(new PhiTheta(Angle.fromRadians(-1.1), Angle.fromRadians(0.5))));
        assertFalse(screen.contains(new PhiTheta(Angle.fromRadians(-0.5), Angle.fromRadians(-0.1))));
        assertFalse(screen.contains(new PhiTheta(Angle.fromRadians(-0.5), Angle.fromRadians(0.6))));
    }

    @Test
    public void testGetRayCollisionPosAngles() throws Exception {

    }
}