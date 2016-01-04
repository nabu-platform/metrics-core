package be.nabu.libs.metrics.core;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import be.nabu.libs.metrics.api.MetricGauge;
import be.nabu.libs.metrics.api.MetricInstance;
import be.nabu.libs.metrics.api.MetricTimer;
import be.nabu.libs.metrics.core.api.Sink;
import be.nabu.libs.metrics.core.api.SinkProvider;
import be.nabu.libs.metrics.core.sinks.DeltaSink;

public class MetricInstanceImpl implements MetricInstance {

	private SinkProvider sinkProvider;
	private Map<String, Sink> sinks = new HashMap<String, Sink>();
	private String id;
	private Map<String, MetricGauge> gauges = new HashMap<String, MetricGauge>();

	public MetricInstanceImpl(String id, SinkProvider sinkProvider) {
		this.id = id;
		this.sinkProvider = sinkProvider;
	}
	
	@Override
	public void duration(String category, long duration, TimeUnit timeUnit) {
		if (!sinks.containsKey(category)) {
			synchronized(sinks) {
				if (!sinks.containsKey(category)) {
					sinks.put(category, sinkProvider.newSink(id, category));
				}
			}
		}
		// normalize because we always expect the start of the timestamp to be there
		sinks.get(category).push(new Date().getTime() - timeUnit.toMillis(duration), duration);
	}

	@Override
	public MetricTimer start(String category) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void increment(String category, long amount) {
		if (!sinks.containsKey(category)) {
			synchronized(sinks) {
				if (!sinks.containsKey(category)) {
					sinks.put(category, new DeltaSink(sinkProvider.newSink(category, category)));
				}
			}
		}
		sinks.get(category).push(new Date().getTime(), amount);
	}

	@Override
	public void log(String category, long value) {
		if (!sinks.containsKey(category)) {
			synchronized(sinks) {
				if (!sinks.containsKey(category)) {
					sinks.put(category, sinkProvider.newSink(id, category));
				}
			}
		}
		sinks.get(category).push(new Date().getTime(), value);
	}

	@Override
	public void set(String category, final long value) {
		if (!gauges.containsKey(category)) {
			synchronized(gauges) {
				if (!gauges.containsKey(category)) {
					gauges.put(category, new MetricGaugeImpl(value));
				}
			}
		}
		((MetricGaugeImpl) gauges.get(category)).setValue(value);
	}

	@Override
	public void set(String category, MetricGauge gauge) {
		synchronized(gauges) {
			gauges.put(category, gauge);
		}
	}

	void log(long value, long started, long duration) {
		
	}
}
