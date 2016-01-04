package be.nabu.libs.metrics.core;

import java.util.concurrent.atomic.AtomicLong;

import be.nabu.libs.metrics.api.MetricGauge;

public class MetricGaugeImpl implements MetricGauge {

	private AtomicLong value;
	
	public MetricGaugeImpl(long initialValue) {
		value = new AtomicLong(initialValue);
	}
	
	@Override
	public long getValue() {
		return value.get();
	}
	
	public void setValue(long value) {
		this.value.set(value);
	}

}
