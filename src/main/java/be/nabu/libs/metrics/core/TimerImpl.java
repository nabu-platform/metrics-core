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

import java.util.Date;
import java.util.concurrent.TimeUnit;

import be.nabu.libs.metrics.api.MetricInstance;
import be.nabu.libs.metrics.api.MetricTimer;

public class TimerImpl implements MetricTimer {

	private long started;
	private MetricInstanceImpl metrics;
	private String category;
	
	TimerImpl(MetricInstanceImpl metrics, String category) {
		this.metrics = metrics;
		this.category = category;
		this.started = new Date().getTime();
	}
	
	@Override
	public long stop() {
		long duration = new Date().getTime() - started;
		getMetrics().duration(category, duration, getTimeUnit());
		return duration;
	}

	@Override
	public MetricInstance getMetrics() {
		return metrics;
	}

	@Override
	public TimeUnit getTimeUnit() {
		return TimeUnit.MILLISECONDS;
	}
}
