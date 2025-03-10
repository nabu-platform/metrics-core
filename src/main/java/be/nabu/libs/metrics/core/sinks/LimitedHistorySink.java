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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

import be.nabu.libs.metrics.core.MetricUtils;
import be.nabu.libs.metrics.core.SinkSnapshotImpl;
import be.nabu.libs.metrics.core.SinkValueImpl;
import be.nabu.libs.metrics.core.api.AutomatedWindowSink;
import be.nabu.libs.metrics.core.api.CurrentValueSink;
import be.nabu.libs.metrics.core.api.HistorySink;
import be.nabu.libs.metrics.core.api.ResettableSink;
import be.nabu.libs.metrics.core.api.SinkSnapshot;
import be.nabu.libs.metrics.core.api.SinkValue;
import be.nabu.libs.metrics.core.api.TaggableSink;

public class LimitedHistorySink implements HistorySink, CurrentValueSink, TaggableSink, AutomatedWindowSink, ResettableSink {
	// how long should the window interval take if set?
	protected long windowInterval;
	protected long windowStart, windowStop;
	protected AtomicLong counter;
	protected AtomicLongArray timestamps, values;
	protected int size;
	protected Map<String, String> tags = Collections.synchronizedMap(new HashMap<String, String>());
	
	public LimitedHistorySink(int size) {
		this.size = size;
		counter = new AtomicLong(0);
		timestamps = new AtomicLongArray(size);
		values = new AtomicLongArray(size);
		windowStart = MetricUtils.getNearestWindowStart(windowInterval);
		windowStop = windowStart;
	}
	
	// TODO: pushing historic data will not work correctly with the windows. we currently assume the timestamp that you push is (near) the current time, otherwise the windowing will be done wrong. We could base the windowing on the timestamp itself, but jumping back and forth would still produce weird results
	@Override
	public void push(long timestamp, long value) {
		if (windowInterval > 0 && timestamp > windowStart + windowInterval) {
			// we want the windows to be sequential if we have configured an interval
			if (windowInterval > 0) {
				// the nearest window start should (almost?) always be accurate, the math.max is just to make sure when migrating from old code
				reset(Math.max(windowStart + windowInterval, MetricUtils.getNearestWindowStart(windowInterval)));
			}
			else {
				reset();
			}
		}
		// the current window stops at the last timestamp
		windowStop = timestamp;
		long index = counter.getAndIncrement();
		timestamps.lazySet((int) (index % size), timestamp);
		values.lazySet((int) (index % size), value);
	}

	@Override
	public SinkSnapshot getSnapshotUntil(int amount, long until) {
		// we do a getAndIncrement, that means the current counter is one ahead of what we have last written
		long index = counter.get() - 1;
		List<SinkValue> values = new ArrayList<SinkValue>();
		// never more than the max size
		amount = Math.min(size, amount);
		// never more than the index
		amount = (int) Math.min(amount, index + 1);
		int counter = 0;
		while(values.size() < amount && counter <= index) {
			long timestamp = this.timestamps.get((int) ((index - counter) % size));
			if (timestamp <= until) {
				values.add(new SinkValueImpl(timestamp, this.values.get((int) ((index - counter++) % size))));
			}
		}
		return new SinkSnapshotImpl(values);
	}

	@Override
	public SinkSnapshot getSnapshotBetween(long from, long until) {
		// we do a getAndIncrement, that means the current counter is one ahead of what we have last written
		long index = counter.get() - 1;
		List<SinkValue> values = new ArrayList<SinkValue>();
		// note that this implementation is susceptible to wrap-around but because we are doing a time-based check, you will simply get new values
		for (int i = 0; i < Math.min(size, index + 1); i++) {
			long timestamp = this.timestamps.get((int) ((index - i) % size));
			// only add the value if the timestamp is in the window
			// otherwise we still continue because the insertions may not be chronologically (due to parallelism)
			if (timestamp >= from) {
				if (timestamp <= until) {
					values.add(new SinkValueImpl(timestamp, this.values.get((int) ((index - i) % size))));
				}
				else {
					break;
				}
			}
		}
		return new SinkSnapshotImpl(values);
	}

	@Override
	public SinkValue getCurrent() {
		List<SinkValue> values = getSnapshotUntil(1, new Date().getTime()).getValues();
		return values.isEmpty() ? null : values.get(0);
	}

	@Override
	public String getTag(String key) {
		return tags.get(key);
	}

	@Override
	public void setTag(String key, String value) {
		tags.put(key, value);		
	}

	@Override
	public Collection<String> getTags() {
		return tags.keySet();
	}

	@Override
	public long getWindowStart() {
		return windowStart;
	}

	@Override
	public void reset() {
		reset(new Date().getTime());
	}

	protected void reset(long windowStart) {
		counter.set(0);
		this.windowStart = windowStart;
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
