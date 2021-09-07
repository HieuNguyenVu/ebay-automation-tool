package org.openjfx.MavenEbay.controllers;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
//import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class FXMLController extends TimerTask {

	@FXML
	private TextField profileDir;

	@FXML
	private TextArea taProfileList;

	@FXML
	private TextArea taKeySearch;

	@FXML
	private Button btnStart;

	@FXML
	private Button btnSave;

	@FXML
	private Pane root;

	@FXML
	private TextField timeWaitAfterSearchMin;
	@FXML
	private TextField timeWaitAfterSearchMax;

	@FXML
	private TextField timesClickToProductsMin;
	@FXML
	private TextField timesClickToProductsMax;

	@FXML
	private CheckBox isNeedToViewDetails;
	@FXML
	private CheckBox isNeedToViewFeedbacks;

	@FXML
	private TextField timesWaitAfterViewDetailsMin;
	@FXML
	private TextField timesWaitAfterViewDetailsMax;

	@FXML
	private TextField ratioAddToWatchList;
	@FXML
	private TextField ratioSaveThisSeller;

	@FXML
	private TextField timesWaitWhenViewImageMin;
	@FXML
	private TextField timesWaitWhenViewImageMax;

	@FXML
	private TextField timeSchedulerHour;
	@FXML
	private TextField timeSchedulerMinute;

	// Add a public no-args constructor
	boolean isStart = false;
	boolean isStopping = false;
	boolean isScheduled = false;

	private final static long fONCE_PER_DAY = 1000 * 60 * 60 * 24;
	private static Logger LOGGER = LoggerFactory.getLogger(FXMLController.class);

	Thread runner;
	AutoController autoGuy;
	Timer timer;

	public FXMLController() {

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		LOGGER.info("Run tool on schedule");
		onStart(new ActionEvent());
	}

	public void initialize() {
//        String javaVersion = System.getProperty("java.version");
//        String javafxVersion = System.getProperty("javafx.version");
//        label.setText("Hello, JavaFX " + javafxVersion + "\nRunning on Java " + javaVersion + ".");
	}

	private static Date getNearestRunTimes(int hour, int minutes) {
		LOGGER.info(String.format("getNearestRunTimes(int %d, int %d)", hour, minutes));
		Calendar tomorrow = new GregorianCalendar();
		if (tomorrow.get(Calendar.HOUR_OF_DAY) >= hour && tomorrow.get(Calendar.MINUTE) >= minutes) {
			tomorrow.add(Calendar.DATE, 1);
		}
		tomorrow.set(Calendar.HOUR_OF_DAY, 0);
		tomorrow.add(Calendar.HOUR_OF_DAY, hour);
		tomorrow.set(Calendar.MINUTE, 0);
		tomorrow.add(Calendar.MINUTE, minutes);
		Calendar result = new GregorianCalendar(tomorrow.get(Calendar.YEAR), tomorrow.get(Calendar.MONTH),
				tomorrow.get(Calendar.DATE), hour, minutes);
		LOGGER.info(String.format("Nearest Runtimes is %s", result.getTime().toString()));
		return result.getTime();
	}

	public void initValue() {
		LOGGER.info(String.format("initValue()"));
		try {
			PropertiesController.initControl();
			profileDir.setText(PropertiesController.getPropertyAsString("profileDir"));
			taProfileList.setText(PropertiesController.getPropertyAsString("taProfileList"));
			taKeySearch.setText(PropertiesController.getPropertyAsString("taKeySearch"));
			timeWaitAfterSearchMin.setText(PropertiesController.getPropertyAsIntString("timeWaitAfterSearchMin"));
			timeWaitAfterSearchMax.setText(PropertiesController.getPropertyAsIntString("timeWaitAfterSearchMax"));
			timesClickToProductsMin.setText(PropertiesController.getPropertyAsIntString("timesClickToProductsMin"));
			timesClickToProductsMax.setText(PropertiesController.getPropertyAsIntString("timesClickToProductsMax"));

			if (PropertiesController.getPropertyAsString("isNeedToViewDetails") != null
					&& PropertiesController.getPropertyAsString("isNeedToViewDetails").equals("true")) {
				isNeedToViewDetails.setSelected(true);
			} else {
				isNeedToViewDetails.setSelected(false);
			}

			if (PropertiesController.getPropertyAsString("isNeedToViewFeedbacks") != null
					&& PropertiesController.getPropertyAsString("isNeedToViewFeedbacks").equals("true")) {
				isNeedToViewFeedbacks.setSelected(true);
			} else {
				isNeedToViewFeedbacks.setSelected(false);
			}

			timesWaitAfterViewDetailsMin
					.setText(PropertiesController.getPropertyAsIntString("timesWaitAfterViewDetailsMin"));
			timesWaitAfterViewDetailsMax
					.setText(PropertiesController.getPropertyAsIntString("timesWaitAfterViewDetailsMax"));

			ratioAddToWatchList.setText(PropertiesController.getPropertyAsFloatString("ratioAddToWatchList"));
			ratioSaveThisSeller.setText(PropertiesController.getPropertyAsFloatString("ratioSaveThisSeller"));

			timesWaitWhenViewImageMin.setText(PropertiesController.getPropertyAsIntString("timesWaitWhenViewImageMin"));
			timesWaitWhenViewImageMax.setText(PropertiesController.getPropertyAsIntString("timesWaitWhenViewImageMax"));

			timeSchedulerHour.setText(PropertiesController.getPropertyAsIntString("timeSchedulerHour"));
			timeSchedulerMinute.setText(PropertiesController.getPropertyAsIntString("timeSchedulerMinute"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reSchedule() {
		try {
			if (timer != null) {
				timer.cancel();
			}
		} catch (Exception e) {
			// Do nothing.
		}
		timer = new Timer();
		timer.scheduleAtFixedRate(this, getNearestRunTimes(PropertiesController.getPropertyAsInt("timeSchedulerHour"),
				PropertiesController.getPropertyAsInt("timeSchedulerMinute")), fONCE_PER_DAY);
	}

	@SuppressWarnings("deprecation")
	@FXML
	private void onStart(ActionEvent event) {
		LOGGER.info(String.format("onStart()"));
		try {
			event.consume();
			if (!isStart) {
				btnStart.setText("Stop");
				isStart = true;
				saveConfig();
				autoGuy = new AutoController(null);

			} else {
				btnStart.setDisable(true);
				btnStart.setText("Stopping");
				isStart = false;
				isStopping = true;
				autoGuy.closeAnyWay();
				Thread.sleep(1000);
				runner.stop();
				isStopping = false;
				btnStart.setText("Start");
				btnStart.setDisable(false);
				return;
			}
			String keySearch = taKeySearch.getText();
			System.out.println(keySearch);
			runner = new Thread(() -> {
				List<String> profiles = Arrays
						.asList(PropertiesController.getPropertyAsString("taProfileList").split(",")).stream()
						.map(key -> key.trim()).collect(Collectors.toList());
				profiles.forEach(name -> {
					if (isStart) {
						autoGuy.openBrowserAndStart(name);
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				// Call again to stop
				onStart(new ActionEvent());
			});
			runner.start();
		} catch (Exception e) {
			LOGGER.error(e.toString());
			btnStart.setDisable(true);
			btnStart.setText("Stopping");
			isStart = false;
			isStopping = true;
			if( autoGuy!= null)	autoGuy.closeAnyWay();
			autoGuy.closeAnyWay();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			runner.stop();
			isStopping = false;
			btnStart.setText("Start");
			btnStart.setDisable(false);
			return;
		}
	}

	@FXML
	private void onSave(ActionEvent event) {
		LOGGER.debug(String.format("onSave()"));
		try {
			event.consume();
			if (!isStart && !isStopping) {
				saveConfig();
				if (!isScheduled) {
					reSchedule();
					isScheduled = true;
					DialogController.showAlertWithoutHeaderText("Save config success!!!");
				} else {
					DialogController.showAlertWithoutHeaderText(
							"Config save success!!!\nBut new scheduled cannot apply, please reset application and click save config again");
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.toString());
		}
	}

	private void saveConfig() {
		LOGGER.debug(String.format("saveConfig()"));

		PropertiesController.setPropertyAsString("profileDir", profileDir.getText());
		PropertiesController.setPropertyAsString("taProfileList", taProfileList.getText());
		PropertiesController.setPropertyAsString("taKeySearch", taKeySearch.getText());

		PropertiesController.setPropertyAsInt("timeWaitAfterSearchMin", timeWaitAfterSearchMin.getText());
		PropertiesController.setPropertyAsInt("timeWaitAfterSearchMax", timeWaitAfterSearchMax.getText());

		PropertiesController.setPropertyAsInt("timesClickToProductsMin", timesClickToProductsMin.getText());
		PropertiesController.setPropertyAsInt("timesClickToProductsMax", timesClickToProductsMax.getText());

		PropertiesController.setPropertyAsString("isNeedToViewDetails",
				String.valueOf(isNeedToViewDetails.isSelected()));
		PropertiesController.setPropertyAsString("isNeedToViewFeedbacks",
				String.valueOf(isNeedToViewFeedbacks.isSelected()));

		PropertiesController.setPropertyAsInt("timesWaitAfterViewDetailsMin", timesWaitAfterViewDetailsMin.getText());
		PropertiesController.setPropertyAsInt("timesWaitAfterViewDetailsMax", timesWaitAfterViewDetailsMax.getText());

		PropertiesController.setPropertyAsFloat("ratioAddToWatchList", ratioAddToWatchList.getText());
		PropertiesController.setPropertyAsFloat("ratioSaveThisSeller", ratioSaveThisSeller.getText());

		PropertiesController.setPropertyAsInt("timesWaitWhenViewImageMin", timesWaitWhenViewImageMin.getText());
		PropertiesController.setPropertyAsInt("timesWaitWhenViewImageMax", timesWaitWhenViewImageMax.getText());

		PropertiesController.setPropertyAsInt("timeSchedulerHour", timeSchedulerHour.getText());
		PropertiesController.setPropertyAsInt("timeSchedulerMinute", timeSchedulerMinute.getText());

		PropertiesController.store();
	}
}
