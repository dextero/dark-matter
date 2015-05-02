package stochastic.visualizer;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import stochastic.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by dex on 02.05.15.
 */
public class SimulationVisualizer extends JFrame implements KeyListener {
    private final LensSimulation simulation;
    private final Ray inputRay;
    private final Range<Double> simulationX;
    private final Range<Double> simulationY;
    private final Range<Double> simulationZ;

    private SimulationVisualizer(LensSimulation simulation,
                                 Ray inputRay) throws HeadlessException {
        super("SimulationVisualizer");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1440, 900);
        addKeyListener(this);

        this.simulation = simulation;
        this.inputRay = inputRay;
        this.simulationX = simulation.getXRange();
        this.simulationY = simulation.getYRange();
        this.simulationZ = new Range<>(-1.0, inputRay.getOrigin().getZ() + 1.0);

        System.err.println("x: " + simulationX);
        System.err.println("y: " + simulationY);
        System.err.println("z: " + simulationZ);
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            dispose();
        }
    }

    public static void show(LensSimulation simulation,
                            Ray inputRay) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new SimulationVisualizer(simulation, inputRay).setVisible(true);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }

    public double scaleX(double x) {
        return x * (double)getHeight() / (simulationX.getMax() - simulationX.getMin());
    }

    public double scaleZ(double z) {
        return z * (double)getWidth() / (simulationZ.getMax() - simulationZ.getMin());
    }

    public int translateX(double x) {
        return (int)((double)getHeight() / (simulationX.getMax() - simulationX.getMin()) * (x - simulationX.getMin()));
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

        int sphereX = translateX(sphereTopLeft.getX());
        int firstSphereZ = translateZ(center.getZ() + sphereCenterOffset - lens.radius);
        int secondSphereZ = translateZ(center.getZ() - sphereCenterOffset - lens.radius);
        int diameterX = (int)scaleX(lens.radius * 2.0);
        int diameterZ = (int)scaleZ(lens.radius * 2.0);

        Angle angleDelta = Angle.fromRadians(Math.atan(lens.height / 2.0 / sphereCenterOffset));

        g.setColor(Color.BLUE);
        g.drawArc(firstSphereZ, sphereX, diameterZ, diameterX,
                  180 + (int) -angleDelta.getDegrees(), (int) (2.0 * angleDelta.getDegrees()));

        g.drawArc(secondSphereZ, sphereX, diameterZ, diameterX,
                  (int) -angleDelta.getDegrees(), (int) (2.0 * angleDelta.getDegrees()));
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

    private void markPoint(Graphics g,
                           Vector3D point,
                           Color color) {
        int POINT_SIZE = 16;

        int x = translateZ(point.getZ()) - POINT_SIZE / 2;
        int y = translateX(point.getX()) - POINT_SIZE / 2;

        g.setColor(color);
        g.fillOval(x, y, POINT_SIZE, POINT_SIZE);
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

        markPoint(g, Vector3D.ZERO, Color.GREEN);
        markPoint(g, rays.get(0).getOrigin(), Color.ORANGE);
    }

    public static void main(String[] args) {
        java.util.List<Lens> lenses = new ArrayList<>();
        lenses.add(new Lens(new Vector3D(3.0, 3.0, 5.0), 70.0, 10.0));

        LensSimulation sim = new LensSimulation(lenses);
        Ray inputRay = new Ray(new Vector3D(0.0, 0.0, 10.0), new Vector3D(0.0, 0.0, -1.0));

        SimulationVisualizer.show(sim, inputRay);
    }
}
