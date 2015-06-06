package stochastic.hgs;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
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
import java.util.stream.Collectors;

public class LensProblem extends AbstractProblem {
    LensProblemFitnessFunction fitnessFunction;

    private static class Config {
        @Parameter(names = "--show", arity = 1)
        public String resultFile = null;
        @Parameter(names = "--num-lenses", arity = 1)
        public int numLenses = 1;
        @Parameter(names = "--lens-x", arity = 1, converter = DoubleRangeConverter.class, description = "Format: 'min;max'")
        public Range<Double> lensPosX = new Range<>(-5.0, 5.0);
        @Parameter(names = "--lens-y", arity = 1, converter = DoubleRangeConverter.class, description = "Format: 'min;max'")
        public Range<Double> lensPosY = new Range<>(0.0, Utils.EPSILON);
        @Parameter(names = "--lens-z", arity = 1, converter = DoubleRangeConverter.class, description = "Format: 'min;max'")
        public Range<Double> lensPosZ = new Range<>(0.5, 0.5 + Utils.EPSILON);
        @Parameter(names = "--lens-radius", arity = 1, converter = DoubleRangeConverter.class, description = "Format: 'min;max'")
        public Range<Double> lensRadius = new Range<>(2.0, 10.0);
        @Parameter(names = "--lens-height", arity = 1, converter = DoubleRangeConverter.class, description = "Format: 'min;max'")
        public Range<Double> lensHeight = new Range<>(0.5, 1.5);
        @Parameter(names = "--light-source", arity = 1, converter = Vector3DConverter.class, description = "Format: 'x;y;z'")
        public Vector3D lightSource = new Vector3D(0.0, 0.0, 10.0);
        @Parameter(names = "--images", splitter = SpaceParameterSplitter.class, converter = PhiThetaConverter.class, description = "Format: 'phi1;theta1 phi2;theta2', both angles in degrees, phi = 0 => x+; phi = 90 => z+; theta = 90 => y+")
        public List<PhiTheta> images = Arrays.asList(new PhiTheta(Angle.fromDegrees(70.0),
                                                                  Angle.fromDegrees(0.0)));

        @Override
        public String toString() {
            return "Config:" +
                    "\n  numLenses = " + numLenses +
                    "\n  lensPosX = " + lensPosX +
                    "\n  lensPosY = " + lensPosY +
                    "\n  lensPosZ = " + lensPosZ +
                    "\n  lensRadius = " + lensRadius +
                    "\n  lensHeight = " + lensHeight +
                    "\n  lightSource = " + lightSource +
                    "\n  images:" +
                    "\n    " + String.join("\n    ", images.stream().map(PhiTheta::toString).collect(Collectors.toList())) +
                    ";";
        }
    }

    private static Config config = new Config();

    public LensProblem(IConfiguration config) throws ConfigurationException {
        super(config);

        fitnessFunction = new LensProblemFitnessFunction(LensProblem.config.images, LensProblem.config.lightSource);
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
                config.lensPosX.getMin(), config.lensPosY.getMin(), config.lensPosZ.getMin(),
                config.lensRadius.getMin(), config.lensHeight.getMin()
        };
        double[] upperBound = new double[]{
                config.lensPosX.getMax(), config.lensPosY.getMax(), config.lensPosZ.getMax(),
                config.lensRadius.getMax(), config.lensHeight.getMax()
        };

        assert lowerBound.length == upperBound.length;

        return new RealDomain(lowerBound.length * config.numLenses,
                              Utils.duplicateDoubleArray(lowerBound, config.numLenses),
                              Utils.duplicateDoubleArray(upperBound, config.numLenses));
    }

    @Override
    public Class getProblemAnnotation() {
        return RealProblem.class;
    }

    public static double scale(double val, Range<Double> range) {
        return val * (range.getMax() - range.getMin()) + range.getMin();
    }

    public static void main(String[] args) throws Exception {
        JCommander commander = new JCommander(config);
        try {
            commander.parse(args);
        } catch (ParameterException e) {
            commander.usage();
            return;
        }

        System.err.println(config);

        if (config.resultFile != null) {
            visualize(config.resultFile);
        } else {
            org.jage.core.AgENode.main(new String[]{});
        }
    }

    private static class Result {
        public List<Double> parameters = new ArrayList<>();
        public double fitness = -1.0;
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
                System.err.println("found result with fitness = " + result.fitness);
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

            Lens lens = new Lens(new Vector3D(scale(scaledLensPos.getX(), config.lensPosX),
                                              scale(scaledLensPos.getY(), config.lensPosY),
                                              scale(scaledLensPos.getZ(), config.lensPosZ)),
                                 scale(scaledLensRadius, config.lensRadius),
                                 scale(scaledLensHeight, config.lensHeight));
            lenses.add(lens);
            System.err.println(lens);
        }
        LensSimulation sim = new LensSimulation(lenses);

        List<List<Ray>> paths = new ArrayList<>();
        for (PhiTheta phiTheta : config.images) {
            sim.simulate(new Ray(Vector3D.ZERO, phiTheta.toPosition()));
            paths.add(sim.getRayPath());
            System.err.println(paths.toString());
        }

        SimulationVisualizer.show(sim, paths, config.lightSource);
    }
}
