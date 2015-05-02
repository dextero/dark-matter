package stochastic.visualizer;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import stochastic.Screen;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by dex on 02.05.15.
 */
public class ScreenVisualizer extends JFrame {
    private final java.util.List<Vector2D> points = new ArrayList<>();
    private final int resolutionX;
    private final int resolutionY;

    private ScreenVisualizer(java.util.List<Vector2D> points,
                             int resolutionX,
                             int resolutionY) throws HeadlessException {
        super("ScreenVisualizer");

        this.points.addAll(points);
        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1440, 900);
    }

    public static void show(java.util.List<Vector2D> points,
                            int resolutionX,
                            int resolutionY) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ScreenVisualizer(points, resolutionX, resolutionY).setVisible(true);
            }
        });
    }

    private void markPoint(Graphics g,
                           Vector2D point,
                           Color color) {
        int POINT_SIZE = 16;

        int x = (int)(point.getX() * (double)getWidth() / (double)resolutionX);
        int y = (int)(point.getY() * (double)getHeight() / (double)resolutionY);

        g.fillOval(x, y, POINT_SIZE, POINT_SIZE);
    }

    @Override
    public void paint(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());
        for (Vector2D point : points) {
            markPoint(g, point, Color.ORANGE);
        }
    }
}
