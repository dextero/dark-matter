package stochastic.hgs;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.jage.app.hgs.IPopulation;
import org.jage.app.hgs.problem.IChromosome;
import org.jage.app.hgs.problem.IFitnessFunction;
import org.jage.app.hgs.problem.IProblemDomain;
import org.jage.app.hgs.problem.IResolution;
import org.jage.app.hgs.problem.real.RealChromosome;

import org.jage.app.hgs.problem.real.RealDomain;
import stochastic.*;

import java.util.ArrayList;
import java.util.List;

public class LensProblemFitnessFunction implements IFitnessFunction {
    private List<PhiTheta> images;
    private Vector3D lightSource;

    public LensProblemFitnessFunction(List<PhiTheta> images,
                                      Vector3D lightSource) {
        this.images = images;
        this.lightSource = lightSource;
    }

    private double rescale(double normalizedValue,
                           double min,
                           double max) {
        return min + (max - min) * normalizedValue;
    }

    private LensSimulation createSimulation(double[] values,
                                            RealDomain domain) throws InvalidArgumentException {
        assert values.length % 5 == 0;

        double[] low = domain.getLowerBound();
        double[] up = domain.getUpperBound();

        List<Lens> lenses = new ArrayList<>();
        for (int i = 0; i < values.length; i += 5) {
            lenses.add(new Lens(new Vector3D(rescale(values[i], low[i], up[i]),
                                             rescale(values[i + 1], low[i + 1], up[i + 1]),
                                             rescale(values[i + 2], low[i + 2], up[i + 2])),
                                rescale(values[i + 3], low[i + 3], up[i + 3]),
                                rescale(values[i + 4], low[i + 4], up[i + 4])));
        }
        return new LensSimulation(lenses);
    }

    @Override
    public void computeFitness(IPopulation population,
                               IProblemDomain problemDomain,
                               IResolution resolution) {
        for (IChromosome chromosome : population.getPopulation()) {
            RealChromosome realChromosome = (RealChromosome) chromosome;
            double fitness = 0.0;

            try {
                LensSimulation simulation = createSimulation(realChromosome.getGenes(), ((RealDomain) problemDomain));

                for (PhiTheta image : images) {
                    Ray inputRay = new Ray(Vector3D.ZERO, image.toPosition());
                    Ray outputRay = simulation.simulate(inputRay);
                    fitness += 1e5 - Geometry.pointRayDistance(outputRay, lightSource);
                }
            } catch (InvalidArgumentException e) {
                fitness = 0.0;
            }

            chromosome.setFitness(fitness);
        }
    }
}


