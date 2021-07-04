package be.nabu.libs.metrics.core;

import be.nabu.libs.metrics.core.api.SinkValue;

public class SinkValueImpl implements SinkValue {
	private long timestamp;
	private long value;
	
	public SinkValueImpl() {
		// auto construct
	}
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

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void setValue(long value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return value + "@" + timestamp;
	}
}
