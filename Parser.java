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
	private Map<String, String> templates = new HashMap<>();
	
	private String primary, secondary;
	
	
	private Parser(String file) throws IOException {
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
			try {
				templates.put(member.getName(), member.getValue().asString());
			} catch (UnsupportedOperationException e) {
				System.err.println("Invalid template format in " + member.getName());
			}
		}
	}
	
	private void parseSections() {
		JsonObject sections = root.get("sections").asObject();
		for (JsonObject.Member member : sections) {
			Section section = new Section(member.getName());
			for (JsonValue value : member.getValue().asArray()) {
				JsonObject jsonWord = value.asObject();
				JsonValue rawPri = jsonWord.get(primary);
				JsonValue rawSec = jsonWord.get(secondary);
				String pri = insertTemplates(
					rawPri.isString() ? rawPri.asString() : "INVALID");
				String sec = insertTemplates(
					rawSec.isString() ? rawSec.asString() : "INVALID");
				AnswerValidator priValidator = new AnswerValidator(pri);
				AnswerValidator secValidator = new AnswerValidator(sec);
				section.addWord(new Word(
					priValidator, secValidator,
					priValidator.getDisplayString(),
					secValidator.getDisplayString()));
			}
			vocabulary.addSection(section);
		}
	}
	
	private String insertTemplates(String raw) {
		while (true) {
			int index1 = raw.indexOf("%{");
			if (index1 == -1) break;
			index1 += 2;
			int index2 = raw.indexOf("}", index1);
			String templateName = raw.substring(index1, index2);
			String templateResult = templates.get(templateName);
			if (templateResult == null) {
				System.err.println("Template " + templateName + " doesn't exist.");
				continue;
			}
			raw = raw.replace("%{" + templateName + "}", templateResult);
		}
		return raw;
	}
}
