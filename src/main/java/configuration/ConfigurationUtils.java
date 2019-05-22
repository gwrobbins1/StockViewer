package configuration;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConfigurationUtils {
	
	private static final String CONF_FILE = "config.properties";

	private Logger logger = LoggerFactory.getLogger(ConfigurationUtils.class);
	private boolean isInitialized = false;
	private Properties props = null;
	
	private ConfigurationUtils() {}
	
	private static class LazyHolder{
		public static final ConfigurationUtils INSTANCE = new ConfigurationUtils();
	}
	
	public static ConfigurationUtils getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	public void initialize() {
		try {
			props = new Properties();
			InputStream in = getClass().getClassLoader().getResourceAsStream(CONF_FILE);
			
			if(in != null) {
				props.load(in);
				isInitialized = true;
			}else {
				logger.error("Cannot load configuration file");
			}
		}catch(Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	public String get(String property) {
		if(isInitialized && props != null)
			return props.getProperty(property);
		
		return "";
	}
}
