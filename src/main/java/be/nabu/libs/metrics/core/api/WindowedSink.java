package be.nabu.libs.metrics.core.api;

public interface WindowedSink {
	// get the start of the window of the current data
	public long getWindowStart();
	// the current end of the data
	public long getWindowStop();
}
