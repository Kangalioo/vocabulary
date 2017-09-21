import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;
import java.util.Locale;

public class Main {
	public static final String JSON_FILE = "vocabulary.json";
	public static final String TRANSLATION_FILE = "translation.txt";
	public static final String FORCE_LANGUAGE = "de";
	public static final String LANGUAGE = 
		FORCE_LANGUAGE == null ?
		Locale.getDefault().getLanguage() : FORCE_LANGUAGE;
	public static final boolean USE_GUI = true;
	
	private static Vocabulary vocabulary;
	
	private static LearningAlgorithm algorithm;
	
	
	public static void main(String[] args) {
		try {
			Translator.load(new File(TRANSLATION_FILE));
			if (Translator.hasLanguage(LANGUAGE)) {
				Translator.setLanguage(LANGUAGE);
			}
			vocabulary = Parser.parse(JSON_FILE);
			start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void start() {
		if (USE_GUI) {
			javafx.application.Application.launch(MyApplication.class);
		} else {
			new CUIApplication().start();
		}
	}
	
	static Vocabulary getVocabulary() {
		return vocabulary;
	}
}
