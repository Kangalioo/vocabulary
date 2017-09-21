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
import javafx.scene.layout.HBox;
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
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;
import javafx.scene.Group;
import javafx.scene.layout.FlowPane;
import javafx.geometry.Orientation;
import javafx.event.EventType;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.CheckBoxTreeItem;

public class MyApplication extends Application {
	public static final int WINDOW_WIDTH = 960, WINDOW_HEIGHT = 650;
	public static final Image CORRECT_IMAGE = new Image("correct.png"), INCORRECT_IMAGE = new Image("incorrect.png");
	public static final int IMAGE_SIZE = 297;
	//~ public static final char[] FOREIGN_CHARACTERS = 
		//~ "áàâäéèêëíîïóôöúùûüñç".toCharArray();
	// I have to use this because Windows cannot handle the above *sigh*
	public static final char[] FOREIGN_CHARACTERS =
		{'á', 'à', 'â', 'ä', 'é', 'è', 'ê', 'ë', 'í', 'î', 'ï', 'ó', 'ô', 'ö', 'ú', 'ù', 'û', 'ü', 'ñ', 'ç'};
	
	
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
	private Button[] foreignCharacterButtons;
	
	private Scene scene2;
	private VBox root2;
	private int selectedWords = 0;
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
		scene.getStylesheets().add("stylesheet.css");
		
		EventHandler<KeyEvent> handler = e -> {
			if (e.getCode() == KeyCode.SHIFT) {
				boolean pressed;
				if (e.getEventType() == KeyEvent.KEY_PRESSED) {
					pressed = true;
				} else if (e.getEventType() == KeyEvent.KEY_RELEASED) {
					pressed = false;
				} else {
					return;
				}
				int index = 0;
				for (Button b : foreignCharacterButtons) {
					char character = FOREIGN_CHARACTERS[index];
					if (pressed) character = Character.toUpperCase(character);
					b.setText(String.valueOf(character));
					index++;
				}
			}
		};
		scene.setOnKeyPressed(handler);
		scene.setOnKeyReleased(handler);
	}
	
	private void prepareScene2() {
		root2 = new VBox(8);
		root2.setAlignment(Pos.TOP_CENTER);
		
		addElements2();
		
		scene2 = new Scene(root2, WINDOW_WIDTH, WINDOW_HEIGHT);
		scene2.getStylesheets().add("stylesheet.css");
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
		Text words = new Text(Translator.get("gui5") + selectedWords);
		words.setFont(new Font(20));
		
		Button learnButton = new Button(Translator.get("gui1"));
		learnButton.setFont(new Font(20));
		Button reviseButton = new Button(Translator.get("gui2"));
		reviseButton.setFont(new Font(20));
		learnButton.setDisable(true);
		reviseButton.setDisable(true);
		learnButton.setOnMouseClicked(e -> {
			startTesting(true);
		});
		reviseButton.setOnMouseClicked(e -> {
			startTesting(false);
		});
		
		//~ ListView<String> listView = new ListView<>();
		//~ checkboxes = new boolean[vocabulary.getSections().size()];
		
		//~ int index = 1; // *intensive breathing*
		//~ for (Section section : vocabulary.getSections()) {
			//~ listView.getItems().add(index + ") " + section.getName());
			//~ index++;
		//~ }
		
		//~ listView.setCellFactory(CheckBoxListCell.forListView(
				//~ new Callback<String, ObservableValue<Boolean>>() {
			//~ @Override
			//~ public ObservableValue<Boolean> call(String item) {
				//~ BooleanProperty observable = new SimpleBooleanProperty();
				//~ observable.addListener((obs, wasSelected, isNowSelected) -> {
					//~ String indexString = item.substring(0, item.indexOf(")"));
					//~ int index = Integer.parseInt(indexString) - 1;
					//~ checkboxes[index] = isNowSelected;
					//~ int delta = 
						//~ vocabulary.getSections().get(index).getWords().size();
					//~ if (isNowSelected) {
						//~ selectedWords += delta;
					//~ } else {
						//~ selectedWords -= delta;
					//~ }
					//~ learnButton.setDisable(selectedWords == 0);
					//~ reviseButton.setDisable(selectedWords == 0);
					//~ words.setText(Translator.get("gui5") + selectedWords);
				//~ });
				//~ return observable;
			//~ }
		//~ }));
		
		CheckBoxTreeItem<String> rootItem = new CheckBoxTreeItem<>(Translator.get("gui6"));
		rootItem.setExpanded(true);
		checkboxes = new boolean[vocabulary.getSections().size()];
		int i = 0;
		for (Section section : vocabulary.getSections()) {
			CheckBoxTreeItem<String> item = 
				new CheckBoxTreeItem<String>((i + 1) + ") " + section.getName());
			final int indexCopy = i;
			item.selectedProperty().addListener((obs, former, selected) -> {
				int index = indexCopy;
				System.out.println(index + ": " + selected);
				checkboxes[index] = selected;
				int delta = 
					vocabulary.getSections().get(index).getWords().size();
				if (selected) {
					selectedWords += delta;
				} else {
					selectedWords -= delta;
				}
				learnButton.setDisable(selectedWords == 0);
				reviseButton.setDisable(selectedWords == 0);
				words.setText(Translator.get("gui5") + selectedWords);
			});
			rootItem.getChildren().add(item);
			i++;
		}
		TreeView<String> treeView = new TreeView<>(rootItem);
		treeView.setCellFactory(CheckBoxTreeCell.<String>forTreeView());
		
		//~ root2.getChildren().add(listView);
		root2.getChildren().add(treeView);
		root2.getChildren().add(learnButton);
		root2.getChildren().add(reviseButton);
		root2.getChildren().add(words);
	}
	
	private void addElements() {
		text = new Text();
		text.setFont(new Font(60));
		text.wrappingWidthProperty().bind(stage.widthProperty());
		text.setTextAlignment(TextAlignment.CENTER);
		//~ TextFlow textFlow = new TextFlow(text);
		//~ StackPane.setAlignment(textFlow, Pos.TOP_CENTER);
		//~ root.getChildren().add(textFlow);
		root.getChildren().add(text);
		StackPane.setAlignment(text, Pos.TOP_CENTER);
		StackPane.setMargin(text, new Insets(20, 0, 0, 0));
		
		textField = new TextField();
		textField.setFont(new Font(50));
		textField.requestFocus();
		
		button = new Button(Translator.get("gui3"));
		button.setMinHeight(textField.getHeight());
		button.setFont(new Font(50));
		
		// TODO: Make vbox not fill out entire horizontal space
		FlowPane vbox = new FlowPane(Orientation.VERTICAL);
		vbox.maxHeightProperty().bind(stage.heightProperty().subtract(textField.heightProperty()));
		root.getChildren().add(vbox);
		StackPane.setAlignment(vbox, Pos.TOP_LEFT);
		foreignCharacterButtons = new Button[FOREIGN_CHARACTERS.length];
		for (int i = 0; i < FOREIGN_CHARACTERS.length; i++) {
			char c = FOREIGN_CHARACTERS[i];
			Button button = new Button(String.valueOf(c));
			button.setOnMouseClicked(e -> {
				textField.setText(textField.getText() + button.getText());
				textField.requestFocus();
				textField.deselect();
				// FIXME: Caret should not jump to end if
				// its position was somewhere else previously.
				textField.end();
			});
			button.setOnMouseEntered(e -> {
				button.setOpacity(1);
			});
			button.setOpacity(0.3);
			button.setOnMouseExited(e -> {
				button.setOpacity(0.3);
			});
			button.setMinWidth(70);
			button.setFont(new Font(30));
			vbox.getChildren().add(button);
			foreignCharacterButtons[i] = button;
		}
		
		// TODO: Be able to switch commenting on the section below
		HBox hbox = new HBox(10, textField, button);
		//~ HBox.setHgrow(textField, Priority.ALWAYS);
		//~ HBox.setHgrow(button, Priority.NEVER);
		//~ hbox.setAlignment(Pos.BOTTOM_LEFT);
		//~ root.getChildren().add(new Group(hbox));
		root.getChildren().add(textField);
		StackPane.setAlignment(textField, Pos.BOTTOM_LEFT);
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
