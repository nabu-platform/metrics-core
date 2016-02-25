package be.nabu.libs.metrics.core;

import java.util.Comparator;

import be.nabu.libs.metrics.core.api.SinkValue;

public class SinkValueComparator<T extends SinkValue> implements Comparator<T> {

	private boolean ascending;

	public SinkValueComparator() {
		this(true);
	}
	
	public SinkValueComparator(boolean ascending) {
		this.ascending = ascending;
	}
	
	@Override
	public int compare(T o1, T o2) {
		int comparison = 0;
		if (o1.getTimestamp() < o2.getTimestamp()) {
			comparison = -1;
		}
		else if (o1.getTimestamp() > o2.getTimestamp()) {
			comparison = 1;
		}
		return ascending ? comparison : comparison * -1;
	}

}
