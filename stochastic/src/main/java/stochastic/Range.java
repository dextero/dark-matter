package stochastic;

/**
 * Created by dex on 01.05.15.
 */
public class Range<ValueT extends Comparable<ValueT>> {
    final private ValueT min;
    final private ValueT max;

    public Range(ValueT min,
                 ValueT max) {
        assert min != null;
        assert max != null;
        assert min.compareTo(max) < 0;

        this.min = min;
        this.max = max;
    }

    public ValueT getMin() {
        return min;
    }

    public ValueT getMax() {
        return max;
    }

    @Override
    public String toString() {
        return "[" +  min + ", " + max + ")";
    }
}
