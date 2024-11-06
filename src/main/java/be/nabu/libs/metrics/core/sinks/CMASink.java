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
import be.nabu.libs.metrics.core.api.Sink;
import be.nabu.libs.metrics.core.api.SinkValue;

/**
 * Cumulative moving average (basically the simple average)
 * https://en.wikipedia.org/wiki/Moving_average
 */
public class CMASink implements CurrentValueSink {
	
	private AtomicLong cma;
	private AtomicLong timestamp;
	private AtomicLong counter;
	private Sink target;
	
	public CMASink() {
		this(null);
	}
	
	public CMASink(Sink target) {
		this.target = target;
		this.timestamp = new AtomicLong(0);
		this.counter = new AtomicLong(0);
	}
	
	@Override
	public void push(long timestamp, long value) {
		double index = this.counter.getAndIncrement();
		boolean set = false;
		if (cma == null) {
			synchronized(this) {
				if (cma == null) {
					cma = new AtomicLong(Double.doubleToLongBits(value));
					this.timestamp.set(timestamp);
					set = true;
				}
			}
		}
		if (!set) {
			double previousCMA = Double.longBitsToDouble(cma.get());
			// we calculate the new average
			double newCMA = previousCMA + ((value - previousCMA) / (index + 1));
			cma.set(Double.doubleToLongBits(newCMA));
			if (target != null) {
				target.push(timestamp, (long) newCMA);
			}
			this.timestamp.set(timestamp);
		}
	}

	@Override
	public SinkValue getCurrent() {
		return cma == null ? null : new SinkValueImpl(timestamp.get(), (long) Double.longBitsToDouble(cma.get()));
	}
	
	public double getValue() {
		return cma == null ? 0 : Double.longBitsToDouble(cma.get());
	}
}
