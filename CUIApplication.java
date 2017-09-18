import java.util.Arrays;

public class CUIApplication {
	private LearningAlgorithm algorithm;
	private Vocabulary vocabulary;
	
	
	public void start() {
		vocabulary = Main.getVocabulary();
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
		while (true) {
			if (askWord(algorithm)) break;
		}
	}
	
	private boolean askWord(LearningAlgorithm algorithm) {
		Word word = algorithm.pickWord();
		System.out.print(Colorer.setColor(11) + word.getPrimary() +
			" - " +	Colorer.setColor(-1));
		String answer = System.console().readLine();
		if (answer.equals("exit")) return true;
		boolean correct = word.isSecondaryCorrect(answer);
		if (correct) {
			System.out.println(Colorer.colored("Correct!", 10));
		} else {
			System.out.println(Colorer.colored("Wrong! ", 9) +
				Colorer.setAttribute(1) + word.getSecondary() +
				Colorer.setAttribute(0) + ".");
		}
		System.out.println(Colorer.colored("Press enter to continue.", 8));
		System.console().readLine();
		algorithm.processAnswer(word, correct);
		
		return false;
	}
	
	private Section pickSection() {
		for (int i = 0; i < vocabulary.getSections().size(); i++) {
			System.out.println(" " + String.format("%02d", (i + 1)) + ") " +
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
}
