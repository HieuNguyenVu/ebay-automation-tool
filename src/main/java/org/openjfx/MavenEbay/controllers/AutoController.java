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

			waitUntil(By.cssSelector("form:first-child"));
			waitUntil(By.cssSelector("div#mainContent"));

			List<String> keySearchs = Arrays.asList(PropertiesController.getPropertyAsString("taKeySearch").split(","))
					.stream().map(key -> key.trim()).collect(Collectors.toList());

			while (keySearchs.size() > 0) {
				try {
					int index = randomBetween(0, keySearchs.size() - 1);
					String key = keySearchs.get(index);
					keySearchs.remove(index);
					waitFor(1000, 1000);
					enterKeySearch(key);
					waitFor(PropertiesController.getPropertyAsInt("timeWaitAfterSearchMin"),
							PropertiesController.getPropertyAsInt("timeWaitAfterSearchMax"));
					viewSearchResult();
					waitFor(2000, 3000);
					clickOnProduct();
				} catch (Exception e) {
					LOGGER.error(e.toString());
					driver.navigate().to("https://www.ebay.com/");
				}
			}

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
		WebElement searchBar = waitUntil(By.xpath("//*[@id=\"gh-ac\"]"));
		searchBar.clear();
		fakeTyping(key, searchBar);
		waitFor(1000, 1500);
		// click search
		WebElement buttonSearch = waitUntil(By.xpath("//*[@id=\"gh-btn\"]"));
		buttonSearch.click();
		waitFor(3000, 4500);
	}

	@SuppressWarnings("deprecation")
	public void viewSearchResult() {
		LOGGER.debug(String.format("viewSearchResult()"));
		int beakTime = 10000;
		while (beakTime > 0) {
			beakTime -= waitFor(500, 1000);
			try {
				java.awt.Robot robot = new java.awt.Robot();
				robot.mouseMove(100, 300);
				robot.mousePress(InputEvent.BUTTON1_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_MASK);

				if (randomTrueFalse())
					robot.keyPress(KeyEvent.VK_PAGE_UP);
				else
					robot.keyPress(KeyEvent.VK_PAGE_DOWN);
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				LOGGER.debug(e.toString());
			}
		}
//Way 2
//		JavascriptExecutor jse = (JavascriptExecutor)driver;
//		jse.executeScript("window.scroll({  top: 100,  left: 100,  behavior: 'smooth'});");
	}

	public void backtoSearhResult() {
		LOGGER.debug(String.format("backtoSearhResult()"));
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript(
				"document.getElementById(\"smtBackToAnchorArrow\").scrollIntoView({behavior: \"smooth\", block: \"center\", inline: \"center\"});");
		WebElement backButton = waitUntil(By.id("smtBackToAnchorArrow"));
		waitFor(2000, 2000);
		backButton.click();
		waitFor(4000, 5000);
	}

	public void clickOnProduct() {
		LOGGER.debug(String.format("clickOnProduct()"));
		List<WebElement> elements = null;
		try {
			int timeToClickOnProduct = randomBetween(PropertiesController.getPropertyAsInt("timesClickToProductsMin"),
					PropertiesController.getPropertyAsInt("timesClickToProductsMax"));
			boolean isNeedToViewDetails = PropertiesController.getPropertyAsBool("isNeedToViewDetails");
			boolean isNeedToViewFeedback = PropertiesController.getPropertyAsBool("isNeedToViewFeedback");
			float ratioAddToWatchList = PropertiesController.getPropertyAsFloat("ratioAddToWatchList");
			float ratioSaveThisSeller = PropertiesController.getPropertyAsFloat("ratioSaveThisSeller");
			// Get result list type.
			JavascriptExecutor jse = (JavascriptExecutor) driver;

			elements = driver.findElements(By.cssSelector("#srp-river-results > ul > li.s-item"));
			int size = elements.size();
			while (timeToClickOnProduct >= 0) {
				try {
					int index = randomBetween(0, size - 1);
//				WebElement selectElement = elements.get(index);
					WebElement selectElement = getElementByJs("document.getElementsByClassName(\"s-item__image-wrapper\")[" + index +"];");
					scrollToElement(selectElement);
//					jse.executeScript("let tmp = document.getElementsByClassName(\"s-item__image-wrapper\")[" + index
//							+ "];"
//							+ "tmp.scrollIntoView({behavior: \"smooth\", block: \"center\", inline: \"center\"});"
//							+ "tmp.click()");
					selectElement.click();
					waitFor(3000, 3000);
					try {
						viewImageOfProduct();
					} catch (AWTException e) {
						// TODO Auto-generated catch block
						LOGGER.debug(e.toString());
					}

					waitFor(PropertiesController.getPropertyAsInt("timesWaitWhenViewImageMin"),
							PropertiesController.getPropertyAsInt("timesWaitWhenViewImageMax"));
					if (isNeedToViewDetails) {
						viewDetailsOfProduct();
					}
					waitFor(PropertiesController.getPropertyAsInt("timesWaitAfterViewDetailsMin"),
							PropertiesController.getPropertyAsInt("timesWaitAfterViewDetailsMax"));
					if (isNeedToViewFeedback) {
						viewFeedbackOfProduct();
					}
					waitFor(1000, 3000);
					if (Math.random() <= ratioAddToWatchList) {
						addToWatchList();
					}
					waitFor(1000, 3000);
					if (Math.random() <= ratioSaveThisSeller) {
						saveThisSeller();
					}
					waitFor(1000, 3000);
					backtoSearhResult();

					waitFor(2000, 3000);
					elements = driver.findElements(By.cssSelector("#srp-river-results > ul > li.s-item"));
					size = elements.size();

					timeToClickOnProduct--;
				} catch (Exception e) {
					LOGGER.debug(e.toString());
					waitFor(1000, 3000);
					backtoSearhResult();
					elements = driver.findElements(By.cssSelector("#srp-river-results > ul > li.s-item"));
				}
			}
		} catch (Exception e) {
			LOGGER.debug(e.toString());
		}
	}

	public void viewImageOfProduct() throws AWTException {
		LOGGER.debug(String.format("viewImageOfProduct()"));
		// Get Image pane
		WebElement imagePane = waitUntil(By.id("icImg"));
		// Get product list
		List<WebElement> productImages = driver.findElements(By.cssSelector("#vi_main_img_fs > ul > li"));
		int timesViewImage = randomBetween(1, productImages.size());
		LOGGER.debug(String.format("TOTAL IMAGE VIEW %d", timesViewImage));
		WebElement nextButton = waitUntil(By.cssSelector("button.next-arr.navigation-image-arr"));

		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript(
				"document.getElementById(\"icImg\").scrollIntoView({behavior: \"smooth\", block: \"center\", inline: \"center\"});");
		while (timesViewImage > 0) {
			fakeMouseHover(imagePane);
			waitFor(1000, 2000);
			nextButton.click();
			waitFor(500, 1000);
			timesViewImage--;
		}
	}

	public void viewDetailsOfProduct() {
		LOGGER.debug(String.format("viewDetailsOfProduct()"));
		try {
			WebElement panelDetail = driver
					.findElement(By.cssSelector("#BottomPanel > div.tabbable > div.tab-content-m"));
			int panelHeigh = panelDetail.getRect().height;

			driver.findElement(By.cssSelector("#BottomPanel > div.tabbable > ul > li:first-child"));

			// After this command screen will scroll to detail.
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript(
					"document.getElementById(\"viTabs_0\").scrollIntoView({behavior: \"smooth\", block: \"start\", inline: \"center\"});");
			// This script will scroll over detail
			String jsScript = "localStorage.setItem(\"viewDetailsOfProductFinish\",\"false\");\r\n"
					+ "function getRandomInt(min, max) {\r\n" + "    min = Math.ceil(min);\r\n"
					+ "    max = Math.floor(max);\r\n"
					+ "    return Math.floor(Math.random() * (max - min + 1)) + min;\r\n" + "}\r\n" + "\r\n"
					+ "let scroller = " + panelDetail.getRect().y + ";\r\n" + "let maxHeight = "
					+ (panelDetail.getRect().y + panelHeigh) + ";\r\n" + "let windowHeight = window.innerHeight;\r\n"
					+ "\r\n" + "function scrollFunc(){\r\n" + "	return new Promise((resolve) => {\r\n"
					+ "    setTimeout(()=>{\r\n"
					+ "        window.scroll({ top: scroller,  left: 0,  behavior: 'smooth'});\r\n"
					+ "        scroller += getRandomInt(300,1000);\r\n" + "        resolve();\r\n"
					+ "    },getRandomInt(500,2500))}); \r\n" + "};\r\n"
					+ "while(scroller + windowHeight < maxHeight){\r\n" + "    await scrollFunc();\r\n" + "}"
					+ "localStorage.setItem(\"viewDetailsOfProductFinish\",\"true\");";
			jse.executeScript(jsScript);
		} catch (Exception e) {
			LOGGER.error(e.toString());
		}

	}

	@SuppressWarnings("deprecation")
	public void viewFeedbackOfProduct() {
		LOGGER.debug(String.format("viewFeedbackOfProduct()"));
		try {
			WebElement soldWithFeedback = driver.findElement(By.cssSelector(".soldwithfeedback"));
			if (soldWithFeedback != null) {
				WebElement feedback = soldWithFeedback.findElement(By.cssSelector("span:last-child"));
				feedback.click();
				WebElement scroller = waitUntil(By.cssSelector("div.POSITIVE > div:nth-child(2) > div"));
				Rectangle rtangle = scroller.getRect();
				java.awt.Robot robot = new java.awt.Robot();
				robot.mouseMove(rtangle.x + (rtangle.width / 2), rtangle.y + 30 + (rtangle.height / 2));
				robot.mousePress(InputEvent.BUTTON1_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
				int wheelTime = 10;
				while (wheelTime > 0) {
					if (randomTrueFalse()) {
						robot.mouseWheel(5);
					} else {
						robot.mouseWheel(-5);
					}
					waitFor(500, 1000);
					wheelTime--;
				}
				waitFor(500, 1000);
				WebElement neutrual = waitUntil(By.cssSelector("div.POSITIVE > div:nth-child(1) > div.neutral"));
				neutrual.click();
				robot.mouseMove(rtangle.x + (rtangle.width / 2), rtangle.y + 30 + (rtangle.height / 2));
				robot.mousePress(InputEvent.BUTTON1_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
				wheelTime = 5;
				while (wheelTime > 0) {
					if (randomTrueFalse()) {
						robot.mouseWheel(5);
					} else {
						robot.mouseWheel(-5);
					}
					waitFor(500, 1000);
					wheelTime--;
				}
				WebElement negative = waitUntil(By.cssSelector("div.NEUTRAL > div:nth-child(1) > div.negative"));
				negative.click();
				robot.mouseMove(rtangle.x + (rtangle.width / 2), rtangle.y + 30 + (rtangle.height / 2));
				robot.mousePress(InputEvent.BUTTON1_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
				wheelTime = 5;
				while (wheelTime > 0) {
					if (randomTrueFalse()) {
						robot.mouseWheel(5);
					} else {
						robot.mouseWheel(-5);
					}
					waitFor(500, 1000);
					wheelTime--;
				}
				WebElement clzButton = waitUntil(By.cssSelector("button.clzBtn"));
				clzButton.click();
			}
		} catch (Exception e) {
			LOGGER.error(e.toString());
		}
	}

	public void addToWatchList() {
		LOGGER.debug(String.format("addToWatchList()"));
		try {
			WebElement addToWatchList = waitUntil(By.id("watchWrapperId"));
			scrollToElement(addToWatchList);
			addToWatchList = waitUntil(By.id("watchWrapperId"));
			addToWatchList.click();
		} catch (Exception e) {
			LOGGER.error(e.toString());
		}
	}

	public void saveThisSeller() {
		LOGGER.debug(String.format("saveThisSeller()"));
		try {
			WebElement saveThisSeller = waitUntil(By.cssSelector("#followSeller > div > a > span"));
			scrollToElement(saveThisSeller);
			saveThisSeller = waitUntil(By.cssSelector("#followSeller > div > a > span"));
			saveThisSeller.click();
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
