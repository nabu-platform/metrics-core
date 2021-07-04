package be.nabu.libs.metrics.core;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import be.nabu.libs.events.api.EventDispatcher;
import be.nabu.libs.metrics.api.MetricGauge;
import be.nabu.libs.metrics.api.MetricInstance;
import be.nabu.libs.metrics.api.MetricTimer;
import be.nabu.libs.metrics.core.api.Sink;
import be.nabu.libs.metrics.core.api.SinkEvent;
import be.nabu.libs.metrics.core.api.SinkProvider;
import be.nabu.libs.metrics.core.sinks.DeltaSink;

public class MetricInstanceImpl implements MetricInstance {

	private SinkProvider sinkProvider;
	private Map<String, Sink> sinks = new HashMap<String, Sink>();
	private String id;
	private Map<String, MetricGauge> gauges = new HashMap<String, MetricGauge>();
	private EventDispatcher dispatcher;
	private boolean enableEvents = true;
	
	/**
	 * By default we do not normalize the timers because default behavior should be chronologically consistent metrics
	 */
	private boolean normalizeTimers = false;
	
	/**
	 * By default we normalize the timeunit to milliseconds to allow for easy graphing
	 */
	private boolean normalizeTimeUnit = true;

	public MetricInstanceImpl(String id, SinkProvider sinkProvider, EventDispatcher dispatcher) {
		this.id = id;
		this.sinkProvider = sinkProvider;
		this.dispatcher = dispatcher;
	}
	
	public MetricInstanceImpl(String id, SinkProvider sinkProvider) {
		this(id, sinkProvider, null);
	}
	
	@Override
	public void duration(String category, long duration, TimeUnit timeUnit) {
		if (!sinks.containsKey(category)) {
			synchronized(sinks) {
				if (!sinks.containsKey(category)) {
					sinks.put(category, sinkProvider.getSink(id, category));
				}
			}
		}
		// you can opt to "normalize" the timers where the timestamp will always point to the beginning of the event while the duration is logged in its natural timeunit
		if (normalizeTimers) {
			pushToSink(category, new Date().getTime() - timeUnit.toMillis(duration), normalizeTimeUnit ? timeUnit.toMillis(duration) : duration);
		}
		// however in a lot of cases we do NOT substract want to perform this normalization because it makes the metrics chronologically inconsistent
		// normalizing allows the data to "jump back in time" from the point it was reported, especially for overlapping durations (e.g. multiple simultaneous service executions etc) this can mess up the timeline
		// in such cases it is better to allow (visually speaking) the user to toggle a value as a duration to shift it at a later time, this at least keeps the metrics chronologically intact
		else {
			pushToSink(category, new Date().getTime(), normalizeTimeUnit ? timeUnit.toMillis(duration) : duration);
		}
	}
	
	private void pushToSink(final String category, final long timestamp, final long value) {
		sinks.get(category).push(timestamp, value);
		if (dispatcher != null && enableEvents) {
			dispatcher.fire(new SinkEvent() {
				@Override
				public long getValue() {
					return value;
				}
				@Override
				public long getTimestamp() {
					return timestamp;
				}
				@Override
				public Sink getSink() {
					return sinks.get(category);
				}
				@Override
				public String getCategory() {
					return category;
				}
				@Override
				public String getId() {
					return id;
				}
			}, this);
		}
	}

	@Override
	public MetricTimer start(String category) {
		return new TimerImpl(this, category);
	}

	@Override
	public void increment(String category, long amount) {
		if (!sinks.containsKey(category)) {
			synchronized(sinks) {
				if (!sinks.containsKey(category)) {
					sinks.put(category, new DeltaSink(sinkProvider.getSink(category, category)));
				}
			}
		}
		pushToSink(category, new Date().getTime(), amount);
	}

	@Override
	public void log(String category, long value) {
		if (!sinks.containsKey(category)) {
			synchronized(sinks) {
				if (!sinks.containsKey(category)) {
					sinks.put(category, sinkProvider.getSink(id, category));
				}
			}
		}
		pushToSink(category, new Date().getTime(), value);
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
	
	public Collection<String> getGaugeIds() {
		return gauges.keySet();
	}
	
	public Collection<String> getSinkIds() {
		return sinks.keySet();
	}
	
	public MetricGauge getGauge(String id) {
		return gauges.get(id);
	}
	
	public Sink getSink(String id) {
		return sinks.get(id);
	}

	public EventDispatcher getDispatcher() {
		return dispatcher;
	}
	public void setDispatcher(EventDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public boolean isEnableEvents() {
		return enableEvents;
	}

	public void setEnableEvents(boolean enableEvents) {
		this.enableEvents = enableEvents;
	}
}
