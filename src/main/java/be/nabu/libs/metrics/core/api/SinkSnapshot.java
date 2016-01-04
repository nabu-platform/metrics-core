package be.nabu.libs.metrics.core.api;

import java.util.List;

public interface SinkSnapshot {
	public List<SinkValue> getValues();
}
