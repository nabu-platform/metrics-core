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

import java.util.HashMap;
import java.util.Map;

import be.nabu.libs.events.api.EventHandler;
import be.nabu.libs.metrics.core.api.Sink;
import be.nabu.libs.metrics.core.api.SinkEvent;
import be.nabu.libs.metrics.core.api.SinkValue;
import be.nabu.libs.metrics.core.sinks.EMASink;

/**
 * Allows you to set a threshold filter on the exponential moving average of a sink
 */
public class EMAThresholdFilter implements EventHandler<SinkEvent, Boolean> {

	private Map<Sink, EMASink> emas = new HashMap<Sink, EMASink>();
	private int windowSize;
	private ThresholdFilter thresholdFilter;
	
	public EMAThresholdFilter(long threshold, boolean above, int windowSize) {
		this.thresholdFilter = new ThresholdFilter(threshold, above);
		this.windowSize = windowSize;
	}
	
	@Override
	public Boolean handle(final SinkEvent event) {
		if (!emas.containsKey(event.getSink())) {
			synchronized(emas) {
				if (!emas.containsKey(event.getSink())) {
					emas.put(event.getSink(), new EMASink(windowSize));
				}
			}
		}
		EMASink emaSink = emas.get(event.getSink());
		emaSink.push(event.getTimestamp(), event.getValue());
		final SinkValue current = emaSink.getCurrent();
		return thresholdFilter.handle(new SinkEvent() {
			@Override
			public long getTimestamp() {
				return current.getTimestamp();
			}
			@Override
			public long getValue() {
				return current.getValue();
			}
			@Override
			public Sink getSink() {
				return event.getSink();
			}
			@Override
			public String getCategory() {
				return event.getCategory();
			}
			@Override
			public String getId() {
				return event.getId();
			}
		});
	}

}
