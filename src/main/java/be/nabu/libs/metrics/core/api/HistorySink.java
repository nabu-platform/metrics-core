package be.nabu.libs.metrics.core.api;

public interface HistorySink extends Sink {
	/**
	 * Gets the last "amount" of values, this is usually used for the first pull from a sink to have "a" starting point
	 */
	public SinkSnapshot getSnapshot(int amount);
	/**
	 * Both the from and until are inclusive
	 */
	public SinkSnapshot getSnapshotBetween(long from, long until);
}
