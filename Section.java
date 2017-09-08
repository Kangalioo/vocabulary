import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Section {
	private String name;
	private List<Word> words;
	
	
	public Section(String name, Word... words) {
		this.name = name;
		this.words = new ArrayList<Word>(Arrays.<Word>asList(words));
	}
	
	public Section(String name, List<Word> words) {
		this.name = name;
		this.words = words;
	}
	
	public String getName() {
		return name;
	}
	
	void addWord(Word word) {
		words.add(word);
	}
	
	public List<Word> getWords() {
		return words;
	}
	
	public static Section combine(Section... sections) {
		List<Word> words = new ArrayList<>();
		for (Section s : sections) words.addAll(s.getWords());
		return new Section(null, words);
	}
}
