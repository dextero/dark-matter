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
        if (args.length > 0 && args[0].equals("show")) {
            visualize();
        } else {
            org.jage.core.AgENode.main(args);
        }
    }

    private static void visualize() throws IOException, InvalidArgumentException {
        BufferedReader reader = new BufferedReader(new FileReader("defaultSessionName.aggregate1.txt"));
        String lastLine = "";
        String line;
        while ((line = reader.readLine()) != null) {
            lastLine = line;
        }

        lastLine = lastLine.replaceAll("[^ .0-9]", "");
        System.err.println(lastLine);
        Scanner scanner = new Scanner(lastLine);

        scanner.nextDouble();
        scanner.nextDouble();

        Vector3D scaledLensPos = new Vector3D(scanner.nextDouble(),
                                              scanner.nextDouble(),
                                              scanner.nextDouble());
        double scaledLensRadius = scanner.nextDouble();
        double scaledLensHeight = scanner.nextDouble();

        Lens lens = new Lens(new Vector3D(scale(scaledLensPos.getX(), lensPosX),
                                          scale(scaledLensPos.getY(), lensPosY),
                                          scale(scaledLensPos.getZ(), lensPosZ)),
                             scale(scaledLensRadius, lensRadius),
                             scale(scaledLensHeight, lensHeight));
        System.err.println(lens);
        LensSimulation sim = new LensSimulation(Arrays.asList(lens));

        List<List<Ray>> paths = new ArrayList<>();
        for (PhiTheta phiTheta : IMAGES) {
            sim.simulate(new Ray(Vector3D.ZERO, phiTheta.toPosition()));
            paths.add(sim.getRayPath());
            System.err.println(paths.toString());
        }

        SimulationVisualizer.show(sim, paths, LIGHT_SOURCE);
    }
}
