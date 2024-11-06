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
 * Exponential moving average: http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:moving_averages
 * Note that we return (and push) long values, not doubles. These can have inherent rounding "errors"
 */
public class EMASink implements CurrentValueSink {

	private double multiplier = 1d;
	private AtomicLong ema;
	private AtomicLong timestamp;
	private Sink target;
	
	public EMASink(int windowSize) {
		this(windowSize, null);
	}
	
	public EMASink(int windowSize, Sink target) {
		this.target = target;
		this.multiplier = 2d / (windowSize + 1);
		this.timestamp = new AtomicLong(0);
	}
	
	@Override
	public void push(long timestamp, long value) {
		boolean set = false;
		if (ema == null) {
			synchronized(this) {
				if (ema == null) {
					ema = new AtomicLong(Double.doubleToLongBits(value));
					this.timestamp.set(timestamp);
					set = true;
				}
			}
		}
		if (!set) {
			double previousEMA = Double.longBitsToDouble(ema.get());
			double newEMA = (multiplier * (value - previousEMA)) + previousEMA;
			ema.set(Double.doubleToLongBits(newEMA));
			if (target != null) {
				target.push(timestamp, (long) newEMA);
			}
			this.timestamp.set(timestamp);
		}
	}

	@Override
	public SinkValue getCurrent() {
		return ema == null ? null : new SinkValueImpl(timestamp.get(), (long) Double.longBitsToDouble(ema.get()));
	}
	
	public double getValue() {
		return ema == null ? 0 : Double.longBitsToDouble(ema.get());
	}

}
