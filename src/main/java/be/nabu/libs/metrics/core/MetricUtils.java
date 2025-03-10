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

import be.nabu.libs.metrics.core.api.SinkSnapshot;
import be.nabu.libs.metrics.core.api.SinkStatistics;
import be.nabu.libs.metrics.core.api.SinkValue;
import be.nabu.libs.metrics.core.sinks.StatisticsSink;

public class MetricUtils {
	
	public static SinkStatistics getStatistics(SinkSnapshot snapshot) {
		StatisticsSink sink = new StatisticsSink();
		for (SinkValue value : snapshot.getValues()) {
			sink.push(value.getTimestamp(), value.getValue());
		}
		return sink;
	}
	
	public static long getNearestWindowStart(long windowInterval) {
		long now = new Date().getTime();
		// if we have an interval, try to reduce it to a clean starting point
		// e.g. if you start the server at 00:02:34.567 with an interval of 5 minutes we might want the window to run from 00:00-00:05.
		// this can make future interpretations of the data easier 
		if (windowInterval > 0) {
			now -= now % windowInterval;
		}
		return now;
	}
	
}
