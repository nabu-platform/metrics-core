/*
* Copyright (C) 2016 Alexander Verbruggen
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package be.nabu.libs.metrics.core.filters;

import java.util.Date;

import be.nabu.libs.events.api.EventHandler;
import be.nabu.libs.metrics.core.api.HistorySink;
import be.nabu.libs.metrics.core.api.SinkEvent;
import be.nabu.libs.metrics.core.api.SinkSnapshot;
import be.nabu.libs.metrics.core.api.SinkValue;

/**
 * Suppose the event is "100" and the oldest event in the window is "50". If your threshold is 49, it will trigger (if above)
 */
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
