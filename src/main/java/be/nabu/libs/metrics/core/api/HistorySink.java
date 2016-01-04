package be.nabu.libs.metrics.core.api;

public interface HistorySink extends Sink {
	public SinkSnapshot getSnapshot(int amount);
	public SinkSnapshot getSnapshotAfter(long timestamp);
}
