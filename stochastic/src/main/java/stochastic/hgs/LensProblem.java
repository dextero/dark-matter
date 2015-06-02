package stochastic.hgs;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.jage.app.hgs.problem.AbstractProblem;
import org.jage.app.hgs.problem.IFitnessFunction;
import org.jage.app.hgs.problem.IProblemDomain;
import org.jage.app.hgs.problem.annotations.RealProblem;
import org.jage.app.hgs.problem.real.AbstractRealProblem;
import org.jage.app.hgs.problem.real.RealDomain;
import org.jage.core.config.ConfigurationException;
import org.jage.core.config.IConfiguration;
import stochastic.Angle;
import stochastic.PhiTheta;
import stochastic.Range;
import stochastic.Utils;

import java.util.Arrays;
import java.util.List;

public class LensProblem extends AbstractProblem {
    LensProblemFitnessFunction fitnessFunction;

    private Range<Double> lensPosX = new Range<>(-5.0, 5.0);
    private Range<Double> lensPosY = new Range<>(0.0, Utils.EPSILON);
    private Range<Double> lensPosZ = new Range<>(0.5, 0.5 + Utils.EPSILON);
    private Range<Double> lensRadius = new Range<>(2.0, 10.0);
    private Range<Double> lensHeight = new Range<>(0.5, 1.5);
    private final Vector3D LIGHT_SOURCE = new Vector3D(0.0, 0.0, 10.0);
    private int numLenses = 1;

    public LensProblem(IConfiguration config) throws ConfigurationException {
        super(config);

        List<PhiTheta> images = Arrays.asList(new PhiTheta(Angle.fromDegrees(90.0), Angle.fromDegrees(1.0)),
                                              new PhiTheta(Angle.fromDegrees(90.0), Angle.fromDegrees(-1.0)));

        fitnessFunction = new LensProblemFitnessFunction(images, LIGHT_SOURCE);
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

    public static void main(String[] args) throws Exception {
        org.jage.core.AgENode.main(args);
    }
}
