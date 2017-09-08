import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.control.TextField;
import javafx.geometry.Pos;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.geometry.Insets;

public class MyApplication extends Application {
	public static final int WINDOW_WIDTH = 960, WINDOW_HEIGHT = 720;
	
	
	private StackPane root;
	
	private Text text;
	private TextField textField;
	
	
	@Override
	public void start(Stage stage) {
		root = new StackPane();
		
		addElements();
		loadWord();
		
		stage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
		stage.show();
	}
	
	private void addElements() {
		textField = new TextField();
		textField.setFont(new Font(50));
		root.getChildren().add(textField);
		root.setAlignment(textField, Pos.BOTTOM_CENTER);
		
		text = new Text();
		text.setFont(new Font(80));
		root.getChildren().add(text);
		root.setAlignment(text, Pos.TOP_CENTER);
		root.setMargin(text, new Insets(20, 0, 0, 0));
	}
	
	private void loadWord() {
		text.setText("Hello");
	}
}
