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

import be.nabu.libs.metrics.core.api.Sink;

/**
 * This maintains a current value and allows you to send incremental delta's instead of full values 
 */
public class DeltaSink implements Sink {

	private AtomicLong value;
	private Sink parent;
	
	public DeltaSink(Sink parent) {
		this.parent = parent;
		value = new AtomicLong(0);
	}
	
	@Override
	public void push(long timestamp, long value) {
		parent.push(timestamp, this.value.addAndGet(value));
	}

}
