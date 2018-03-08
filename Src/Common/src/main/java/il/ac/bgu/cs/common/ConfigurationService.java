package il.ac.bgu.cs.common;

public interface ConfigurationService {
	String getString(ConfigParam key);
	Integer getInteger(ConfigParam key);
}
