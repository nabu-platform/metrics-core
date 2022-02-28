package be.nabu.libs.metrics.core.api;

public interface AutomatedWindowSink extends WindowedSink {
	public void setWindowInterval(long milliseconds);
}
