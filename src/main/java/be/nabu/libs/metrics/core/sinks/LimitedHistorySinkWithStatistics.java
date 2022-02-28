package be.nabu.libs.metrics.core.sinks;

import be.nabu.libs.metrics.core.api.SinkStatistics;
import be.nabu.libs.metrics.core.api.StatisticsContainer;
import be.nabu.libs.metrics.core.api.StatisticsListener;
import be.nabu.libs.metrics.core.sinks.LimitedHistorySink;
import be.nabu.libs.metrics.core.sinks.StatisticsSink;

public class LimitedHistorySinkWithStatistics extends LimitedHistorySink implements StatisticsContainer {

	private static final int WINDOW = 100;
	private StatisticsListener statisticsListener;

	private volatile StatisticsSink statistics;
	
	public LimitedHistorySinkWithStatistics(int size) {
		super(size);
		this.statistics = new StatisticsSink(WINDOW);
		// synchronize window start
		this.statistics.setWindowStart(getWindowStart());
	}
	
	@Override
	public void push(long timestamp, long value) {
		super.push(timestamp, value);
		statistics.push(timestamp, value);
	}

	@Override
	public SinkStatistics getStatistics() {
		return statistics;
	}

	@Override
	protected void reset(long windowStart) {
		StatisticsSink previous = this.statistics;
		super.reset(windowStart);
		this.statistics = new StatisticsSink(WINDOW);
		// synchronize start window
		this.statistics.setWindowStart(getWindowStart());
		if (this.windowInterval > 0) {
			previous.setWindowStop(previous.getWindowStart() + this.windowInterval);
		}
		// we need at least _a_ datapoint before we are interested
		if (statisticsListener != null && previous.getAmountOfDataPoints() > 0) {
			statisticsListener.handle(previous.getWindowStart(), previous.getWindowStop(), previous);
		}
	}

	public StatisticsListener getStatisticsListener() {
		return statisticsListener;
	}
	public void setStatisticsListener(StatisticsListener statisticsListener) {
		this.statisticsListener = statisticsListener;
	}
}
