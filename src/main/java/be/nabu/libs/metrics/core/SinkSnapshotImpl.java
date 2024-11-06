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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import be.nabu.libs.metrics.core.api.SinkSnapshot;
import be.nabu.libs.metrics.core.api.SinkValue;

public class SinkSnapshotImpl implements SinkSnapshot {

	private List<SinkValue> values;

	public SinkSnapshotImpl() {
		// auto construct
	}
	
	public SinkSnapshotImpl(List<SinkValue> values) {
		this(values, true);
	}
	
	public SinkSnapshotImpl(List<SinkValue> values, boolean sort) {
		this.values = new ArrayList<SinkValue>(values);
		if (sort) {
			// sort them chronologically
			Collections.sort(this.values, new Comparator<SinkValue>() {
				@Override
				public int compare(SinkValue o1, SinkValue o2) {
					return Long.compare(o1.getTimestamp(), o2.getTimestamp());
				}
			});
		}
	}
	
	@Override
	public List<SinkValue> getValues() {
		if (values == null) {
			values = new ArrayList<SinkValue>();
		}
		return values;
	}
	public void setValues(List<SinkValue> values) {
		this.values = values;
	}
}
