package be.nabu.libs.metrics.core;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import be.nabu.libs.metrics.api.MetricInstance;
import be.nabu.libs.metrics.api.MetricTimer;

public class TimerImpl implements MetricTimer {

	private long started;
	private MetricInstanceImpl metrics;
	
	TimerImpl(MetricInstanceImpl metrics) {
		this.metrics = metrics;
		started = new Date().getTime();
	}
	
	@Override
	public long stop() {
		long duration = new Date().getTime() - started;
		return duration;
	}

	@Override
	public MetricInstance getMetrics() {
		return metrics;
	}

	@Override
	public TimeUnit getTimeUnit() {
		return TimeUnit.MILLISECONDS;
	}
}
