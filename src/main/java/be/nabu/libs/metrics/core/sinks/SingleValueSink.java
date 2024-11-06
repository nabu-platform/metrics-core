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

import java.util.concurrent.atomic.AtomicLong;

import be.nabu.libs.metrics.core.SinkValueImpl;
import be.nabu.libs.metrics.core.api.CurrentValueSink;
import be.nabu.libs.metrics.core.api.SinkValue;

public class SingleValueSink implements CurrentValueSink {

	private AtomicLong value;
	private AtomicLong timestamp = new AtomicLong(0);
	private UpdateDecider decider;
	
	public SingleValueSink(UpdateDecider decider) {
		this.decider = decider;
	}
	
	@Override
	public void push(long timestamp, long value) {
		if (decider.shouldUpdate(getCurrent(), new SinkValueImpl(timestamp, value))) {
			if (this.value == null) {
				this.value = new AtomicLong(value);
			}
			else {
				this.value.set(value);
			}
			this.timestamp.set(timestamp);
		}
	}

	@Override
	public SinkValue getCurrent() {
		return value == null ? null : new SinkValueImpl(timestamp.get(), value.get());
	}
	
	public static interface UpdateDecider {
		public boolean shouldUpdate(SinkValue oldValue, SinkValue newValue);
	}
	
	public static class MaxValue implements UpdateDecider {
		@Override
		public boolean shouldUpdate(SinkValue oldValue, SinkValue newValue) {
			return oldValue == null || newValue.getValue() > oldValue.getValue();
		}
	}
	public static class MinValue implements UpdateDecider {
		@Override
		public boolean shouldUpdate(SinkValue oldValue, SinkValue newValue) {
			return oldValue == null || newValue.getValue() < oldValue.getValue();
		}
	}
}
