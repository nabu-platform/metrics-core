package be.nabu.libs.metrics.core.sinks;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import be.nabu.libs.metrics.core.DeviationImpl;
import be.nabu.libs.metrics.core.SinkValueImpl;
import be.nabu.libs.metrics.core.api.Deviation;
import be.nabu.libs.metrics.core.api.Sink;
import be.nabu.libs.metrics.core.api.SinkStatistics;
import be.nabu.libs.metrics.core.api.SinkValue;
import be.nabu.libs.metrics.core.api.WindowedSink;

public class StatisticsSink implements SinkStatistics, Sink, WindowedSink {

	private SinkValue min, max;
	private EMASink ema;
	private CMASink cma;
	private long windowStart, windowStop;
	private AtomicLong totalValues = new AtomicLong();
	
	// double
	private AtomicLong maxDeviation = new AtomicLong();
	// doubles
	private double [] deviations;
	private AtomicLong [] cmaDeviation;
	
	public StatisticsSink() {
		this(100);
	}
	
	public StatisticsSink(int emaWindow, double...deviations) {
		this.ema = new EMASink(emaWindow);
		this.cma = new CMASink();
		this.deviations = deviations == null || deviations.length == 0 ? new double [] { 0.25, 0.50, 0.75 } : deviations;
		this.cmaDeviation = new AtomicLong[this.deviations.length];
		for (int i = 0; i < this.deviations.length; i++) {
			this.cmaDeviation[i] = new AtomicLong();
		}
		windowStart = new Date().getTime();
		windowStop = windowStart;
	}
	
	@Override
	public void push(long timestamp, long value) {
		if (min == null || value < min.getValue()) {
			min = new SinkValueImpl(timestamp, value);
		}
		if (max == null || value > max.getValue()) {
			max = new SinkValueImpl(timestamp, value);
		}
		ema.push(timestamp, value);
		cma.push(timestamp, value);
		totalValues.incrementAndGet();
		for (int i = 0; i < deviations.length; i++) {
			if (isWithinDeviation(value, deviations[i])) {
				cmaDeviation[i].incrementAndGet();
				break;
			}
		}
		// keep track of the maximum deviation from the cma to be able to generate a correct remainder overview
		double currentDeviation = cma.getValue() == 0 ? 0 : Math.abs((double) value / cma.getValue());
		double currentMaxDeviation = maxDeviation.get() == 0 ? 0 : Double.longBitsToDouble(maxDeviation.get());
		if (currentDeviation > currentMaxDeviation) {
			maxDeviation.set(Double.doubleToLongBits(currentDeviation));
		}
		windowStop = timestamp;
	}
	
	private boolean isWithinDeviation(long value, double deviationPercentage) {
		double average = cma.getValue();
		double deviation = average * deviationPercentage;
		return value > average - deviation && value < average + deviation;
	}

	@Override
	public SinkValue getMinimum() {
		return min;
	}

	@Override
	public SinkValue getMaximum() {
		return max;
	}
	
	@Override
	public double getCumulativeAverage() {
		return cma.getValue();
	}

	@Override
	public double getExponentialAverage() {
		return ema.getValue();
	}

	@Override
	public List<Deviation> getCumulativeAverageDeviation() {
		List<Deviation> result = new ArrayList<Deviation>();
		long remaining = totalValues.get();
		for (int i = 0; i < deviations.length; i++) {
			remaining -= cmaDeviation[i].get();
			result.add(new DeviationImpl(deviations[i], totalValues.get() == 0 ? 0 : cmaDeviation[i].get() / (double) totalValues.get()));
		}
		// put the "remainder" in the max deviation
		result.add(new DeviationImpl(Double.longBitsToDouble(maxDeviation.get()), totalValues.get() == 0 ? 0 : (double) remaining / (double) totalValues.get()));
		return result;
	}

	@Override
	public long getAmountOfDataPoints() {
		return totalValues.get();
	}

	@Override
	public long getWindowStart() {
		return windowStart;
	}

	@Override
	public long getWindowStop() {
		return windowStop;
	}

	// allow changing the window for reporting reasons
	// e.g. the actual window is 2-6 but for standardization (like connecting windows) you want 0-10
	public void setWindowStart(long windowStart) {
		this.windowStart = windowStart;
	}

	public void setWindowStop(long windowStop) {
		this.windowStop = windowStop;
	}
	
}
