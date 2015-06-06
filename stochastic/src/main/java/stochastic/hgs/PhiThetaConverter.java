package stochastic.hgs;

import com.beust.jcommander.IStringConverter;
import stochastic.Angle;
import stochastic.PhiTheta;

import java.util.ArrayList;
import java.util.List;

public class PhiThetaConverter implements IStringConverter<PhiTheta> {
    @Override
    public PhiTheta convert(String s) {
            String[] angles = s.split(";");
            assert angles.length == 2;
            return new PhiTheta(Angle.fromDegrees(Double.parseDouble(angles[0])),
                                Angle.fromDegrees(Double.parseDouble(angles[1])));
    }
}
