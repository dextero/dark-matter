package stochastic.hgs;

import com.beust.jcommander.IStringConverter;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Vector3DConverter implements IStringConverter<Vector3D> {
    @Override
    public Vector3D convert(String s) {
        String[] words = s.split(";");
        assert words.length == 3;
        return new Vector3D(Double.parseDouble(words[0]),
                            Double.parseDouble(words[1]),
                            Double.parseDouble(words[2]));
    }
}
