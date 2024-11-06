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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GaugeHistorizer implements Runnable {
	
	private List<MetricInstanceImpl> instances;
	private long interval;

	public GaugeHistorizer(long interval, MetricInstanceImpl...instances) {
		this.interval = interval;
		this.instances = Arrays.asList(instances);
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			Date started = new Date();
			for (MetricInstanceImpl instance : new ArrayList<MetricInstanceImpl>(instances)) {
				for (String id : new ArrayList<String>(instance.getGaugeIds())) {
					instance.log(id, instance.getGauge(id).getValue());
				}
			}
			long sleep = interval - (new Date().getTime() - started.getTime());
			if (sleep > 0) {
				try {
					// sleep for whatever is left of the interval
					Thread.sleep(sleep);
				}
				catch (InterruptedException e) {
					break;
				}
			}
		}
	}
	
	public void add(MetricInstanceImpl...instances) {
		List<MetricInstanceImpl> list = new ArrayList<MetricInstanceImpl>(this.instances);
		list.addAll(Arrays.asList(instances));
		this.instances = list;
	}
	
	public void remove(MetricInstanceImpl...instances) {
		List<MetricInstanceImpl> list = new ArrayList<MetricInstanceImpl>(this.instances);
		list.removeAll(Arrays.asList(instances));
		this.instances = list;
	}
}
