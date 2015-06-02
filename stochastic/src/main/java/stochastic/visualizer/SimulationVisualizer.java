package stochastic.visualizer;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import stochastic.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.util.List;

/**
 * Created by dex on 02.05.15.
 */
public class SimulationVisualizer extends JFrame implements KeyListener {
    private final LensSimulation simulation;
    private final Range<Double> simulationX;
    private final Range<Double> simulationY;
    private final Range<Double> simulationZ;
    private final java.util.List<java.util.List<Ray>> rayPaths;
    private final Vector3D lightSource;

    private SimulationVisualizer(LensSimulation simulation,
                                 java.util.List<java.util.List<Ray>> rayPaths,
                                 Vector3D lightSource) throws HeadlessException {
        super("SimulationVisualizer");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1440, 900);
        addKeyListener(this);

        this.simulation = simulation;
        this.simulationX = simulation.getXRange();
        this.simulationY = simulation.getYRange();
        if (lightSource == null) {
            this.simulationZ = new Range<>(-1.0, simulation.getRayPath().get(0).getOrigin().getZ() + 1.0);
        } else {
            double min = -1.0;
            double max = simulation.getRayPath().get(0).getOrigin().getZ() + 1.0;
            if (lightSource.getZ() - 1.0 < min) {
                min = lightSource.getZ() - 1.0;
            } else if (lightSource.getZ() + 1.0 > max) {
                max = lightSource.getZ() + 1.0;
            }
            this.simulationZ = new Range<>(min, max);
        }
        this.rayPaths = rayPaths;
        this.lightSource = lightSource;

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

    public static void show(LensSimulation simulation) {
        java.util.List<java.util.List<Ray>> rayPaths = new ArrayList<>();
        rayPaths.add(simulation.getRayPath());

        show(simulation, rayPaths);
    }

    public static void show(LensSimulation simulation,
                            java.util.List<java.util.List<Ray>> rayPaths) {
        show(simulation, rayPaths, null);
    }

    public static void show(LensSimulation simulation,
                            java.util.List<java.util.List<Ray>> rayPaths,
                            Vector3D lightSource) {
        System.err.println("ray path: " + rayPaths.get(0).size());
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new SimulationVisualizer(simulation, rayPaths, lightSource).setVisible(true);
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
        return getHeight() - (int)((double)getHeight() / (simulationX.getMax() - simulationX.getMin()) * (x - simulationX.getMin()));
    }

    public int translateZ(double z) {
        return (int)((double)getWidth() / (simulationZ.getMax() - simulationZ.getMin()) * (z - simulationZ.getMin()));
    }

    private void drawLensArc(Graphics g,
                             Vector3D sphereCenter,
                             double sphereRadius,
                             Angle arcStart,
                             Angle arcEnd,
                             Angle step) {
        int prevX = translateZ(Math.cos(arcStart.getRadians()) * sphereRadius + sphereCenter.getZ());
        int prevY = translateX(Math.sin(arcStart.getRadians()) * sphereRadius + sphereCenter.getX());

        for (Angle angle = Angle.fromRadians(arcStart.getRadians() + step.getRadians());
             angle.compareTo(arcEnd) <= 0;
             angle = Angle.fromRadians(angle.getRadians() + step.getRadians())) {
            int x = translateZ(Math.cos(angle.getRadians()) * sphereRadius + sphereCenter.getZ());
            int y = translateX(Math.sin(angle.getRadians()) * sphereRadius + sphereCenter.getX());

            g.drawLine(prevX, prevY, x, y);

            prevX = x;
            prevY = y;
        }

        int x = translateZ(Math.cos(arcEnd.getRadians()) * sphereRadius + sphereCenter.getZ());
        int y = translateX(Math.sin(arcEnd.getRadians()) * sphereRadius + sphereCenter.getX());
        g.drawLine(prevX, prevY, x, y);
    }

    private void drawLens(Graphics g,
                          Lens lens) {
        Vector3D center = lens.getCenter();
        double sphereCenterOffset = lens.getSphereCenterOffset();

        Vector3D firstSphereCenter = new Vector3D(center.getX(), center.getY(), center.getZ() - sphereCenterOffset);
        Vector3D secondSphereCenter = new Vector3D(center.getX(), center.getY(), center.getZ() + sphereCenterOffset);

        Angle angleDelta = Angle.fromRadians(Math.atan(lens.height / 2.0 / sphereCenterOffset));

        g.setColor(Color.BLUE);
        drawLensArc(g, firstSphereCenter, lens.radius,
                    Angle.fromRadians(-angleDelta.getRadians()),
                    Angle.fromRadians(angleDelta.getRadians()),
                    Angle.fromRadians(0.01));
        drawLensArc(g, secondSphereCenter, lens.radius,
                    Angle.fromRadians(Math.PI - angleDelta.getRadians()),
                    Angle.fromRadians(Math.PI + angleDelta.getRadians()),
                    Angle.fromRadians(0.01));
    }

    private void drawRay(Graphics g,
                         Ray ray,
                         Vector3D end)
    {
        markPoint(g, ray.getOrigin(), Color.RED);
        g.setColor(Color.RED);
        Vector3D start = ray.getOrigin();

        if (end != null) {
            g.drawLine(translateZ(ray.getOrigin().getZ()), translateX(ray.getOrigin().getX()),
                       translateZ(end.getZ()), translateX(end.getX()));
            start = end;
            end = start.add(2.0, ray.getDir());
        } else {
            end = ray.getOrigin().add(2.0, ray.getDir());
        }

        Graphics2D g2d = (Graphics2D)g.create();
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
        g2d.drawLine(translateZ(start.getZ()), translateX(start.getX()),
                     translateZ(end.getZ()), translateX(end.getX()));
        g2d.dispose();
    }

    private void markPoint(Graphics g,
                           Vector3D point,
                           Color color) {
        int POINT_SIZE = 10;

        int x = translateZ(point.getZ()) - POINT_SIZE / 2;
        int y = translateX(point.getX()) - POINT_SIZE / 2;

        g.setColor(color);
        g.fillOval(x, y, POINT_SIZE, POINT_SIZE);
    }

    @Override
    public void paint(Graphics g) {
        try {
            g.clearRect(0, 0, getWidth(), getHeight());

            for (Lens lens : simulation.getLensList()) {
                drawLens(g, lens);
            }

            for (List<Ray> path : rayPaths) {
                for (int i = 0; i < path.size() - 1; i++) {
                    drawRay(g, path.get(i), path.get(i + 1).getOrigin());
                }

                Ray lastRay = path.get(path.size() - 1);
                Vector3D endPoint = lastRay.getOrigin().add(lastRay.getDir().scalarMultiply(10.0));
                drawRay(g, path.get(path.size() - 1), endPoint);
            }

            if (lightSource == null) {
                markPoint(g, Vector3D.ZERO, Color.GREEN);
                markPoint(g, rayPaths.get(0).get(0).getOrigin(), Color.ORANGE);
            } else {
                markPoint(g, rayPaths.get(0).get(0).getOrigin(), Color.GREEN);
                markPoint(g, lightSource, Color.ORANGE);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            List<Lens> lenses = new ArrayList<>();
            lenses.add(new Lens(new Vector3D(0.0, 0.0, 3.0), 20.0, 10.0));
            lenses.add(new Lens(new Vector3D(0.0, 0.0, 7.0), 20.0, 10.0));

            LensSimulation sim = new LensSimulation(lenses);
            List<List<Ray>> paths = new ArrayList<>();

            final double X_LIMIT = 1.5;
            for (double x = -X_LIMIT; x <= X_LIMIT; x += 0.1) {
                Ray inputRay = new Ray(new Vector3D(0.0, 0.0, 10.0), new Vector3D(x, 0.0, -1.0).normalize());
                sim.simulate(inputRay);
                paths.add(sim.getRayPath());
            }

            SimulationVisualizer.show(sim, paths);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }
    }
}
