package be.nabu.libs.metrics.core.sinks;

import java.util.concurrent.atomic.AtomicLong;

import be.nabu.libs.metrics.core.api.Sink;

/**
 * This maintains a current value and allows you to send incremental delta's instead of full values 
 */
public class DeltaSink implements Sink {

	private AtomicLong value;
	private Sink parent;
	
	public DeltaSink(Sink parent) {
		this.parent = parent;
		value = new AtomicLong(0);
	}
	
	@Override
	public void push(long timestamp, long value) {
		parent.push(timestamp, this.value.addAndGet(value));
	}

}
