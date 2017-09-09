import java.util.List;
import java.util.ArrayList;

public class Vocabulary {
	String primary = "primary";
	String secondary = "secondary";
	
	List<Section> sections = new ArrayList<>();
	
	
	void setLangInformation(String primary, String secondary) {
		this.primary = primary;
		this.secondary = secondary;
	}
	
	String getPrimary() {
		return primary;
	}
	
	String getSecondary() {
		return secondary;
	}
	
	void addSection(Section section) {
		sections.add(section);
	}
	
	List<Section> getSections() {
		return sections;
	}
	
	List<Word> getAllWords() {
		List<Word> words = new ArrayList<>();
		sections.stream().forEach(s -> words.addAll(s.getWords()));
		return words;
	}
}
