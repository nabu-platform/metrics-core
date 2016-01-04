package be.nabu.libs.metrics.core.sinks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

import be.nabu.libs.metrics.core.SinkSnapshotImpl;
import be.nabu.libs.metrics.core.SinkValueImpl;
import be.nabu.libs.metrics.core.api.HistorySink;
import be.nabu.libs.metrics.core.api.SinkSnapshot;
import be.nabu.libs.metrics.core.api.SinkValue;

public class LimitedHistorySink implements HistorySink {
	
	protected AtomicLong counter;
	protected AtomicLongArray timestamps, values;
	protected int size;
	
	public LimitedHistorySink(int size) {
		this.size = size;
		counter = new AtomicLong(0);
		timestamps = new AtomicLongArray(size);
		values = new AtomicLongArray(size);
	}
	
	@Override
	public void push(long timestamp, long value) {
		long index = counter.getAndIncrement();
		timestamps.lazySet((int) (index % size), timestamp);
		values.lazySet((int) (index % size), value);
	}

	@Override
	public SinkSnapshot getSnapshot(int amount) {
		// we do a getAndIncrement, that means the current counter is one ahead of what we have last written
		long index = counter.get() - 1;
		List<SinkValue> values = new ArrayList<SinkValue>();
		for (int i = 0; i < Math.min(size, amount); i++) {
			if (index < 0) {
				break;
			}
			values.add(new SinkValueImpl(this.timestamps.get((int) ((index - i) % size)), this.values.get((int) ((index - i) % size))));
		}
		return new SinkSnapshotImpl(values);
	}

	@Override
	public SinkSnapshot getSnapshotAfter(long after) {
		// we do a getAndIncrement, that means the current counter is one ahead of what we have last written
		long index = counter.get() - 1;
		List<SinkValue> values = new ArrayList<SinkValue>();
		// note that this implementation is susceptible to wrap-around but because we are doing a time-based check, you will simply get new values
		for (int i = 0; i < size; i++) {
			if (index < 0) {
				break;
			}
			SinkValueImpl value = new SinkValueImpl(this.timestamps.get((int) ((index - i) % size)), this.values.get((int) ((index - i) % size)));
			// only add the value if the timestamp is in the window
			// otherwise we still continue because the insertions may not be chronologically (due to parallelism)
			if (value.getTimestamp() > after) {
				values.add(value);
			}
		}
		return new SinkSnapshotImpl(values);
	}
}
