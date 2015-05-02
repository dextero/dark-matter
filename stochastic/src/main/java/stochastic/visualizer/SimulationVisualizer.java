package stochastic.visualizer;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import stochastic.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by dex on 02.05.15.
 */
public class SimulationVisualizer extends JFrame {
    private final LensSimulation simulation;
    private final Ray inputRay;
    private final Range<Double> simulationX;
    private final Range<Double> simulationY;
    private final Range<Double> simulationZ;

    public SimulationVisualizer(LensSimulation simulation,
                                Ray inputRay) throws HeadlessException {
        super("SimulationVisualizer");

        this.simulation = simulation;
        this.inputRay = inputRay;
        this.simulationX = simulation.getXRange();
        this.simulationY = simulation.getYRange();
        this.simulationZ = new Range<Double>(-1.0, inputRay.getOrigin().getZ() + 1.0);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1440, 900);
    }

    public int translateX(double x) {
        return (int)((double)getHeight() / (simulationX.getMax() - simulationX.getMin()) * (x - simulationX.getMin()));
    }

    public int translateY(double y) {
        return (int)((double)getHeight() / (simulationY.getMax() - simulationY.getMin()) * (y - simulationY.getMin()));
    }

    public int translateZ(double z) {
        return (int)((double)getWidth() / (simulationZ.getMax() - simulationZ.getMin()) * (z - simulationZ.getMin()));
    }

    private void drawLens(Graphics g,
                          Lens lens) {
        Vector3D center = lens.getCenter();
        double sphereCenterOffset = lens.getSphereCenterOffset();
        Vector2D sphereTopLeft = new Vector2D(center.getX() - lens.radius,
                                              center.getY() - lens.radius);
        double firstSphereZ = center.getZ() + sphereCenterOffset;
        double secondSphereZ = center.getZ() - sphereCenterOffset;

        Angle angleDelta = Angle.fromRadians(Math.atan(lens.height / lens.radius));

        g.setColor(Color.BLUE);
        g.drawArc(translateZ(firstSphereZ), translateX(sphereTopLeft.getX()),
                  (int)(lens.radius * 2.0), (int)(lens.radius * 2.0),
                  180 + (int)-angleDelta.getDegrees(), (int)(2.0 * angleDelta.getDegrees()));

        g.drawArc(translateZ(secondSphereZ), translateX(sphereTopLeft.getX()),
                  (int)(lens.radius * 2.0), (int)(lens.radius * 2.0),
                  (int)-angleDelta.getDegrees(), (int)(2.0 * angleDelta.getDegrees()));
    }

    private void drawRay(Graphics g,
                         Ray ray,
                         Vector3D end)
    {
        if (end == null) {
            end = ray.getOrigin().add(1000.0, ray.getDir());
        }

        g.setColor(Color.RED);
        g.drawLine(translateZ(ray.getOrigin().getZ()), translateX(ray.getOrigin().getX()),
                   translateZ(end.getZ()), translateX(end.getX()));
    }

    @Override
    public void paint(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());

        for (Lens lens : simulation.getLensList()) {
            drawLens(g, lens);
        }

        java.util.List<Ray> rays = simulation.simulationSteps(inputRay);
        for (int i = 0; i < rays.size() - 1; i++) {
            drawRay(g, rays.get(i), rays.get(i + 1).getOrigin());
        }
        drawRay(g, rays.get(rays.size() - 1), null);
    }
}
