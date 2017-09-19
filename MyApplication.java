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
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import java.util.List;
import java.util.ArrayList;
import javafx.scene.text.TextFlow;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;

public class MyApplication extends Application {
	public static final int WINDOW_WIDTH = 960, WINDOW_HEIGHT = 640;
	public static final Image CORRECT_IMAGE = new Image("correct.png"), INCORRECT_IMAGE = new Image("incorrect.png");
	public static final int IMAGE_SIZE = 297;
	public static final char[] FOREIGN_CHARACTERS = "áéíóúñ".toCharArray();
	
	
	private Vocabulary vocabulary;
	private LearningAlgorithm algorithm;
	private Stage stage;
	
	private Scene scene;
	private StackPane root;
	private Text text;
	private TextField textField;
	private Button button;
	private ImageView view;
	private Text solution = null;
	
	private Scene scene2;
	private Pane root2;
	private boolean[] checkboxes;
	
	private Word currentWord;
	private boolean submitted = false;
	
	
	@Override
	public void init() {
		vocabulary = Main.getVocabulary();
	}
	
	@Override
	public void start(Stage stage) {
		this.stage = stage;
		prepareScene2();
		
		stage.setScene(scene2);
		stage.show();
	}
	
	private void prepareScene1() {
		root = new StackPane();
		
		addElements();
		addListeners();
		loadWord();
		
		scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
	}
	
	private void prepareScene2() {
		root2 = new VBox();
		
		addElements2();
		
		scene2 = new Scene(root2, WINDOW_WIDTH, WINDOW_HEIGHT);
	}
	
	private void startTesting(boolean isNew) {
		List<Section> sections = new ArrayList<>();
		for (int i = 0; i < checkboxes.length; i++) {
			if (checkboxes[i]) {
				sections.add(vocabulary.getSections().get(i));
			}
		}
		List<Word> words = Section.combine(sections.toArray(new Section[0])).getWords();
		algorithm = new SmartAlgorithm(words, isNew);
		prepareScene1();
		stage.setScene(scene);
	}
	
	private void addListeners() {
		root.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				buttonPress();
			}
		});
		
		button.setOnMouseClicked(e -> {
			buttonPress();
		});
	}
	
	private void addElements2() {
		ListView<String> listView = new ListView<>();
		//~ listView.prefHeightProperty().bindBidirectional(
			//~ stage.widthProperty().divide(2));
		root2.getChildren().add(listView);
		checkboxes = new boolean[vocabulary.getSections().size()];
		
		int index = 1; // *intensive breathing*
		for (Section section : vocabulary.getSections()) {
			listView.getItems().add(index + ") " + section.getName());
			index++;
		}
		
		listView.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
			@Override
			public ObservableValue<Boolean> call(String item) {
				BooleanProperty observable = new SimpleBooleanProperty();
				observable.addListener((obs, wasSelected, isNowSelected) -> {
					String indexString = item.substring(0, item.indexOf(")"));
					int index = Integer.parseInt(indexString) - 1;
					checkboxes[index] = isNowSelected;
				});
				return observable;
			}
		}));
		
		Button learnButton = new Button(Translator.get("gui1"));
		Button reviseButton = new Button(Translator.get("gui2"));
		learnButton.setOnMouseClicked(e -> {
			startTesting(true);
		});
		reviseButton.setOnMouseClicked(e -> {
			startTesting(false);
		});
		root2.getChildren().add(learnButton);
		root2.getChildren().add(reviseButton);
	}
	
	private void addElements() {
		text = new Text();
		text.setFont(new Font(60));
		//~ TextFlow textFlow = new TextFlow(text);
		//~ StackPane.setAlignment(textFlow, Pos.TOP_CENTER);
		//~ root.getChildren().add(textFlow);
		root.getChildren().add(text);
		StackPane.setAlignment(text, Pos.TOP_CENTER);
		StackPane.setMargin(text, new Insets(20, 0, 0, 0));
		
		VBox vbox = new VBox();
		root.getChildren().add(vbox);
		StackPane.setAlignment(root, Pos.CENTER_LEFT);
		for (char c : FOREIGN_CHARACTERS) {
			Button button = new Button("" + c);
			button.setOnMouseClicked(e -> {
				textField.setText(textField.getText() + c);
				textField.requestFocus();
				textField.deselect();
				// FIXME
				textField.end();
			});
			button.setFont(new Font(30));
			vbox.getChildren().add(button);
		}
		
		textField = new TextField();
		textField.setFont(new Font(50));
		textField.requestFocus();
		root.getChildren().add(textField);
		StackPane.setAlignment(textField, Pos.BOTTOM_LEFT);
		
		button = new Button(Translator.get("gui3"));
		button.setMinHeight(textField.getHeight());
		button.setFont(new Font(40));
		root.getChildren().add(button);
		StackPane.setAlignment(button, Pos.BOTTOM_RIGHT);
		
		view = new ImageView();
		view.setPreserveRatio(true);
		view.setFitHeight(IMAGE_SIZE);
		root.getChildren().add(view);
		StackPane.setAlignment(view, Pos.CENTER);
	}
	
	private void buttonPress() {
		if (submitted) {
			loadWord();
			button.setText(Translator.get("gui3"));
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
			button.setText(Translator.get("gui4"));
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
			solution = new Text(currentWord.getSecondary());
			solution.setFont(new Font(50));
			StackPane.setAlignment(solution, Pos.CENTER);
			StackPane.setMargin(solution, new Insets(IMAGE_SIZE / 2 + 150, 0, 0, 0));
			root.getChildren().add(solution);
		}
	}
	
	private void loadWord() {
		currentWord = algorithm.pickWord();
		text.setText(currentWord.getPrimary());
	}
}
