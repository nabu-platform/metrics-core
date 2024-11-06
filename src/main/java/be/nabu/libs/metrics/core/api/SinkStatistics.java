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

package be.nabu.libs.metrics.core.api;

import java.util.List;

public interface SinkStatistics {
	/**
	 * The minimum value that was pushed to the sink
	 */
	public SinkValue getMinimum();
	/**
	 * The maximum value that was pushed to the sink
	 */
	public SinkValue getMaximum();
	/**
	 * The exponential (moving) average
	 */
	public double getExponentialAverage();
	/**
	 * The cumulative (moving) average
	 */
	public double getCumulativeAverage();
	/**
	 * The deviation of the sink values against the cumulative average
	 * For example suppose we push "1, 2, 3, 4", then the cumulative average is 2.5
	 * But we could also push "-10, 100, -90, 10" and still get a cumulative average of 2.5
	 * 
	 * To get an inkling of how "stable" the values are, we (can) keep deviations from the cumulative average, for example we keep track of how many values fall within the 25%, 50% and 75% range of the current cumulative average
	 * This gives a number of "zones" around the cumulative average that give instant feedback over metric volatility
	 */
	public List<Deviation> getCumulativeAverageDeviation();
	/**
	 * The amount of data points collected to calculate the statistics
	 */
	public long getAmountOfDataPoints();
	
	/**
	 * Get the combined total
	 * For large numbers this may overflow but for the vast majority of numbers (given a small enough window), it should be representative
	 */
	public long getTotal();
}
