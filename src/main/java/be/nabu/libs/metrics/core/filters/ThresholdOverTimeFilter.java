package be.nabu.libs.metrics.core.filters;

import java.util.Date;

import be.nabu.libs.events.api.EventHandler;
import be.nabu.libs.metrics.core.api.HistorySink;
import be.nabu.libs.metrics.core.api.SinkEvent;
import be.nabu.libs.metrics.core.api.SinkSnapshot;
import be.nabu.libs.metrics.core.api.SinkValue;

public class ThresholdOverTimeFilter implements EventHandler<SinkEvent, Boolean> {

	private boolean above;
	private long threshold;
	private long duration;

	public ThresholdOverTimeFilter(long threshold, boolean above, long duration) {
		this.duration = duration;
		this.threshold = threshold;
		this.above = above;
	}
	
	@Override
	public Boolean handle(SinkEvent event) {
		// we always filter the event _unless_ the delta over time reaches the threshold
		if (event.getSink() instanceof HistorySink) {
			long time = new Date().getTime();
			SinkSnapshot snapshot = ((HistorySink) event.getSink()).getSnapshotBetween(time - duration, time);
			if (!snapshot.getValues().isEmpty()) {
				SinkValue sinkValue = snapshot.getValues().get(0);
				long delta = event.getValue() - sinkValue.getValue();
				if (above && delta > threshold) {
					return false;
				}
				else if (!above && delta < threshold) {
					return false;
				}
			}
		}
		return true;
	}

}
