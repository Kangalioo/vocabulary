import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public abstract class LearningAlgorithm {
	private List<Word> words = new ArrayList<>();
	
	
	public LearningAlgorithm(List<Word> words) {
		this.words = words;
	}
	
	protected List<Word> getWords() {
		return words;
	}
	
	protected int amount() {
		return words.size();
	}
	
	abstract public int pick();
	
	abstract public void processAnswer(int index, boolean answer);
	
	public int wordId(Word word) {
		return words.indexOf(word);
	}
	
	public Word pickWord() {
		return words.get(pick());
	}
	
	public void processAnswer(Word word, boolean answer) {
		int index = words.indexOf(word);
		if (index == -1) {
			throw new IllegalStateException(
				"The given word does not exist in the database.");
		}
		processAnswer(index, answer);
	}
}
