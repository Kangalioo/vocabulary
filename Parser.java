import com.eclipsesource.json.*;
import java.io.IOException;
import java.io.FileReader;

public class Parser {
	private Vocabulary vocabulary = new Vocabulary();
	private JsonObject root;
	private String primary, secondary;
	
	private Parser(String file) throws IOException {
		JsonValue rawRoot = Json.parse(new FileReader(file));
		if (rawRoot.isObject()) {
			root = rawRoot.asObject();
		} else {
			throw new RuntimeException("File does not contain an object.");
		}
	}
	
	
	public static Vocabulary parse(String file) throws IOException {
		return new Parser(file).parse();
	}
	
	private Vocabulary parse() {
		parseLangInformation();
		parseSections();
		
		return vocabulary;
	}
	
	private void parseLangInformation() {
		primary = root.getString("primary", "primary"); // get key "primary", return "primary" if not existing
		secondary = root.getString("secondary", "secondary"); // get key "secondary", return "secondary" if not existing
		String primaryDisplay = root.getString("primaryDisplay", primary);
		String secondaryDisplay = root.getString("secondaryDisplay", secondary);
		vocabulary.setLangInformation(primaryDisplay, secondaryDisplay);
	}
	
	private void parseSections() {
		JsonObject sections = root.get("sections").asObject();
		for (JsonObject.Member member : sections) {
			vocabulary.addSection(parseSection(new Section(member.getName()), member.getValue().asArray()));
		}
	}
	
	private Section parseSection(Section section, JsonArray words) {
		for (JsonValue value : words) {
			JsonObject jsonWord = value.asObject();
			Word word = new Word();
			
			processRawWord(word, jsonWord.get(primary), true);
			processRawWord(word, jsonWord.get(secondary), false);
			
			section.addWord(word);
		}
		return section;
	}
	
	private void processRawWord(Word word, JsonValue raw, boolean isPrimary) {
		if (raw.isObject()) {
			JsonObject object = raw.asObject();
			processRawWord(word, object.get("word"), isPrimary);
			String display = object.getString("display", null);
			if (isPrimary) {
				word.setPrimaryDisplay(display);
			} else {
				word.setSecondaryDisplay(display);
			}
		} else {
			String[] array;
			if (raw.isArray()) {
				JsonArray jsonArray = raw.asArray();
				array = new String[jsonArray.size()];
				int index = 0;
				for (JsonValue value : jsonArray) {
					array[index++] = value.asString();
				}
			} else if (raw.isString()) {
				array = new String[]{raw.asString()};
			} else {
				throw new RuntimeException("Incorrect data type in word.");
			}
			if (isPrimary) {
				word.setPrimary(array);
			} else {
				word.setSecondary(array);
			}
		}
	}
}
