package be.nabu.libs.metrics.core.api;

public interface HistorySink extends Sink {
	/**
	 * Gets the last "amount" of values until a certain point in time (inclusive) 
	 */
	public SinkSnapshot getSnapshotUntil(int amount, long until);
	/**
	 * Both the from and until are inclusive
	 */
	public SinkSnapshot getSnapshotBetween(long from, long until);
}
