package be.nabu.libs.metrics.core.api;

public interface CurrentValueSink extends Sink {
	public SinkValue getCurrent();
}
