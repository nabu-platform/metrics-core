package be.nabu.libs.metrics.core.sinks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.nabu.libs.metrics.core.api.ListableSinkProvider;
import be.nabu.libs.metrics.core.api.Sink;

public class LimitedHistorySinkProvider implements ListableSinkProvider {

	private int size;
	private Map<String, Map<String, Sink>> sinks = new HashMap<String, Map<String, Sink>>();

	public LimitedHistorySinkProvider(int size) {
		this.size = size;
	}
	
	@Override
	public Sink getSink(String id, String category) {
		if (!sinks.containsKey(id)) {
			synchronized(this) {
				if (!sinks.containsKey(id)) {
					sinks.put(id, new HashMap<String, Sink>());
				}
			}
		}
		if (!sinks.get(id).containsKey(category)) {
			synchronized(sinks.get(id)) {
				if (!sinks.get(id).containsKey(category)) {
					sinks.get(id).put(category, new LimitedHistorySinkWithStatistics(size));
				}
			}
		}
		return sinks.get(id).get(category);
	}

	@Override
	public Map<String, List<String>> getSinks() {
		Map<String, List<String>> sinks = new HashMap<String, List<String>>();
		for (String id : this.sinks.keySet()) {
			sinks.put(id, new ArrayList<String>(this.sinks.get(id).keySet()));
		}
		return sinks;
	}

}
