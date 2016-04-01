package com.cho.cleartext;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {
	List<String> endMarks = Arrays.asList("," , ".", "?" , "!", " ", "\r");
	List<String> validWords = null;
	
	public static void main(String[] args) {
	    launch(args);
	}
	
	public void start(Stage stage) {
		
		try {
			validWords = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/en.txt")))
					.lines().collect(Collectors.toList());
		} catch (Exception ex) {
			ex.printStackTrace();
			(new Alert(AlertType.ERROR, "Can't find vocabulary data.", ButtonType.CLOSE)).showAndWait();
			System.exit(-1);
		}
		
		TextArea textArea = new TextArea();
		textArea.setStyle("-fx-padding: 5px; -fx-font-size: 24px;");
		textArea.addEventHandler(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {			
			@Override
			public void handle(KeyEvent e) {
				check(e);
			}
		});
		textArea.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event e) {
				e.consume();
				((TextArea) e.getSource()).end();
			}
		});

		VBox root = new VBox();
		root.setStyle("-fx-min-height: 480px; -fx-min-width: 640px;");
		root.getChildren().add(textArea);
		VBox.setVgrow(textArea, Priority.ALWAYS);
		
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.setTitle("ClearText in JavaFX");
		stage.show();
	}

	private void check(KeyEvent e) {
		TextArea textArea = (TextArea) e.getSource();
		String currentChar = e.getCharacter();
		
		if(!endMarks.contains(currentChar)) {
			return;
		}
		
		textArea.selectPreviousWord();
		String word = textArea.getSelectedText().replaceAll("\n", "")
				.chars()
				.mapToObj(i -> String.valueOf((char)i).toLowerCase())
				.filter(c -> !endMarks.contains(c))
				.collect(Collectors.joining());
		
		if(!word.matches("[-+]?\\d*\\.?\\d+") && !validWords.contains(word)) {
			//System.out.println("invalid Word");
			animation(textArea, 10);
			e.consume();
		} else {
			textArea.end();
		}
	}
	
	private void animation(Node node, int x) {
		
		Timeline timeline = new Timeline(
			new KeyFrame(Duration.millis(0), new KeyValue(node.translateXProperty(), 0)),
			new KeyFrame(Duration.millis(50), new KeyValue(node.translateXProperty(), x)),
			new KeyFrame(Duration.millis(100), new KeyValue(node.translateXProperty(), -0.8*x)),
			new KeyFrame(Duration.millis(150), new KeyValue(node.translateXProperty(), 0))
				);
		timeline.play();
		
	}
	
}
