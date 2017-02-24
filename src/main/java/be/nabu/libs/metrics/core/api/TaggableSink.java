package be.nabu.libs.metrics.core.api;

import java.util.Collection;

public interface TaggableSink {
	public String getTag(String key);	
	public void setTag(String key, String value);
	public Collection<String> getTags();	
}
