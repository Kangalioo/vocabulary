import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.control.TextField;
import javafx.geometry.Pos;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MyApplication extends Application {
	public static final int WINDOW_WIDTH = 960, WINDOW_HEIGHT = 640;
	public static final Image CORRECT_IMAGE = new Image("correct.png"), INCORRECT_IMAGE = new Image("incorrect.png");
	public static final int IMAGE_SIZE = 297;
	
	
	private LearningAlgorithm algorithm;
	
	private StackPane root;
	
	private Text text;
	private TextField textField;
	private Button button;
	private ImageView view;
	private Text solution = null;
	
	private Word currentWord;
	private boolean submitted = false;
	
	
	@Override
	public void init() {
		algorithm = Main.getAlgorithm();
	}
	
	@Override
	public void start(Stage stage) {
		root = new StackPane();
		
		addElements();
		loadWord();
		
		stage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
		stage.show();
	}
	
	private void addElements() {
		root.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				buttonPress();
			}
		});
		
		textField = new TextField();
		textField.setFont(new Font(50));
		root.setAlignment(textField, Pos.BOTTOM_LEFT);
		root.getChildren().add(textField);
		
		text = new Text();
		text.setFont(new Font(60));
		root.setAlignment(text, Pos.TOP_CENTER);
		root.setMargin(text, new Insets(20, 0, 0, 0));
		root.getChildren().add(text);
		
		button = new Button("Submit");
		button.setMinHeight(textField.getHeight());
		button.setFont(new Font(40));
		button.setOnMouseClicked(e -> {
			buttonPress();
		});
		root.setAlignment(button, Pos.BOTTOM_RIGHT);
		root.getChildren().add(button);
		
		view = new ImageView();
		view.setPreserveRatio(true);
		view.setFitHeight(IMAGE_SIZE);
		root.setAlignment(view, Pos.CENTER);
		root.getChildren().add(view);
	}
	
	private void buttonPress() {
		if (submitted) {
			loadWord();
			button.setText("Submit");
			if (solution != null) {
				root.getChildren().removeAll(solution);
				solution = null;
			}
			view.setImage(null);
			textField.setText("");
			textField.setDisable(false);
			textField.requestFocus();
			submitted = false;
		} else {
			processAnswer();
			button.setText("Continue");
			textField.setDisable(true);
			submitted = true;
		}
	}
	
	private void processAnswer() {
		String answer = textField.getText();
		boolean correct = currentWord.isSecondaryCorrect(answer);
		algorithm.processAnswer(currentWord, correct);
		view.setImage(correct ? CORRECT_IMAGE : INCORRECT_IMAGE);
		if (!correct) {
			solution = new Text(currentWord.getSecondaryString());
			solution.setFont(new Font(50));
			root.setAlignment(solution, Pos.CENTER);
			root.setMargin(solution, new Insets(IMAGE_SIZE / 2 + 150, 0, 0, 0));
			root.getChildren().add(solution);
		}
	}
	
	private void loadWord() {
		currentWord = algorithm.pickWord();
		text.setText(currentWord.getPrimaryString());
	}
}
