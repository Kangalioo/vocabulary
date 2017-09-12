import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
	public static final String JSON_FILE = "vocabulary.json";
	public static final boolean USE_GUI = true;
	
	private static Vocabulary vocabulary;
	
	private static LearningAlgorithm algorithm;
	
	
	public static void main(String[] args) {
		try {
			vocabulary = Parser.parse(JSON_FILE);
			start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void start() {
		Section section = pickSection();
		System.out.println();
		
		boolean isNew;
		while (true) {
			System.out.print("Are you revising your vocabulary (r) or are you learning your vocabulary new (n)? (r/n) ");
			String input = System.console().readLine();
			if (input.equalsIgnoreCase("r")) isNew = false;
			else if (input.equalsIgnoreCase("n")) isNew = true;
			else continue;
			
			break;
		}
		System.out.println();
		
		algorithm = new SmartAlgorithm(section.getWords(), isNew);
		System.out.println();
		if (USE_GUI) {
			System.out.println("Starting GUI.");
			javafx.application.Application.launch(MyApplication.class);
		} else {
			while (true) {
				if (askWord(algorithm)) break;
			}
		}
	}
	
	static LearningAlgorithm getAlgorithm() {
		return algorithm;
	}
	
	private static Section pickSection() {
		for (int i = 0; i < vocabulary.getSections().size(); i++) {
			System.out.println(" " + (i + 1) + ") " +
				vocabulary.getSections().get(i).getName());
		}
		System.out.print("Select one or more, comma-seperated: ");
		String input = System.console().readLine();
		String[] tokens = input.split(" *, *");
		Section[] sections = Arrays.stream(tokens)
			// Convert strings to integers
			.mapToInt(e -> Integer.parseInt(e) - 1)
			// Pick the sections
			.mapToObj(e -> vocabulary.getSections().get(e))
			.toArray(Section[]::new);
		
		return Section.combine(sections);
	}
	
	private static boolean askWord(LearningAlgorithm algorithm) {
		Word word = algorithm.pickWord();
		System.out.print(Colorer.setColor(11) + word.getPrimaryString() +
			" - " +	Colorer.setColor(-1));
		String answer = System.console().readLine();
		if (answer.equals("exit")) return true;
		boolean correct = word.isSecondaryCorrect(answer);
		if (correct) {
			System.out.println(Colorer.colored("Correct!", 10));
		} else {
			System.out.println(Colorer.colored("Wrong! ", 9) +
				Colorer.setAttribute(1) + word.getSecondaryString() +
				Colorer.setAttribute(0) + ".");
		}
		System.out.println(Colorer.colored("Press enter to continue.", 8));
		System.console().readLine();
		algorithm.processAnswer(word, correct);
		
		return false;
	}
}
