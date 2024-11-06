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

package be.nabu.libs.metrics.core;

import java.util.List;

import be.nabu.libs.metrics.core.api.Deviation;
import be.nabu.libs.metrics.core.api.SinkStatistics;
import be.nabu.libs.metrics.core.api.SinkValue;

public class SinkStatisticsImpl implements SinkStatistics {

	private SinkValue min, max;
	private double exponentialAverage, cumulativeAverage;
	private List<Deviation> cumulativeAverageDeviation;
	private long amountOfDataPoints, total;
	
	public SinkStatisticsImpl() {
		// auto construct
	}
	public SinkStatisticsImpl(SinkStatistics snapshot) {
		this.max = snapshot.getMaximum();
		this.min = snapshot.getMinimum();
		this.exponentialAverage = snapshot.getExponentialAverage();
		this.cumulativeAverage = snapshot.getCumulativeAverage();
		this.cumulativeAverageDeviation = snapshot.getCumulativeAverageDeviation();
		this.amountOfDataPoints = snapshot.getAmountOfDataPoints();
		this.total = snapshot.getTotal();
	}
	
	@Override
	public SinkValue getMinimum() {
		return min;
	}
	public void setMinimum(SinkValue min) {
		this.min = min;
	}

	@Override
	public SinkValue getMaximum() {
		return max;
	}
	public void setMaximum(SinkValue max) {
		this.max = max;
	}

	@Override
	public double getExponentialAverage() {
		return exponentialAverage;
	}
	public void setExponentialAverage(double exponentialAverage) {
		this.exponentialAverage = exponentialAverage;
	}

	@Override
	public double getCumulativeAverage() {
		return cumulativeAverage;
	}
	public void setCumulativeAverage(double cumulativeAverage) {
		this.cumulativeAverage = cumulativeAverage;
	}

	@Override
	public List<Deviation> getCumulativeAverageDeviation() {
		return cumulativeAverageDeviation;
	}
	public void setCumulativeAverageDeviation(List<Deviation> cumulativeAverageDeviation) {
		this.cumulativeAverageDeviation = cumulativeAverageDeviation;
	}
	@Override
	public long getAmountOfDataPoints() {
		return amountOfDataPoints;
	}
	public void setAmountOfDataPoints(long amountOfDataPoints) {
		this.amountOfDataPoints = amountOfDataPoints;
	}
	@Override
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
}
