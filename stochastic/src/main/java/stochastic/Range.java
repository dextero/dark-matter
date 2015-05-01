package stochastic;

/**
 * Created by dex on 01.05.15.
 */
public class Range<ValueT> {
    private ValueT min;
    private ValueT max;

    public Range(ValueT min,
                 ValueT max) {
        assert min != null;
        assert max != null;

        this.min = min;
        this.max = max;
    }

    public ValueT getMin() {
        return min;
    }

    public ValueT getMax() {
        return max;
    }
}
