package be.nabu.libs.metrics.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import be.nabu.libs.metrics.core.api.SinkSnapshot;
import be.nabu.libs.metrics.core.api.SinkValue;

public class SinkSnapshotImpl implements SinkSnapshot {

	private List<SinkValue> values;

	public SinkSnapshotImpl(List<SinkValue> values) {
		this.values = new ArrayList<SinkValue>(values);
		// sort them chronologically
		Collections.sort(this.values, new Comparator<SinkValue>() {
			@Override
			public int compare(SinkValue o1, SinkValue o2) {
				return Long.compare(o1.getTimestamp(), o2.getTimestamp());
			}
		});
	}
	
	@Override
	public List<SinkValue> getValues() {
		return new ArrayList<SinkValue>(values);
	}
}
