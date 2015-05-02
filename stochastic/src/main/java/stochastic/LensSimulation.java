package stochastic;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import stochastic.visualizer.SimulationVisualizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LensSimulation {
	private final List<Lens> lensList = new ArrayList<Lens>();

    public LensSimulation(List<Lens> lensList) {
        this.lensList.addAll(lensList);
        Collections.sort(this.lensList, (a, b) -> (int)Math.signum(a.getCenter().getZ() - b.getCenter().getZ()));

        for (int i = 1; i < this.lensList.size(); i++) {
            assert !Utils.almostEqual(this.lensList.get(i - 1).getCenter().getZ(),
                                      this.lensList.get(i).getCenter().getZ());
        }
    }

    public List<Lens> getLensList() {
        return Collections.unmodifiableList(lensList);
    }

    public Ray simulate(Ray inputRay) {
		Ray ray = inputRay;
        try {
            for (Lens lens : lensList) {
                ray = lens.refract(ray);
                if (ray == null) {
                    return null;
                }
            }
        } catch (AssertionError e) {
            new SimulationVisualizer(this, inputRay).setVisible(true);
        }

		return ray;
	}

    public List<Ray> simulationSteps(Ray inputRay) {
        List<Ray> intermediateRays = new ArrayList<>();
        intermediateRays.add(inputRay);

        Ray ray = inputRay;
        for (Lens lens : lensList) {
            try {
                ray = lens.refract(ray);
            } catch (AssertionError e) {
                ray = null;
            }

            if (ray == null) {
                break;
            }
        }

        return intermediateRays;
    }

    public Range<Double> getXRange() {
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;

        for (Lens lens : lensList) {
            if (lens.getCenter().getX() - lens.radius < minX) {
                minX = lens.getCenter().getX() - lens.radius;
            }
            if (lens.getCenter().getX() + lens.radius > maxX) {
                maxX = lens.getCenter().getX() + lens.radius;
            }
        }

        return new Range<>(minX, maxX);
    }

    public Range<Double> getYRange() {
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Lens lens : lensList) {
            if (lens.getCenter().getY() - lens.radius < minY) {
                minY = lens.getCenter().getY() - lens.radius;
            }
            if (lens.getCenter().getY() + lens.radius > maxY) {
                maxY = lens.getCenter().getY() + lens.radius;
            }
        }

        return new Range<>(minY, maxY);
    }

    public Range<Double> getZRange() {
        double minZ = Double.MAX_VALUE;
        double maxZ = Double.MIN_VALUE;

        for (Lens lens : lensList) {
            if (lens.getCenter().getZ() - lens.radius < minZ) {
                minZ = lens.getCenter().getZ() - lens.radius;
            }
            if (lens.getCenter().getZ() + lens.radius > maxZ) {
                maxZ = lens.getCenter().getZ() + lens.radius;
            }
        }

        return new Range<>(minZ, maxZ);
    }
}
