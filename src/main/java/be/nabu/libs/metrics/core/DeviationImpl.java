package be.nabu.libs.metrics.core;

import be.nabu.libs.metrics.core.api.Deviation;

public class DeviationImpl implements Deviation {
	
	private double deviation, percentage;

	public DeviationImpl(double deviation, double percentage) {
		this.deviation = deviation;
		this.percentage = percentage;
	}
	
	public DeviationImpl() {
		// autoconstruct
	}
	
	@Override
	public double getDeviation() {
		return deviation;
	}
	public void setDeviation(double deviation) {
		this.deviation = deviation;
	}

	@Override
	public double getPercentage() {
		return percentage;
	}
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
	
}
