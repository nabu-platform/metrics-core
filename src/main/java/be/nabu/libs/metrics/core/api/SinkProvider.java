package be.nabu.libs.metrics.core.api;

public interface SinkProvider {
	public Sink getSink(String id, String category);
}
