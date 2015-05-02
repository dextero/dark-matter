package stochastic.visualizer;

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

    public ScreenVisualizer(java.util.List<Vector2D> points,
                            int resolutionX,
                            int resolutionY) throws HeadlessException {
        super("ScreenVisualizer");

        Collections.copy(this.points, points);
        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
    }

    @Override
    public void paint(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());
        for (Vector2D point : points) {
            g.drawOval((int)(point.getX() * (double)getWidth() / (double)resolutionX),
                       (int)(point.getY() * (double)getHeight() / (double)resolutionY),
                       5, 5);
        }
    }
}
