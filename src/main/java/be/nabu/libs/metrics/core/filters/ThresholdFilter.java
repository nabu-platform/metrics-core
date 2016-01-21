package be.nabu.libs.metrics.core.filters;

import be.nabu.libs.events.api.EventHandler;
import be.nabu.libs.metrics.core.api.SinkEvent;

public class ThresholdFilter implements EventHandler<SinkEvent, Boolean> {

	private boolean above;
	private long threshold;

	public ThresholdFilter(long threshold, boolean above) {
		this.threshold = threshold;
		this.above = above;
	}
	
	@Override
	public Boolean handle(SinkEvent event) {
		if (above && event.getValue() > threshold) {
			return false;
		}
		else if (!above && event.getValue() < threshold) {
			return false;
		}
		return true;
	}

}
