package be.nabu.libs.metrics.core.api;

public interface SinkProvider {
	public Sink newSink(String id, String category);
}
