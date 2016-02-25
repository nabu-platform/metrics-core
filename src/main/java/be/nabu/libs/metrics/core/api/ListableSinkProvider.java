package be.nabu.libs.metrics.core.api;

import java.util.List;
import java.util.Map;

public interface ListableSinkProvider extends SinkProvider {
	public Map<String, List<String>> getSinks();
}
