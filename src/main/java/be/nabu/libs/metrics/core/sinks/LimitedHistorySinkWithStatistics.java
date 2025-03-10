/*
* Copyright (C) 2016 Alexander Verbruggen
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package be.nabu.libs.metrics.core.sinks;

import be.nabu.libs.metrics.core.api.SinkStatistics;
import be.nabu.libs.metrics.core.api.StatisticsContainer;
import be.nabu.libs.metrics.core.api.StatisticsListener;

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
