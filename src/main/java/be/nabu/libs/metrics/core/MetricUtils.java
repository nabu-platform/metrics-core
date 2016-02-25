package be.nabu.libs.metrics.core;

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
	
}
