package il.ac.bgu.cs.common;

import java.util.Map;

public class ConfigurationServiceImpl implements ConfigurationService {
	private Map<ConfigParam, Object> configuration;

	@Override
	public String getString(ConfigParam key) {
		if (configuration.containsKey(key)){
			return (String)configuration.get(key);
		}
		return null;
	}
	
	@Override
	public Integer getInteger(ConfigParam key){
		if (configuration.containsKey(key)){
			return (Integer)configuration.get(key);
		}
		return null;
	}	
}
