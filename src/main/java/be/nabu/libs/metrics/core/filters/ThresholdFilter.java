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

import be.nabu.libs.events.api.EventHandler;
import be.nabu.libs.metrics.core.api.SinkEvent;

/**
 * Simply trigger if an event goes over a certain threshold 
 */
public class ThresholdFilter implements EventHandler<SinkEvent, Boolean> {

	private boolean above;
	private long threshold;
	private long interval, last;

	public ThresholdFilter(long threshold, boolean above) {
		this.threshold = threshold;
		this.above = above;
	}
	
	public ThresholdFilter(long threshold, boolean above, long interval) {
		this.threshold = threshold;
		this.above = above;
		this.interval = interval;
	}
	
	@Override
	public Boolean handle(SinkEvent event) {
		if ((above && event.getValue() > threshold) || (!above && event.getValue() < threshold)) {
			// if we haven't notified yet, let it through, otherwise we check the interval
			// if the interval is negative, we only want to be alerted once, ever (until the last resets at least)
			if (last == 0 || (interval >= 0 && last < event.getTimestamp() - interval)) {
				last = event.getTimestamp();
				return false;
			}
			else {
				return true;
			}
		}
		// reset last
		last = 0;
		return true;
	}

}
