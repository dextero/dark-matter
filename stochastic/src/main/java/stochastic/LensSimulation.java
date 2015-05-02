package stochastic;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import stochastic.visualizer.SimulationVisualizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LensSimulation {
	private final List<Lens> lensList = new ArrayList<Lens>();
    private final List<Ray> simulationSteps = new ArrayList<>();

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
        simulationSteps.add(ray);
        try {
            for (Lens lens : lensList) {
                ray = lens.refract(ray);
                if (ray == null) {
                    return null;
                }
                simulationSteps.add(lens.getIntermediateRay());
                simulationSteps.add(ray);
            }
        } catch (AssertionError e) {
            SimulationVisualizer.show(this);
            return ray;
        }

		return ray;
	}

    public List<Ray> getSimulationSteps() {
        assert !simulationSteps.isEmpty();
        return simulationSteps;
    }

    public Range<Double> getXRange() {
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;

        for (Lens lens : lensList) {
            if (lens.getCenter().getX() - lens.height < minX) {
                minX = lens.getCenter().getX() - lens.height;
            }
            if (lens.getCenter().getX() + lens.height > maxX) {
                maxX = lens.getCenter().getX() + lens.height;
            }
        }

        return new Range<>(minX, maxX);
    }

    public Range<Double> getYRange() {
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Lens lens : lensList) {
            if (lens.getCenter().getY() - lens.height < minY) {
                minY = lens.getCenter().getY() - lens.height;
            }
            if (lens.getCenter().getY() + lens.height > maxY) {
                maxY = lens.getCenter().getY() + lens.height;
            }
        }

        return new Range<>(minY, maxY);
    }

    public Range<Double> getZRange() {
        double minZ = Double.MAX_VALUE;
        double maxZ = Double.MIN_VALUE;

        for (Lens lens : lensList) {
            if (lens.getCenter().getZ() - lens.height < minZ) {
                minZ = lens.getCenter().getZ() - lens.height;
            }
            if (lens.getCenter().getZ() + lens.height > maxZ) {
                maxZ = lens.getCenter().getZ() + lens.height;
            }
        }

        return new Range<>(minZ, maxZ);
    }
}
