package stochastic;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class LensSimulation {
	private final List<Lens> lensList = new ArrayList<Lens>();
    private final List<Ray> rayPath = new ArrayList<>();

    public LensSimulation(List<Lens> lensList) throws InvalidArgumentException {
        this.lensList.addAll(lensList);
        Collections.sort(this.lensList, (a, b) -> (int)Math.signum(b.getCenter().getZ() - a.getCenter().getZ()));

        for (int i = 1; i < this.lensList.size(); i++) {
            if (Utils.almostEqual(this.lensList.get(i - 1).getCenter().getZ(),
                                  this.lensList.get(i).getCenter().getZ())) {
                throw new InvalidArgumentException("lenses must not overlap");
            }
        }
    }

    public List<Lens> getLensList() {
        return Collections.unmodifiableList(lensList);
    }

    public Ray simulate(Ray inputRay) {
		Ray ray = inputRay;
        Ray prevRay = inputRay;
        rayPath.clear();
        rayPath.add(ray);
        for (Lens lens : lensList) {
            ray = lens.refract(ray);
            if (ray == null) {
                return null;
            } else if (ray != prevRay) {
                rayPath.add(lens.getIntermediateRay());
                rayPath.add(ray);
            }
            prevRay = ray;
        }

		return ray;
	}

    public List<Ray> getRayPath() {
        assert !rayPath.isEmpty();
        List<Ray> result = new ArrayList<>();
        result.addAll(rayPath);
        return result;
    }

    private Range<Double> getRange(Function<Vector3D, Double> getter) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (Lens lens : lensList) {
            double val = getter.apply(lens.getCenter());

            if (val - lens.height < min) {
                min = val - lens.height;
            }
            if (val + lens.height > max) {
                max = val + lens.height;
            }
        }

        return new Range<>(min, max);
    }

    public Range<Double> getXRange() {
        return getRange(Vector3D::getX);
    }

    public Range<Double> getYRange() {
        return getRange(Vector3D::getY);
    }

    public Range<Double> getZRange() {
        return getRange(Vector3D::getZ);
    }
}
