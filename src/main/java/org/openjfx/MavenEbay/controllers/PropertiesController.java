package org.openjfx.MavenEbay.controllers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class PropertiesController {
	static Properties props;
	static ClassLoader classloader;

	public static void initControl() {
		try {
			classloader = Thread.currentThread().getContextClassLoader();
			props = new Properties();
			props.load(classloader.getResourceAsStream("org/openjfx/MavenEbay/config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getPropertyAsString(String key) {
		String result = "";
		if (props.getProperty(key) != null && !props.getProperty(key).equals("")) {
			result = props.getProperty(key);
		}
		return result;
	}
	
	public static boolean getPropertyAsBool(String key) {
		boolean result = false;
		if (props.getProperty(key) != null && props.getProperty(key).trim().equals("true")) {
			result = true;
		}
		return result;
	}

	public static int getPropertyAsInt(String key) {
		int result = 0;
		try {
			if (props.getProperty(key) != null && !props.getProperty(key).equals("")) {
				result = Integer.parseInt(props.getProperty(key));
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			DialogController.showAlertWithoutHeaderText(String.format("Value of [%s] should be integer, but it's value is [%s], value \"0\" will be use", key, props.getProperty(key)));
		}
		return result;
	}
	
	public static float getPropertyAsFloat(String key) {
		float result = 0.5f;
		try {
			if (props.getProperty(key) != null && !props.getProperty(key).equals("")) {
				result = Float.parseFloat(props.getProperty(key));
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			DialogController.showAlertWithoutHeaderText(String.format("Value of [%s] should be float, but it's value is [%s], value \"0.5\" will be use", key, props.getProperty(key)));
		}
		return result;
	}
	
	public static String getPropertyAsIntString(String key) {
		return String.valueOf(getPropertyAsInt(key));
	}
	
	public static String getPropertyAsFloatString(String key) {
		return String.valueOf(getPropertyAsFloat(key));
	}

	public static void setPropertyAsString(String key, String value) {
		props.setProperty(key, value);
	}

	public static void setPropertyAsInt(String key, String value) {
		String result = "0";
		try {
			result = String.valueOf(Integer.parseInt(value));
		} catch (NumberFormatException e) {
			DialogController.showAlertWithoutHeaderText(String.format("Value of [%s] should be integer, but it's value is [%s], value \"0\" will be use", key, value));
			e.printStackTrace();
		}
		props.setProperty(key, result);
	}
	
	public static void setPropertyAsFloat(String key, String value) {
		String result = "0.5";
		try {
			result = String.valueOf(Float.parseFloat(value));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			DialogController.showAlertWithoutHeaderText(String.format("Value of [%s] should be float, but it's value is [%s], value \"0.5\" will be use", key, value));
		}
		props.setProperty(key, result);
	}
	
	public static void store() {
		FileOutputStream st = null;
		try {
			URL config = classloader.getResource("org/openjfx/MavenEbay/config.properties");
			st = new FileOutputStream(config.getPath());
			props.store(st, null);
			st.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			try {
				st.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
			DialogController.showAlertWithoutHeaderText("Cannot save config file!, don't know why !");
		}
	}
}
