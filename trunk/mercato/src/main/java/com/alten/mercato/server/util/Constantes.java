package com.alten.mercato.server.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author pdonaghy
 */
public class Constantes {

	private static Log log = LogFactory.getLog(Constantes.class);

	private static Properties properties = null;
	// property will be sent to client side by RPC
	private static Map<String,String> property = null;

	/**
	 * @param key
	 * @return
	 * @throws RuntimeException
	 */
	public static String getString(String key) throws RuntimeException {
		try {
			if (properties!=null) {
				return properties.getProperty(key);
			} else {
				throw new RuntimeException("Class Settings not initialized!");
			}

		} catch (MissingResourceException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getPath(String key) throws RuntimeException {
		try {
			if (properties!=null) {
				String str_path = properties.getProperty(key);
				String root = properties.getProperty("ROOT_CONF");
				return root+"/"+str_path;
			} else {
				throw new RuntimeException("Class Settings not initialized!");
			}

		} catch (MissingResourceException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * @param key
	 * @return
	 */

	public static boolean getBoolean(String key) throws RuntimeException {
		String prop = null;
		prop = getString(key);
		if (prop.equalsIgnoreCase("true")) return true;
		else if (prop.equalsIgnoreCase("false")) return false;
		else throw new RuntimeException("La clef "+key+" est introuvable ou ne contient pas de booleen");
	}

	public static Long getLong(String key) throws RuntimeException {
		String prop = null;
		Long lProp = null;
		prop = getString(key);
		lProp = Long.valueOf(prop);
		return lProp;
	}

	public static Integer getInteger(String key) throws RuntimeException {
		String prop = null;
		Integer iProp = null;
		prop = getString(key);
		iProp = Integer.valueOf(prop);
		return iProp;
	}

	public static void setParam(String key, Object obj) throws RuntimeException {
		if (properties!=null) {
			properties.put(key, obj);
		} else {
			throw new RuntimeException("Class Settings not initialized!");
		}
	}

	public static void addProperties(Properties propertiesToAdd) throws RuntimeException {
		if (properties!=null) {
			properties.putAll(propertiesToAdd);
		} else {
			throw new RuntimeException("Class Settings not initialized!");
		}

	}

	public static void addProperties(URL url) throws RuntimeException {
		try {
			Properties propertiesToAdd = new Properties();
			InputStream is = url.openStream();
			propertiesToAdd.load(is);
			is.close();
			if (properties!=null) {
				properties.putAll(propertiesToAdd);
			} else {

				throw new RuntimeException("Class Settings not initialized!");
			}
		} catch (Exception e) {
			// this is more serious, SCREAM!!!
			// log.error(e);
			log.fatal("Properties file error.", e);
			throw new RuntimeException("Properties file error");
		}

	}

	public static void init(File file) throws RuntimeException {
		try {
			if (file.exists()) {
				properties = new Properties();
				log.info("Loading properties from : "+file.toString());
				FileInputStream fis = new FileInputStream(file);
				properties.load(fis);
				fis.close();

				// initialisation of the property to send to client side
				property = new HashMap<String,String>();
				for (Map.Entry<Object,Object> entry: properties.entrySet()) {
					String key = (String) entry.getKey();
					String value = (String) entry.getValue();
					property.put(key, value);
				}
			}
		} catch (FileNotFoundException e) {
			log.fatal(e);
		} catch (IOException e) {
			// this is more serious, SCREAM!!!
			// log.fatal(e);
			log.fatal("Properties file error.", e);
			throw new RuntimeException("Properties file error");
		}
	}

	public static void init(URL url) throws RuntimeException {
		try {
			properties = new Properties();
			log.info("Loading properties from : "+url.toString());
			InputStream is = url.openStream();
			properties.load(is);
			is.close();

			// initialisation of the property to send to client side
			property = new HashMap<String,String>();
			for (Map.Entry<Object,Object> entry: properties.entrySet()) {
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				property.put(key, value);
			}
		} catch (Exception e) {
			// this is more serious, SCREAM!!!
			// log.fatal(e);
			log.fatal("Properties file error.", e);
			throw new RuntimeException("Properties file error");
		}
	}

	public static Properties getProperties() {
		return properties;
	}

	public static Map<String,String> getProperty() {
		return property;
	}

}
