package be.nabu.libs.metrics.core.filters;

import java.util.Date;

import be.nabu.libs.events.api.EventHandler;
import be.nabu.libs.metrics.core.api.HistorySink;
import be.nabu.libs.metrics.core.api.SinkEvent;
import be.nabu.libs.metrics.core.api.SinkSnapshot;
import be.nabu.libs.metrics.core.api.SinkValue;

public class SustainedThresholdFilter implements EventHandler<SinkEvent, Boolean> {

	private long duration;
	private long threshold;
	private boolean above;

	public SustainedThresholdFilter(long threshold, boolean above, long duration) {
		this.duration = duration;
		this.threshold = threshold;
		this.above = above;
	}
	
	@Override
	public Boolean handle(SinkEvent event) {
		if (event.getSink() instanceof HistorySink) {
			SinkSnapshot snapshot = ((HistorySink) event.getSink()).getSnapshotAfter(new Date().getTime() - duration);
			if (snapshot.getValues().isEmpty()) {
				return true;
			}
			// check all the values
			for (SinkValue value : snapshot.getValues()) {
				if (above && value.getValue() <= threshold) {
					return true;
				}
				else if (!above && value.getValue() >= threshold) {
					return true;
				}
			}
			// if all values match the criteria, don't filter
			return false;
		}
		return true;
	}

}
