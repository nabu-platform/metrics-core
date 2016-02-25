package be.nabu.libs.metrics.core.api;

/**
 * The deviation tells us how many (percentage) of the incoming events are below a certain deviation (e.g. 25%) of a certain point (e.g. the average)
 * The percentage is not cumulative but relative to the lower deviations, e.g. if 50% of events falls below 25%, these events are not represented in the 50% deviation
 */
public interface Deviation {
	public double getDeviation();
	public double getPercentage();
}
