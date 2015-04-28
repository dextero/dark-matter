package stochastic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LensSimulation {
	
	private List<Lens> lensList = new ArrayList<Lens>();

    public LensSimulation(List<Lens> lensList) {
        this.lensList = lensList;
        Collections.sort(this.lensList, (a, b) -> (int)Math.signum(a.getCenter().getZ() - b.getCenter().getZ()));
    }

    public Ray simulate(Ray inputRay) {
		Ray ray = inputRay;
		for (Lens lens : lensList) {
            ray = lens.refract(ray);
            if (ray == null) {
                return null;
            }
		}
		return ray;
	}
	
}
