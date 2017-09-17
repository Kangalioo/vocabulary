public class CUIApplication {
	private LearningAlgorithm algorithm;
	
	
	public void start() {
		algorithm = Main.getAlgorithm();
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
}
