package org.openjfx.MavenEbay.controllers;

import java.awt.AWTException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.openjfx.MavenEbay.App;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoController {
	WebDriver driver;
	String profileName = "Default";
	ClassLoader classloader;
	private static Logger LOGGER = LoggerFactory.getLogger(AutoController.class);

	public AutoController(String profileName) {
		if (profileName != null) {
			this.profileName = profileName;
		}
		classloader = Thread.currentThread().getContextClassLoader();
	}

	public void openBrowserAndStart(String userProfile) {
		LOGGER.debug(String.format("openBrowserAndStart(String %s)", userProfile));
		try {
			URL driverURL = classloader.getResource("org/openjfx/MavenEbay/chromedriver.exe");

			System.setProperty("webdriver.chrome.driver", driverURL.getPath());
			ChromeOptions options = new ChromeOptions();
			options.addArguments("user-data-dir=" + PropertiesController.getPropertyAsString("profileDir"));
			options.addArguments("--start-maximized");
			options.addArguments("--profile-directory=" + userProfile);
			driver = new ChromeDriver(options);
			driver.navigate().to("https://www.ebay.com/");

			// Your implement here!

		} catch (Exception e) {
			LOGGER.error(e.toString());
			closeAnyWay();
		}
	}

	public void closeAnyWay() {
		LOGGER.debug(String.format("closeAnyWay()"));
		try {
			driver.close();
			driver.quit();
		} catch (Exception e) {
			LOGGER.error(e.toString());
		}
	}

	public void fakeTyping(String content, WebElement element) {
		LOGGER.debug(String.format("fakeTyping(String %s, WebElement element)", content));
		String existText = element.getText();
		List<String> textChar = Arrays.asList(content.split("(?!^)"));
		textChar.forEach(mChar -> {
			waitFor(100, 300);
			element.sendKeys(mChar);
		});
	}

	public void fakeMouseHover(WebElement pane) {
		LOGGER.debug(String.format("fakeMouseHover(WebElement pane)"));
		try {
			Rectangle rectange = pane.getRect();

			java.awt.Robot robot = new java.awt.Robot();
			int minY = rectange.y;
			int startX = randomBetween(rectange.x, rectange.x + rectange.width);
			if (startX < 280)
				startX = 280;
			int maxY = rectange.y + rectange.height;
			int endX = randomBetween(rectange.x, rectange.x + rectange.width);
			if (endX > 640)
				endX = 640;

			int runX = startX;
			int runY = minY;
			if (runX <= endX) {
				while (runX < endX && runY < maxY) {
					runX += 2;
					runY += 2;
					robot.mouseMove(runX, runY);
					waitFor(17, 17);
				}
			} else {
				while (runX > endX && runY < maxY) {
					runX += -2;
					runY += +2;
					robot.mouseMove(runX, runY);
					waitFor(17, 17);
				}
			}

		} catch (AWTException e) {
			// TODO Auto-generated catch block
			LOGGER.debug(e.toString());
		}

	}

	public int waitFor(int min, int max) {
		LOGGER.debug(String.format("waitFor(int %d, int %d)", min, max));
		int delay = randomBetween(min, max);
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			LOGGER.debug(e.toString());
		}
		return delay;
	}

	public void enterKeySearch(String key) {
		LOGGER.debug(String.format("enterKeySearch(String %s)", key));
		// Your implement here!
	}

	@SuppressWarnings("deprecation")
	public void viewSearchResult() {
		LOGGER.debug(String.format("viewSearchResult()"));
		// Your implement here!
	}

	public void backtoSearhResult() {
		LOGGER.debug(String.format("backtoSearhResult()"));
		// Your implement here!
	}

	public void clickOnProduct() {
		LOGGER.debug(String.format("clickOnProduct()"));
		List<WebElement> elements = null;
		try {
			// Your implement here!
		} catch (Exception e) {
			LOGGER.debug(e.toString());
		}
	}

	public void viewImageOfProduct() throws AWTException {
		LOGGER.debug(String.format("viewImageOfProduct()"));
		// Your implement here!
	}

	public void viewDetailsOfProduct() {
		LOGGER.debug(String.format("viewDetailsOfProduct()"));
		// Your implement here!

	}

	@SuppressWarnings("deprecation")
	public void viewFeedbackOfProduct() {
		LOGGER.debug(String.format("viewFeedbackOfProduct()"));
		try {
			// Your implement here!
		} catch (Exception e) {
			LOGGER.error(e.toString());
		}
	}

	public void addToWatchList() {
		LOGGER.debug(String.format("addToWatchList()"));
		try {
			// Your implement here!
		} catch (Exception e) {
			LOGGER.error(e.toString());
		}
	}

	public void saveThisSeller() {
		LOGGER.debug(String.format("saveThisSeller()"));
		try {
			// Your implement here!
		} catch (Exception e) {
			LOGGER.error(e.toString());
		}

	}

	public int randomBetween(int min, int max) {
		LOGGER.debug(String.format("randomBetween(int %d, int %d)", min, max));
		if (min == max)
			return min;
		if (min > max) {
			int tmp = max;
			max = min;
			min = tmp;
		}
		Random random = new Random();
		int randomNumber = random.nextInt(max + 1 - min) + min;
		return randomNumber;
	}

	public boolean randomTrueFalse() {
		LOGGER.debug(String.format("randomTrueFalse()"));
		if (Math.random() < 0.5) {
			LOGGER.debug(String.format("True"));
			return true;
		}
		LOGGER.debug(String.format("False"));
		return false;
	}

	public int randomWithRatio(int min, int max) {
		LOGGER.debug(String.format("randomWithRatio(int min, int max)"));

		return 0;
	}

	public WebElement waitUntil(By what) {
		LOGGER.debug(String.format("waitUntil(By %s)", what.toString()));
		WebDriverWait wait = new WebDriverWait(driver, 10);
		@SuppressWarnings("unused")
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(what));
		return driver.findElement(what);
	}

	public boolean waitUntil(String storageKey) {
		LOGGER.debug(String.format("waitUntil(String %s)", storageKey));
		try {
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			int timeWait = 60;
			while (timeWait > 0) {
				String output = (String) jse
						.executeScript("return window.localStorage.getItem(\"" + storageKey + "\")");
				if (output != null && output.equals("true")) {
					return true;
				} else {
					System.out.println(String.valueOf(output));
				}
				waitFor(1000, 1000);
				timeWait--;
			}
			return false;
		} catch (Exception e) {
			LOGGER.error(e.toString());
			return false;
		}
	}

	public void scrollToElement(WebElement element) {
		LOGGER.debug(String.format("scrollToElement(WebElement element)"));
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("arguments[0].scrollIntoView({behavior: \"smooth\", block: \"center\", inline: \"center\"});",
				element);
		waitFor(2000, 4000);
	}

	public WebElement getElementByJs(String command) {
		LOGGER.debug(String.format("getElementByJs( `%s` )", command));
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		WebElement element = (WebElement) jse.executeScript("return " + command + "");
		return element;
	}
}
