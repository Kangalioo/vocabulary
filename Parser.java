import com.eclipsesource.json.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

public class Parser {
	private Vocabulary vocabulary = new Vocabulary();
	private JsonObject root;
	private String primary, secondary;
	
	private Map<String, String[]> templates = new HashMap<>();
	
	private Parser(String file) throws IOException {
		// I really don't understand why Micro&soft is incapable of changing
		// their encoding to UTF-8 systemwide...
		// UTF-8 > everything else
		JsonValue rawRoot = Json.parse(
			new InputStreamReader(new FileInputStream(file), "UTF-8"));
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
		parseTemplates();
		parseSections();
		
		return vocabulary;
	}
	
	private void parseLangInformation() {
		// get key "primary", return "primary" if not existing
		// get key "secondary", return "secondary" if not existing
		primary = root.getString("primary", "primary");
		secondary = root.getString("secondary", "secondary");
		String primaryDisplay = root.getString("primaryDisplay", primary);
		String secondaryDisplay = root.getString("secondaryDisplay", secondary);
		vocabulary.setLangInformation(primaryDisplay, secondaryDisplay);
	}
	
	private void parseTemplates() {
		JsonObject object = root.get("templates").asObject();
		for (JsonObject.Member member : object) {
			JsonValue value = member.getValue();
			String[] valueArray = null;
			if (value.isString()) {
				valueArray = new String[]{value.asString()};
			} else if (value.isArray()) {
				List<JsonValue> stringValueList = value.asArray().values();
				List<String> stringList = new ArrayList<>();
				for (JsonValue v : stringValueList) {
					stringList.add(v.asString());
				}
				valueArray = stringList.toArray(new String[stringList.size()]);
			}
			templates.put(member.getName(), valueArray);
		}
	}
	
	private void parseSections() {
		JsonObject sections = root.get("sections").asObject();
		for (JsonObject.Member member : sections) {
			vocabulary.addSection(parseSection(
				new Section(member.getName()), member.getValue().asArray()));
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
			array = addExtensions(array);
			if (isPrimary) {
				word.setPrimary(array);
			} else {
				word.setSecondary(array);
			}
		}
	}
	
	private String[] addExtensions(String[] array) {
		List<String> result = new ArrayList<>();
		for (String string : array) {
			int a = string.indexOf("{");
			int b = string.indexOf("}");
			if (a == -1 || b == -1 || b < a) {
				result.add(string);
				continue;
			}
			String template = string.substring(a + 1, b);
			String[] replacements = templates.get(template);
			if (replacements == null) {
				System.err.println("Template \"" + template +
					"\" does not exist.");
				continue;
			}
			String[] results = new String[replacements.length];
			String preTemplate = string.substring(0, a);
			String postTemplate = string.substring(b + 1);
			for (int i = 0; i < results.length; i++) {
				results[i] = preTemplate + replacements[i] + postTemplate;
			}
			results = addExtensions(results);
			for (String s : results) result.add(s);
		}
		return result.toArray(new String[result.size()]);
	}
	
	//~ private void test() {
		//~ System.out.println(Arrays.toString(addExtensions(
			//~ new String[]{"hallo", "etw", "{etw}", "{o}_{e}", "distint{o}"})));
	//~ }
}
