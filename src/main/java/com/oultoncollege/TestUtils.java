package com.oultoncollege;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;

import io.appium.java_client.AppiumDriver;

public class TestUtils {

	public static final String DEFAULT_IP = "127.0.0.1";

	/**
	 * 
	 * @return
	 */
	protected static String getIP() {
		InetAddress ip;
		try {
			ip = InetAddress.getLocalHost();
			return ip.getHostAddress();
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
			return DEFAULT_IP;
		}
	}

	/**
	 * 
	 * @param driver
	 */
	public static void changeDriverContextToWeb(AppiumDriver driver) {
		Set<String> allContext = driver.getContextHandles();
		for (String context : allContext) {
			if (context.contains("WEBVIEW")) {
				driver.context(context);
			}
		}
	}

	/**
	 * 
	 * @param driver
	 */
	public static void changeDriverContextToNative(AppiumDriver driver) {
		Set<String> contextNames = driver.getContextHandles();
		for (String contextName : contextNames) {
			if (contextName.contains("NATIVE")) { 
				driver.context(contextName);
			}
		}
	}

	/**
	 * 
	 * @param driver
	 * @param imageFolder
	 * @throws IOException
	 */
	public void getScreenshot(AppiumDriver driver, String imageFolder) throws IOException {
		File srcImgFile = driver.getScreenshotAs(OutputType.FILE);
		String filename = UUID.randomUUID().toString();
		File targetImgFile = new File(imageFolder + filename + ".jpg");
		FileUtils.copyFile(srcImgFile, targetImgFile);
	}

	/**
	 * Runs the "adb devices" command from CLI and checks output to provide a list 
	 * of attached physical Android Devices and/or Emulators.
	 * NEED:
	 * 		method to build the desired capability based on the device UDID as the parameter
	 * 		properties file to save the mapping of tags and devices to pick at runtime
	 * 		method in Maven or Gradle to read from the properties file and run the test
	 *
	 * @return
	 */
	public List<String> attachedDevicesAndEmulators() {
		List<String> devices = new ArrayList<>();
		String line;
		StringBuilder log = new StringBuilder();
		Process process;
		Runtime rt = Runtime.getRuntime();
		try {
			process = rt.exec(new String[] { "adb", "devices", "-l" });
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			while ((line = stdInput.readLine()) != null) {
				log.append(line);
				log.append(System.getProperty("line.separator"));
			}
			while ((line = stdError.readLine()) != null) {
				log.append(line);
				log.append(System.getProperty("line.separator"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Scanner scan = new Scanner(String.valueOf(log));
		while (scan.hasNextLine()) {
			String oneLine = scan.nextLine();
			if (oneLine.contains("model")) {
				devices.add(oneLine.split("device")[0].trim());
			}
		}
		return devices;
	}

}
