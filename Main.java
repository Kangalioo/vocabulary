import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
	public static final String JSON_FILE = "vocabulary.json";
	
	private static Vocabulary vocabulary;
	
	
	public static void main(String[] args) {
		try {
			vocabulary = Parser.parse(JSON_FILE);
			start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static String colored(String string, int color) {
		return setColor(color) + string + setColor(-1);
	}
	
	private static String setColor(int color) {
		if (color == -1) color = 39;
		else if (color < 8) color = 30 + color;
		else color = 90 + color - 8;
		
		return setAttribute(color);
	}
	
	private static String setAttribute(int number) {
		return "\u001b[" + number + "m";
	}
	
	private static void start() {
		LearningAlgorithm algorithm = new SmartAlgorithm(pickSection().getWords());
		System.out.println();
		while (true) {
			if (askWord(algorithm)) break;
		}
	}
	
	private static Section pickSection() {
		for (int i = 0; i < vocabulary.getSections().size(); i++) {
			System.out.println(" " + (i + 1) + ") " + vocabulary.getSections().get(i).getName());
		}
		System.out.print("Select one or more, comma-seperated: ");
		String input = System.console().readLine();
		String[] tokens = input.split(" *, *");
		Section[] sections = Arrays.stream(tokens)
			.mapToInt(e -> Integer.parseInt(e) - 1) // Convert strings to integers
			.mapToObj(e -> vocabulary.getSections().get(e)) // Pick the sections
			.toArray(Section[]::new);
		
		return Section.combine(sections);
	}
	
	private static boolean askWord(LearningAlgorithm algorithm) {
		Word word = algorithm.pickWord();
		System.out.print(setColor(11) + word.getPrimaryString() + " - " + setColor(-1));
		String answer = System.console().readLine();
		if (answer.equals("exit")) return true;
		boolean correct = word.isSecondaryCorrect(answer);
		if (correct) {
			System.out.println(colored("Correct!", 10));
		} else {
			System.out.println(colored("Wrong! ", 9) + setAttribute(1) + word.getSecondaryString() + setAttribute(0) + ".");
		}
		System.out.println(colored("Press enter to continue.", 8));
		System.console().readLine();
		algorithm.processAnswer(word, correct);
		
		return false;
	}
}
