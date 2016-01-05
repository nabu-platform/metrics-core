package be.nabu.libs.metrics.core;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import be.nabu.libs.metrics.api.MetricInstance;
import be.nabu.libs.metrics.api.MetricTimer;

public class TimerImpl implements MetricTimer {

	private long started;
	private MetricInstanceImpl metrics;
	private String category;
	
	TimerImpl(MetricInstanceImpl metrics, String category) {
		this.metrics = metrics;
		this.category = category;
		this.started = new Date().getTime();
	}
	
	@Override
	public long stop() {
		long duration = new Date().getTime() - started;
		getMetrics().duration(category, duration, getTimeUnit());
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
