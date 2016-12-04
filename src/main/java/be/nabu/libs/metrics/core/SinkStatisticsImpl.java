package be.nabu.libs.metrics.core;

import java.util.List;

import be.nabu.libs.metrics.core.api.Deviation;
import be.nabu.libs.metrics.core.api.SinkStatistics;
import be.nabu.libs.metrics.core.api.SinkValue;

public class SinkStatisticsImpl implements SinkStatistics {

	private SinkValue min, max;
	private double exponentialAverage, cumulativeAverage;
	private List<Deviation> cumulativeAverageDeviation;
	private long amountOfDataPoints;
	
	public SinkStatisticsImpl() {
		// auto construct
	}
	public SinkStatisticsImpl(SinkStatistics snapshot) {
		this.max = snapshot.getMaximum();
		this.min = snapshot.getMinimum();
		this.exponentialAverage = snapshot.getExponentialAverage();
		this.cumulativeAverage = snapshot.getCumulativeAverage();
		this.cumulativeAverageDeviation = snapshot.getCumulativeAverageDeviation();
		this.amountOfDataPoints = snapshot.getAmountOfDataPoints();
	}
	
	@Override
	public SinkValue getMinimum() {
		return min;
	}
	public void setMinimum(SinkValue min) {
		this.min = min;
	}

	@Override
	public SinkValue getMaximum() {
		return max;
	}
	public void setMaximum(SinkValue max) {
		this.max = max;
	}

	@Override
	public double getExponentialAverage() {
		return exponentialAverage;
	}
	public void setExponentialAverage(double exponentialAverage) {
		this.exponentialAverage = exponentialAverage;
	}

	@Override
	public double getCumulativeAverage() {
		return cumulativeAverage;
	}
	public void setCumulativeAverage(double cumulativeAverage) {
		this.cumulativeAverage = cumulativeAverage;
	}

	@Override
	public List<Deviation> getCumulativeAverageDeviation() {
		return cumulativeAverageDeviation;
	}
	public void setCumulativeAverageDeviation(List<Deviation> cumulativeAverageDeviation) {
		this.cumulativeAverageDeviation = cumulativeAverageDeviation;
	}
	@Override
	public long getAmountOfDataPoints() {
		return amountOfDataPoints;
	}
	public void setAmountOfDataPoints(long amountOfDataPoints) {
		this.amountOfDataPoints = amountOfDataPoints;
	}
}
