package be.nabu.libs.metrics.core.sinks;

import java.util.ArrayList;
import java.util.List;

import be.nabu.libs.metrics.core.DeviationImpl;
import be.nabu.libs.metrics.core.SinkValueImpl;
import be.nabu.libs.metrics.core.api.Deviation;
import be.nabu.libs.metrics.core.api.Sink;
import be.nabu.libs.metrics.core.api.SinkStatistics;
import be.nabu.libs.metrics.core.api.SinkValue;

public class StatisticsSink implements SinkStatistics, Sink {

	private SinkValue min, max;
	private EMASink ema;
	private CMASink cma;
	private long totalValues;
	
	private double maxDeviation;
	private double [] deviations;
	private long [] cmaDeviation;
	
	public StatisticsSink() {
		this(100);
	}
	
	public StatisticsSink(int emaWindow, double...deviations) {
		this.ema = new EMASink(emaWindow);
		this.cma = new CMASink();
		this.deviations = deviations == null || deviations.length == 0 ? new double [] { 0.25, 0.50, 0.75 } : deviations;
		this.cmaDeviation = new long[this.deviations.length];
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
		totalValues++;
		for (int i = 0; i < deviations.length; i++) {
			if (isWithinDeviation(value, deviations[i])) {
				cmaDeviation[i]++;
				break;
			}
		}
		// keep track of the maximum deviation from the cma to be able to generate a correct remainder overview
		double currentDeviation = cma.getValue() == 0 ? 0 : Math.abs((double) value / cma.getValue());
		if (currentDeviation > maxDeviation) {
			maxDeviation = currentDeviation;
		}
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
		long remaining = totalValues;
		for (int i = 0; i < deviations.length; i++) {
			remaining -= cmaDeviation[i];
			result.add(new DeviationImpl(deviations[i], totalValues == 0 ? 0 : (double) cmaDeviation[i] / (double) totalValues));
		}
		// put the "remainder" in the max deviation
		result.add(new DeviationImpl(maxDeviation, totalValues == 0 ? 0 : (double) remaining / (double) totalValues));
		return result;
	}

}
