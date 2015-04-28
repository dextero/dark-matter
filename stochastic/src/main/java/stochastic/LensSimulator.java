package stochastic;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;
import java.util.List;

public class LensSimulator {
	public static void main(String[] args) {
        List<Lens> lenses = new ArrayList<>();
        lenses.add(new Lens(new Vector3D(1.0, 1.0, 1.0), 100.0, 10.0));

        LensSimulation sim = new LensSimulation(lenses);
        Ray input = new Ray(new Vector3D(0.0, 0.0, 0.0),
                            new Vector3D(0.0, 0.0, 1.0));
        Ray output = sim.simulate(input);
        System.out.println("input = " + input);
        System.out.println("output = " + output);
    }
}
