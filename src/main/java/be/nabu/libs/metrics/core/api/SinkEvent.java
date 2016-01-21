package be.nabu.libs.metrics.core.api;

public interface SinkEvent extends SinkValue {
	public String getCategory();
	public Sink getSink();
}
