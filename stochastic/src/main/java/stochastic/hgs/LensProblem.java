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
import stochastic.Utils;

import java.util.Arrays;
import java.util.List;

public class LensProblem extends AbstractProblem {
    LensProblemFitnessFunction fitnessFunction;

    private static final double[] PARAMETER_LOWER_BOUNDS = new double[] { -1.0, -1.0, 1.0, 0.8, 0.1 };
    private static final double[] PARAMETER_UPPER_BOUNDS = new double[] { 1.0, 1.0, 9.0, 1.5, 0.5 };
    private static final Vector3D LIGHT_SOURCE = new Vector3D(0.0, 0.0, 10.0);

    private int numLenses = 2;

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
        double[] lowerBounds = Utils.duplicateDoubleArray(PARAMETER_LOWER_BOUNDS, numLenses);
        double[] upperBounds = Utils.duplicateDoubleArray(PARAMETER_UPPER_BOUNDS, numLenses);

        return new RealDomain(numLenses * PARAMETER_LOWER_BOUNDS.length, lowerBounds, upperBounds);
    }

    @Override
    public Class getProblemAnnotation() {
        return RealProblem.class;
    }
}
