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
			for (MetricInstanceImpl instance : instances) {
				for (String id : instance.getGaugeIds()) {
					instance.log(id, instance.getGauge(id).getValue());
				}
			}
			try {
				// sleep for whatever is left of the interval
				Thread.sleep(interval - (new Date().getTime() - started.getTime()));
			}
			catch (InterruptedException e) {
				break;
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
