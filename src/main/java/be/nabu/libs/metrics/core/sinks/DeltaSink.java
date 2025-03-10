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

package be.nabu.libs.metrics.core.sinks;

import java.util.concurrent.atomic.AtomicLong;

import be.nabu.libs.metrics.core.MetricUtils;
import be.nabu.libs.metrics.core.api.AutomatedWindowSink;
import be.nabu.libs.metrics.core.api.Sink;

/**
 * This maintains a current value and allows you to send incremental delta's instead of full values 
 */
public class DeltaSink implements Sink, AutomatedWindowSink {

	protected long windowInterval;
	protected long windowStart, windowStop;
	
	private AtomicLong value;
	private Sink parent;
	
	public DeltaSink(Sink parent) {
		this.parent = parent;
		value = new AtomicLong(0);
		windowStart = MetricUtils.getNearestWindowStart(windowInterval);
		windowStop = windowStart;
	}
	
	@Override
	public void push(long timestamp, long value) {
		if (windowInterval > 0 && timestamp > windowStart + windowInterval) {
			reset(Math.max(windowStart + windowInterval, MetricUtils.getNearestWindowStart(windowInterval)));
		}
		// the current window stops at the last timestamp
		windowStop = timestamp;
		
		parent.push(timestamp, this.value.addAndGet(value));
	}
	
	protected void reset(long windowStart) {
		// reset to 0
		value.set(0);
		this.windowStart = windowStart;
	}

	@Override
	public long getWindowStart() {
		return windowStart;
	}

	@Override
	public long getWindowStop() {
		return windowStop;
	}

	@Override
	public void setWindowInterval(long milliseconds) {
		this.windowInterval = milliseconds;
	}
	
	@Override
	public long getWindowInterval() {
		return windowInterval;
	}

}
