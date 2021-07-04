package be.nabu.libs.metrics.core.filters;

import java.util.Date;
import java.util.List;

import be.nabu.libs.events.api.EventHandler;
import be.nabu.libs.metrics.core.api.HistorySink;
import be.nabu.libs.metrics.core.api.SinkEvent;
import be.nabu.libs.metrics.core.api.SinkSnapshot;
import be.nabu.libs.metrics.core.api.SinkValue;

/**
 * If a threshold has been defined and it is consistently over or under that threshold for a particular duration, we let an event through
 * Otherwise, we filter the event
 */
public class SustainedThresholdFilter implements EventHandler<SinkEvent, Boolean> {

	// you can choose either duration or amount
	private int amount;
	private long duration;
	private long threshold;
	private boolean above;
	// the percentage of variation allowed
	private double variation;
	// the minimum amount of events we need
	// otherwise, when starting up a new filter, we might instantly hit our sustained threshold because there is only a single value
	// especially because the variation is percentage-based, that won't help you.
	private int minimumEvents;
	// you can set an interval when it should be pushed, you don't want repeat notifications if it stays sustained
	// an interval of 0 is effectively no interval, a negative interval is never repeat
	private long last, interval;

	public SustainedThresholdFilter(long threshold, boolean above, long duration) {
		this.duration = duration;
		this.threshold = threshold;
		this.above = above;
	}
	
	public SustainedThresholdFilter(long threshold, boolean above, long duration, double variation, int minimumEvents, long interval) {
		this.duration = duration;
		this.threshold = threshold;
		this.above = above;
		this.variation = variation;
		this.minimumEvents = minimumEvents;
		this.interval = interval;
	}
	
	public SustainedThresholdFilter(long threshold, boolean above, int amount, double variation, int minimumEvents, long interval) {
		this.amount = amount;
		this.threshold = threshold;
		this.above = above;
		this.variation = variation;
		this.minimumEvents = minimumEvents;
		this.interval = interval;
	}
	
	@Override
	public Boolean handle(SinkEvent event) {
		if (event.getSink() instanceof HistorySink) {
			// the amount of events that do not match the criteria, we make it a double to get better division
			double varied = 0;
			long time = new Date().getTime();
			SinkSnapshot snapshot = amount == 0
				? ((HistorySink) event.getSink()).getSnapshotBetween(time - duration, time)
				: ((HistorySink) event.getSink()).getSnapshotUntil(amount, time);
			List<SinkValue> values = snapshot.getValues();
			// if there are no values, nothing is sustained!
			if (values.isEmpty()) {
				last = 0;
				return true;
			}
			// not enough events to form a conclusion
			else if (minimumEvents > 0 && values.size() < minimumEvents) {
				last = 0;
				return true;
			}
			// check all the values to see how many "deviated" from the required threshold
			for (SinkValue value : values) {
				// check if we are below the required threshold, if there is no variation allowed, that means we instantly filter the event as it is not sustained!
				if (above && value.getValue() <= threshold) {
					varied++;
				}
				else if (!above && value.getValue() >= threshold) {
					varied++;
				}
				// if we have no variation, any deviation from the threshold ends up filtering the event
				if (varied > 0 && variation == 0) {
					last = 0;
					return true;
				}
				// if we allow for some variation, check if we are over the allowed variation, if so, filter the event
				// basically suppose we say the threshold is 75 and the variation is 20% (so 0.2), if we have data [75, 76, 50, 80, 85] with 20%, we can allow one value to not meet the threshold, which is the case here
				// at that point we still want the event to go through
				else if (varied > 0 && varied / values.size() >= variation) {
					last = 0;
					return true;
				}
			}
			// if all values match the criteria, don't filter
			// note that we want to keep track of the interval and the last notification
			if (interval >= 0 && last < event.getTimestamp() - interval) {
				last = event.getTimestamp();
				return false;
			}
			else {
				return true;
			}
		}
		return true;
	}

}
