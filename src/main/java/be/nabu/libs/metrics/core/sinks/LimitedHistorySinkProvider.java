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
