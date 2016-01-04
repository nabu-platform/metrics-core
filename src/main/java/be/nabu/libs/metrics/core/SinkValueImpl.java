package be.nabu.libs.metrics.core;

import be.nabu.libs.metrics.core.api.SinkValue;

public class SinkValueImpl implements SinkValue {
	private long timestamp;
	private long value;
	
	public SinkValueImpl(long timestamp, long value) {
		this.timestamp = timestamp;
		this.value = value;
	}
	@Override
	public long getTimestamp() {
		return timestamp;
	}
	@Override
	public long getValue() {
		return value;
	}
}
