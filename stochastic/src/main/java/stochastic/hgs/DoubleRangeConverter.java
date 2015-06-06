package stochastic.hgs;

import com.beust.jcommander.IStringConverter;
import stochastic.Range;

public class DoubleRangeConverter implements IStringConverter<Range<Double>> {
    @Override
    public Range<Double> convert(String s) {
        String[] words = s.split(";");
        assert words.length == 2;

        return new Range<>(Double.parseDouble(words[0]), Double.parseDouble(words[1]));
    }
}
