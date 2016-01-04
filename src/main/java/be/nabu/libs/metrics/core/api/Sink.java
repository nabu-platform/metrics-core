package be.nabu.libs.metrics.core.api;

public interface Sink {
	public void push(long timestamp, long value);
}
