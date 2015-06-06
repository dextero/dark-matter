package stochastic.hgs;

import com.beust.jcommander.converters.IParameterSplitter;

import java.util.Arrays;
import java.util.List;

public class SpaceParameterSplitter implements IParameterSplitter {
    @Override
    public List<String> split(String s) {
        return Arrays.asList(s.split(" "));
    }
}
