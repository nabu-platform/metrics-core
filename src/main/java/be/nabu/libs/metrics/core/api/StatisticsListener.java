package be.nabu.libs.metrics.core.api;

public interface StatisticsListener {
	public void handle(long windowStart, long windowStop, SinkStatistics container);
}
