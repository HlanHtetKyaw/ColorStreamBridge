package tt;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import purejavacomm.CommPort;
import purejavacomm.CommPortIdentifier;
import purejavacomm.SerialPort;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import purejavacomm.CommPortIdentifier;
import java.util.Enumeration;
import java.io.BufferedReader;
import java.io.*;
import javafx.concurrent.Task;
public class Main extends Application {
	Image image = new Image("camera.png");

	private ImageView imageView = new ImageView(image);
	private ImageView settingView = new ImageView(new Image("setting.png"));
	private ImageView refreshView = new ImageView(new Image("refresh.png"));
	private Image logo = new Image("logo.png");
	private VideoCapture capture;
	private Button b1;
	private Button settingButt;

	Mat hsvFrame = new Mat();
	Mat maskRed = new Mat();
	Mat maskBlue = new Mat();
	Mat maskGreen = new Mat();
	Mat colorMask = new Mat();
	Mat frameMat = new Mat();
	String data = "n";
	String text;
	private Label l1 = new Label();

	ObservableList<String> portNames = getAvailablePorts();
	ComboBox<String> comboBox = new ComboBox<>(portNames);
	ComboBox<String> cb = new ComboBox<String>();;
	ComboBox<Integer> appWaitTime= new ComboBox<Integer>();
	boolean stopCap = true;
	boolean settingNotPress = true;
	Mat frameMatloop;

	public void checkSensor() {
		try {
			if (comboBox.getValue().equals("Choose Port")) {
				System.out.println("Choose Port");
			} else {
				CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(comboBox.getValue());
				SerialPort serialPort = null;
				serialPort = (SerialPort) portId.open("ArduinoSerialCommunication", 2000);
				serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);

				System.out.println("Port opened successfully.");
				BufferedReader reader = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
				;
				while (true) {
					if (reader.ready()) {
						reader = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
						String inputLine = reader.readLine();

						if (inputLine.equals("yes")) {
							serialPort.close();
							b1.fire();
							System.out.println("Arduino says: " + inputLine);
							break;
						} else {
							System.out.println("Arduino says: " + inputLine);
						}
						System.out.println("while loop continue");
					}
					Thread.sleep(100);

				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	public void newStage() {
		
		Stage st = new Stage();
		Button refresh = new Button();
		refresh.setGraphic(refreshView);
		refreshView.setFitWidth(30);
		refreshView.setFitHeight(30);
		refresh.getStyleClass().add("settingButt");
		st.setTitle("Set Up");
		StackPane sp = new StackPane();
		Button b = new Button("Confirm");
		b.setFont(new Font(20));
		b.setMaxSize(100, 50);
		Label cam = new Label("Camera :");
		Label port = new Label("Port :");
		Label waitTime = new Label("\tApplication\n Waiting Time(ms): ");


		StackPane.setAlignment(appWaitTime, Pos.TOP_RIGHT);
		StackPane.setMargin(appWaitTime, new Insets(20, 10, 0, 0));
		StackPane.setAlignment(cam, Pos.TOP_LEFT);
		StackPane.setMargin(cam, new Insets(25, 0, 0, 20));
		StackPane.setAlignment(cb, Pos.TOP_LEFT);
		StackPane.setMargin(cb, new Insets(20, 0, 0, 70));
		StackPane.setAlignment(waitTime, Pos.TOP_RIGHT);
		StackPane.setMargin(waitTime, new Insets(20, 90, 0, 0));

		if (settingNotPress) {
			try {
				comboBox.setValue("Choose Port");
				cb.getItems().addAll("On", "Off");
				cb.setValue(cb.getItems().getFirst());
				appWaitTime.setValue(5000);
				appWaitTime.getItems().addAll(100, 200, 300, 400, 500, 1000, 2000, 3000, 4000, 5000, 7000, 8000, 10000);
			} catch (Exception e) {

			}
		}
		
		StackPane.setAlignment(refresh, Pos.BOTTOM_LEFT);
		StackPane.setAlignment(port, Pos.TOP_CENTER);
		StackPane.setMargin(port, new Insets(25, 140, 0, 0));
		StackPane.setAlignment(comboBox, Pos.TOP_CENTER);
		StackPane.setMargin(comboBox, new Insets(20, 20, 0, 16));
		StackPane.setAlignment(refresh, Pos.BOTTOM_CENTER);
		StackPane.setMargin(refresh, new Insets(0,0,30,0));
		b.setOnAction(e -> {
			//new Thread(this::checkSensor).start();
			System.out.println(cb.getValue());
			cb.setValue(cb.getValue());
			comboBox.setValue(comboBox.getValue());
			appWaitTime.setValue(appWaitTime.getValue());
			st.close();
		});
		refresh.setOnAction(e->{
			getAvailablePorts();
			comboBox.setItems(portNames);
		});
		sp.getChildren().addAll(b, cam, cb, comboBox, port, appWaitTime, waitTime,refresh);
		sp.getStyleClass().add("backgroundColor");
		Scene scene = new Scene(sp, 500, 200);
		st.getIcons().add(logo);
		scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
		st.setScene(scene);
		st.setResizable(false);
		st.showAndWait();
	}

	StackPane root;
	int camWaitTime;

	@Override
	public void start(Stage primaryStage) {
		newStage();
		StackPane sar = new StackPane();
		sar.setMaxSize(300, 100);
		sar.setStyle("-fx-background-color: gray;");
		root = new StackPane();

		b1 = new Button("Capture");
		settingButt = new Button();
		settingButt.setGraphic(settingView);
		settingView.setFitWidth(50);
		settingView.setFitHeight(50);
		settingButt.getStyleClass().add("settingButt");
		settingButt.setOnAction(e -> {
			settingNotPress = false;
			newStage();
		});
		Label camLabel = new Label("Camera Waiting Time: ");
		ComboBox<Integer> camWaitBox = new ComboBox<Integer>();
		camLabel.setVisible(false);
		camWaitBox.setVisible(false);
		camWaitBox.setValue(1000);
		camWaitBox.getItems().addAll(100, 200, 300, 400, 500, 1000, 2000, 3000, 4000, 5000, 7000, 8000, 10000);
		StackPane.setAlignment(camWaitBox, Pos.TOP_CENTER);
		StackPane.setMargin(camWaitBox, new Insets(100, 0, 0, 0));
		StackPane.setAlignment(camLabel, Pos.TOP_CENTER);
		StackPane.setMargin(camLabel, new Insets(100, 200, 0, 0));
		l1.setFont(new Font("Arial", 17));
		l1.setTextFill(Color.WHITE);
		
		b1.setOnAction(e -> {
			camWaitTime = camWaitBox.getValue();
			try {
				Thread.sleep(camWaitTime);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			new Thread(this::processFrame).start();

			stopCap = false;
		});
		StackPane.setAlignment(b1, Pos.BOTTOM_CENTER);
		StackPane.setMargin(b1, new Insets(0, 0, 100, 0));
		StackPane.setAlignment(settingButt, Pos.TOP_RIGHT);
		StackPane.setMargin(settingButt, new Insets(20, 20, 0, 0));
		StackPane.setMargin(imageView, new Insets(0, 350, 0, 0));
		StackPane.setMargin(sar, new Insets(0, 0, 0, 250));
		imageView.setFitWidth(200);
		imageView.setFitHeight(200);
		root.getChildren().addAll(imageView, b1, sar, camWaitBox, camLabel, settingButt);
		sar.getChildren().add(l1);
		root.getStyleClass().add("backgroundColor");
		Scene scene = new Scene(root, 700, 600);
		scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
		primaryStage.getIcons().add(logo);
		primaryStage.setTitle("ColorStreamBridge");
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest(e -> handleClose(primaryStage));

		capture = new VideoCapture(0);

		if (!capture.isOpened()) {
			System.out.println("Error: Camera not opened!");
			Platform.exit();
		}

		// Start continuous frame capture and display
		if (cb.getValue() == "On") {
			new Thread(this::captureAndDisplay).start();
		}
	}

	static {
		String libraryPath = "/lib/";
		String libraryName = "opencv_java480.dll";// for windows 64-bit operation system

		try {
			InputStream in = Main.class.getResourceAsStream(libraryPath + libraryName);

			// Create a temporary file for the native library
			File fileOut = File.createTempFile("lib", ".dll");
			fileOut.deleteOnExit();

			try (FileOutputStream out = new FileOutputStream(fileOut)) {
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = in.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
				}
			}

			// Load the native library
			System.load(fileOut.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		launch(args);
	}

	private ObservableList<String> getAvailablePorts() {
		portNames = FXCollections.observableArrayList();

		Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
		while (portList.hasMoreElements()) {
			CommPortIdentifier portId = portList.nextElement();
			System.out.println("Port Name: " + portId.getName());
			portNames.add(portId.getName());
		}

		return portNames;
	}

	private void handleClose(Stage stage) {
		stage.close();
		capture.release();
		Platform.exit();
		Thread.currentThread().interrupt();
		hsvFrame.release();
		maskRed.release();
		maskBlue.release();
		maskGreen.release();
		colorMask.release();
		frameMat.release();
		System.exit(0);
	}

	private void captureAndDisplay() {
		frameMatloop = new Mat();

		while (stopCap == true) {
			capture.read(frameMatloop);
			Platform.runLater(() -> updateImageView(mat2Image(frameMatloop)));

			try {
				Thread.sleep(30); // Adjust delay to control frame rate
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	boolean isRedPresent = false;
	boolean isBluePresent = false;
	boolean isGreenPresent = false;

	private void drawContoursAndHighlightRed(Mat originalImage, List<MatOfPoint> contours, Mat maskRed, int rV, int gV,
			int bV) {
		Mat overlay = originalImage.clone();

		// Highlight regions
		for (MatOfPoint contour : contours) {
			Rect boundingRect = Imgproc.boundingRect(contour);
			if (Core.countNonZero(maskRed.submat(boundingRect)) > 50) {
				Imgproc.rectangle(overlay, boundingRect.tl(), boundingRect.br(), new Scalar(bV, gV, rV), 3);
			}
		}

		// Convert the overlay to JavaFX Image and update the ImageView
		Image processedImage = mat2Image(overlay);
		updateImageView(processedImage);
	}

	private void processFrame() {
		frameMat = new Mat();
		capture.read(frameMat);
		hsvFrame = new Mat();
		Imgproc.cvtColor(frameMat, hsvFrame, Imgproc.COLOR_BGR2HSV);

		// Red color range
		Scalar lowerBoundRed = new Scalar(0, 100, 100);// HSV
		Scalar upperBoundRed = new Scalar(10, 255, 255);

		// Blue color range
		Scalar lowerBoundBlue = new Scalar(100, 100, 100);
		Scalar upperBoundBlue = new Scalar(130, 255, 255);

		// Green color range
		Scalar lowerBoundGreen = new Scalar(40, 100, 100);
		Scalar upperBoundGreen = new Scalar(80, 255, 255);

		maskRed = new Mat();
		maskBlue = new Mat();
		maskGreen = new Mat();

		// Create masks for each color
		Core.inRange(hsvFrame, lowerBoundRed, upperBoundRed, maskRed);
		Core.inRange(hsvFrame, lowerBoundBlue, upperBoundBlue, maskBlue);
		Core.inRange(hsvFrame, lowerBoundGreen, upperBoundGreen, maskGreen);

		// Combine masks to get a complete mask
		colorMask = new Mat();
		Core.bitwise_or(maskRed, maskBlue, colorMask);
		Core.bitwise_or(colorMask, maskGreen, colorMask);

		List<MatOfPoint> contours = new ArrayList<>();
		Imgproc.findContours(colorMask, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

		double totalRedArea = 0;
		double totalBlueArea = 0;
		double totalGreenArea = 0;

		for (MatOfPoint contour : contours) {
			double area = Imgproc.contourArea(contour);
			if (area > 200) { // Adjust area threshold
				// Check the color present within the contour
				double redArea = Core.countNonZero(maskRed.submat(Imgproc.boundingRect(contour)));
				double blueArea = Core.countNonZero(maskBlue.submat(Imgproc.boundingRect(contour)));
				double greenArea = Core.countNonZero(maskGreen.submat(Imgproc.boundingRect(contour)));
				// Update the total areas for each color
				totalRedArea += redArea;
				totalBlueArea += blueArea;
				totalGreenArea += greenArea;

			}
		}

		// Determine the color based on the total areas
		if (totalGreenArea > totalRedArea && totalGreenArea > totalBlueArea) {
			isGreenPresent = true;
		} else if (totalRedArea > totalBlueArea && totalRedArea > totalGreenArea) {
			isRedPresent = true;
		} else if (totalBlueArea > totalRedArea && totalBlueArea > totalGreenArea) {
			isBluePresent = true;
		}

		Platform.runLater(() -> {
			if (isRedPresent) {

				text = "Red Color is detected\n Wait " + appWaitTime.getValue() + " milli seconds\n";
				l1.setText(text);
				SendDataToArduino("r");
				isRedPresent = false;
				drawContoursAndHighlightRed(frameMat, contours, maskRed, 255, 0, 0);
				Platform.runLater(() -> {

					try {

						Thread.sleep(appWaitTime.getValue());

						//new Thread(this::checkSensor).start();
						stopCap = true;
						if (cb.getValue() == "On") {
							new Thread(this::captureAndDisplay).start();
						}
						l1.setText("Done");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				});

			} else if (isBluePresent) {

				text = "Blue Color is detected\n Wait " + appWaitTime.getValue() + " milli seconds\n";
				l1.setText(text);
				SendDataToArduino("b");
				
				isBluePresent = false;
				drawContoursAndHighlightRed(frameMat, contours, maskBlue, 0, 0, 255);
				Platform.runLater(() -> {

					try {
						Thread.sleep(appWaitTime.getValue());
						// Update UI or perform other actions after 5 seconds
						//new Thread(this::checkSensor).start();
						stopCap = true;
						if (cb.getValue() == "On") {
							new Thread(this::captureAndDisplay).start();
						}

						l1.setText("Done");
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			} else if (isGreenPresent) {

				text = "Green color is detected\n Wait " + appWaitTime.getValue() + " milli seconds\n";
				l1.setText(text);
				SendDataToArduino("g");
				isGreenPresent = false;
				drawContoursAndHighlightRed(frameMat, contours, maskGreen, 0, 255, 0);
				Platform.runLater(() -> {

					try {
						Thread.sleep(appWaitTime.getValue());
						// Update UI or perform other actions after 5 seconds
						//new Thread(this::checkSensor).start();
						stopCap = true;
						if (cb.getValue() == "On") {
							new Thread(this::captureAndDisplay).start();
						}
						l1.setText("Done");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				});
			} else {
				text = "No Color is detected\n Wait " + appWaitTime.getValue() + " milli seconds\n";
				l1.setText(text);
				// Schedule a task to run after 5 seconds
				new Thread(() -> {
					try {
						Thread.sleep(appWaitTime.getValue());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// Update UI or perform other actions after 5 seconds
					Platform.runLater(() -> {
						l1.setText("Done");
						stopCap = true;
						if (cb.getValue() == "On") {
							new Thread(this::captureAndDisplay).start();
						}
						new Thread(this::checkSensor).start();
					});
				}).start();
				//new Thread(this::checkSensor).start();
			}
		});
	}

	private void updateImageView(Image image) {
		imageView.setImage(image);
	}

	private Image mat2Image(Mat mat) {
		MatOfByte byteMat = new MatOfByte();
		Imgcodecs.imencode(".png", mat, byteMat);
		return new Image(new java.io.ByteArrayInputStream(byteMat.toArray()));
	}

	public void SendDataToArduino(String data) {
		String portName = comboBox.getValue();
		int baudRate = 9600;
		SerialPort serialPort = null;

		try {
			if (comboBox.getValue().equals("Choose Port")) {
				System.out.println("Choose Port");
			} else {
				CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);

				if (portIdentifier.isCurrentlyOwned()) {
					System.out.println("Error: Port is currently in use");
				} else {
					CommPort commPort = portIdentifier.open("ArduinoCommunication", 2000);
					if (commPort instanceof SerialPort) {
						serialPort = (SerialPort) commPort;
						serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
								SerialPort.PARITY_NONE);

						OutputStream outputStream = serialPort.getOutputStream();

						String dataToSend = data;
						outputStream.write(dataToSend.getBytes());

						Thread.sleep(500);

						commPort.close();

					} else {
						System.out.println("invalid port number");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void stop() {
		// Release resources when the application is closed
		if (capture != null) {
			capture.release();
		}
	}
}
