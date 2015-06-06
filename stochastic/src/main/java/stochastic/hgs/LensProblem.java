package stochastic.hgs;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.jage.app.hgs.problem.AbstractProblem;
import org.jage.app.hgs.problem.IFitnessFunction;
import org.jage.app.hgs.problem.IProblemDomain;
import org.jage.app.hgs.problem.annotations.RealProblem;
import org.jage.app.hgs.problem.real.RealDomain;
import org.jage.core.config.ConfigurationException;
import org.jage.core.config.IConfiguration;
import stochastic.*;
import stochastic.visualizer.SimulationVisualizer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class LensProblem extends AbstractProblem {
    LensProblemFitnessFunction fitnessFunction;

    private final static Range<Double> lensPosX = new Range<>(-5.0, 5.0);
    private final static Range<Double> lensPosY = new Range<>(0.0, Utils.EPSILON);
    private final static Range<Double> lensPosZ = new Range<>(0.5, 0.5 + Utils.EPSILON);
    private final static Range<Double> lensRadius = new Range<>(2.0, 10.0);
    private final static Range<Double> lensHeight = new Range<>(0.5, 1.5);
    private final static Vector3D LIGHT_SOURCE = new Vector3D(0.0, 0.0, 10.0);
    private final static Angle X_ANGLE = Angle.fromDegrees(20.0);
    private final static List<PhiTheta> IMAGES = Arrays.asList(
            new PhiTheta(Angle.fromDegrees(90.0 - X_ANGLE.getDegrees()),
                         Angle.fromDegrees(0.0)));
    private int numLenses = 1;

    public LensProblem(IConfiguration config) throws ConfigurationException {
        super(config);

        fitnessFunction = new LensProblemFitnessFunction(IMAGES, LIGHT_SOURCE);
    }

    @Override
    protected IFitnessFunction getNewFitnessFunction() {
        return fitnessFunction;
    }

    @Override
    public String getName() {
        return "LensProblem";
    }

    @Override
    public IProblemDomain getRootDomain() {
        double[] lowerBound = new double[]{
                lensPosX.getMin(), lensPosY.getMin(), lensPosZ.getMin(),
                lensRadius.getMin(), lensHeight.getMin()
        };
        double[] upperBound = new double[]{
                lensPosX.getMax(), lensPosY.getMax(), lensPosZ.getMax(),
                lensRadius.getMax(), lensHeight.getMax()
        };

        assert lowerBound.length == upperBound.length;

        return new RealDomain(lowerBound.length * numLenses,
                              Utils.duplicateDoubleArray(lowerBound, numLenses),
                              Utils.duplicateDoubleArray(upperBound, numLenses));
    }

    @Override
    public Class getProblemAnnotation() {
        return RealProblem.class;
    }

    public static double scale(double val, Range<Double> range) {
        return val * (range.getMax() - range.getMin()) + range.getMin();
    }

    public static void main(String[] args) throws Exception {
        boolean visualize = false;

        if (args.length > 0 && args[0].equals("show")) {
            visualize = true;
            args = Arrays.copyOfRange(args, 1, args.length);
        }

        JCommander commander = new JCommander(LensProblem.class);
        commander.parse(args);
        
        if (visualize) {
            visualize(args.length > 1 ? args[1] : "defaultSessionName.aggregate1.txt");
        } else {
            org.jage.core.AgENode.main(new String[]{});
        }
    }

    private static class Result {
        public List<Double> parameters = new ArrayList<>();
        public double fitness;
    }

    private static Result parseResultLine(String line) {
        Scanner scanner = new Scanner(line.replaceAll("[^ .0-9]", ""));
        Result result = new Result();

        scanner.nextDouble();
        scanner.nextDouble();

        while (scanner.hasNextDouble()) {
            result.parameters.add(scanner.nextDouble());
        }

        int lastIndex = result.parameters.size() - 1;
        result.fitness = result.parameters.get(lastIndex);
        result.parameters.remove(lastIndex);

        return result;
    }

    private static void visualize(String inputFile) throws IOException, InvalidArgumentException {
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        Result bestResult = new Result();
        String line;

        while ((line = reader.readLine()) != null) {
            Result result = parseResultLine(line);
            if (result.fitness > bestResult.fitness) {
                bestResult = result;
            }
        }

        System.err.println("fitness = " + bestResult.fitness);
        List<Lens> lenses = new ArrayList<>();

        for (int i = 0; i < bestResult.parameters.size(); i += 5) {
            Vector3D scaledLensPos = new Vector3D(bestResult.parameters.get(i + 0),
                                                  bestResult.parameters.get(i + 1),
                                                  bestResult.parameters.get(i + 2));
            double scaledLensRadius = bestResult.parameters.get(i + 3);
            double scaledLensHeight = bestResult.parameters.get(i + 4);

            Lens lens = new Lens(new Vector3D(scale(scaledLensPos.getX(), lensPosX),
                                              scale(scaledLensPos.getY(), lensPosY),
                                              scale(scaledLensPos.getZ(), lensPosZ)),
                                 scale(scaledLensRadius, lensRadius),
                                 scale(scaledLensHeight, lensHeight));
            lenses.add(lens);
        }
        LensSimulation sim = new LensSimulation(lenses);

        List<List<Ray>> paths = new ArrayList<>();
        for (PhiTheta phiTheta : IMAGES) {
            sim.simulate(new Ray(Vector3D.ZERO, phiTheta.toPosition()));
            paths.add(sim.getRayPath());
            System.err.println(paths.toString());
        }

        SimulationVisualizer.show(sim, paths, LIGHT_SOURCE);
    }
}
