package com.s1scottd;

import org.kordamp.ikonli.javafx.FontIcon;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.List;

public class VisualPostfixCalculator extends Application {

  private TextField display;
  private LinkedListStack<Double> stack; // Custom stack implementation
  private VBox stackDisplay;
  private boolean clearDisplayOnNextDigit = false;

  @Override
  public void start(Stage primaryStage) {
    stack = new LinkedListStack<>(); // Initialize custom stack
    display = new TextField();
    display.setEditable(false);

    InputStream stream = getClass().getResourceAsStream("/icons/divide-solid.svg");
    if (stream == null) {
      System.out.println("Resource not found: /icons/divide-solid.svg");
    }

    // Main Layout
    BorderPane root = new BorderPane();

    // Stack Display
    stackDisplay = new VBox();
    stackDisplay.setPadding(new Insets(10));
    stackDisplay.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 25;");

    // Wrap stackDisplay in a ScrollPane
    ScrollPane scrollingStackDisplay = new ScrollPane(stackDisplay);
    scrollingStackDisplay.setFitToWidth(true);
    scrollingStackDisplay.setPrefWidth(100);
    scrollingStackDisplay.setStyle("-fx-background: #F0F0F0; -fx-border-color: black; -fx-border-width: 1;");

    // Label stackTitle = new Label("Stack:");
    // stackDisplay.getChildren().add(stackTitle);
    updateStackDisplay();

    // Calculator Grid
    GridPane grid = new GridPane();
    grid.setPadding(new Insets(10));
    grid.setHgap(5);
    grid.setVgap(5);

    // Add display field to grid
    grid.add(display, 0, 0, 4, 1);

    String[][] buttonLabels = {
        { "7", "8", "9", "/" },
        { "4", "5", "6", "*" },
        { "1", "2", "3", "-" },
        { ".", "0", "CHS", "+" }
    };

    Button enterButton = createButton("Enter");
    enterButton.setOnAction(e -> handleButtonPress("Enter"));
    grid.add(enterButton, 0, 1, 2, 1);

    Button clearButton = createButton("CLx");
    clearButton.setOnAction(e -> handleButtonPress("CLx"));
    grid.add(clearButton, 2, 1);

    Button backspaceButton = createButton("BS");
    backspaceButton.setOnAction(e -> handleButtonPress("BS"));
    grid.add(backspaceButton, 3, 1);

    // Add buttons to grid
    for (int row = 0; row < buttonLabels.length; row++) {
      for (int col = 0; col < buttonLabels[row].length; col++) {
        String label = buttonLabels[row][col];
        if (label.isEmpty())
          continue; // Skip empty placeholders

        Button button = createButton(label);
        if (button != null) {
          button.setOnAction(e -> handleButtonPress(label));
          grid.add(button, col, row + 2);
        }
      }
    }

    // Place grid on the right, stack display on the left
    root.setLeft(scrollingStackDisplay);
    root.setRight(grid);

    // Set up the scene
    Scene scene = new Scene(root, 350, 350);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Postfix Calculator with Stack Visualization");
    primaryStage.show();
  }

  private Button createButton(String label) {
    Button button = new Button();
    button.setMinSize(50, 50);

    switch (label) {
      case "+":
        button.setGraphic(createFontAwesomeIcon("fas-plus"));
        break;
      case "-":
        button.setGraphic(createFontAwesomeIcon("fas-minus"));
        break;
      case "*":
        button.setGraphic(createFontAwesomeIcon("fas-times"));
        break;
      case "/":
        button.setGraphic(createFontAwesomeIcon("fas-divide"));
        break;
      case "BS":
        button.setGraphic(createFontAwesomeIcon("fas-backspace"));
        break;
      case "CHS":
        button.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 14; -fx-font-weight: bold;");
        button.setText(label);
        break;
      case "CLx":
        button.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 14; -fx-font-weight: bold;");
        button.setText(label);
        break;
      case "Enter":
        button.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 14; -fx-font-weight: bold;");
        button.setText(label);
        button.setMinSize(105, 50);
        break;
      default:
        button.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        button.setText(label); // Use text for numbers and other buttons
        break;
    }

    return button;
  }

  private FontIcon createFontAwesomeIcon(String iconLiteral) {
    FontIcon icon = new FontIcon(iconLiteral); // Use FontAwesome icon literals
    icon.setIconSize(18);
    icon.setIconColor(Paint.valueOf("#4CAF50"));
    return icon;
  }

  private void handleButtonPress(String label) {

    // number. The display should clear and show that number.
    switch (label) {
      case "CLx": // Clear the display and stack
        display.clear();
        stack.clear(); // Clear the custom stack
        updateStackDisplay();
        break;
      case "BS": // Backspace
        String text = display.getText();
        if (!text.isEmpty()) {
          display.setText(text.substring(0, text.length() - 1));
        }
        break;
      case "Enter": // Process entry and push to stack
        processEntry();
        clearDisplayOnNextDigit = true;
        break;
      case "CHS": // Change sign
        changeSign();
        break;
      case "+":
      case "-":
      case "*":
      case "/": // Perform the operation
        if (!display.getText().isEmpty()) {
          if (clearDisplayOnNextDigit == false)
            processEntry();
        }
        performOperation(label);
        clearDisplayOnNextDigit = true;
        break;
      default: // Handle numbers and decimal points
        if (clearDisplayOnNextDigit) {
          display.clear(); // Clear the display if an operation was just executed
          clearDisplayOnNextDigit = false;
        }
        display.appendText(label);
        break;
    }
  }

  private void processEntry() {
    try {
      // Parse the value in the display and push it to the stack only if non-empty
      if (!display.getText().isEmpty()) {
        double value = Double.parseDouble(display.getText());
        stack.push(value); // Push to custom stack
        display.clear(); // Clear display after pushing to stack
        updateStackDisplay();
      }
    } catch (NumberFormatException e) {
      display.setText("Error");
    }
  }

  private void changeSign() {
    if (stack.size() < 1) {
      display.setText("Error");
      return;
    }

    double result = stack.pop();
    result *= -1;
    stack.push(result);

    updateStackDisplay();
    display.setText(String.valueOf(result));
  }

  private void performOperation(String operator) {
    if (stack.size() < 2) {
      display.setText("Error");
      return;
    }

    // Pop values from the custom stack
    double b = stack.pop();
    double a = stack.pop();
    double result = 0;

    switch (operator) {
      case "+":
        result = a + b;
        break;
      case "-":
        result = a - b;
        break;
      case "*":
        result = a * b;
        break;
      case "/":
        if (b != 0) {
          result = a / b;
        } else {
          display.setText("Error");
          stack.push(a); // Restore values if operation fails
          stack.push(b);
          updateStackDisplay();
          return;
        }
        break;
    }

    stack.push(result); // Push the result back to the custom stack
    updateStackDisplay();
    display.setText(String.valueOf(result));
  }

  private void updateStackDisplay() {
    stackDisplay.getChildren().clear();
    stackDisplay.getChildren().add(new Label("Stack:"));

    // Display each stack item in reverse order (LIFO)
    List<Double> stackContents = stack.getContents(); // Get all stack contents
    for (int i = 0; i < stackContents.size(); i++) {
      Label item = new Label(String.valueOf(stackContents.get(i)));
      item.setStyle("-fx-border-color: black; -fx-padding: 5; -fx-background-color: #ADD8E6; -fx-font-size: 14;");
      stackDisplay.getChildren().add(item);
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}