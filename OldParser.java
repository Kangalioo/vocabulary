import com.eclipsesource.json.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
// temp
import java.io.FileWriter;

public class OldParser {
	private Vocabulary vocabulary = new Vocabulary();
	private JsonObject root;
	private String primary, secondary;
	
	private Map<String, Word> templates = new HashMap<>();
	
	public OldParser(String file) {
		// I really don't understand why Micro&soft is incapable of changing
		// their encoding to UTF-8 systemwide...
		// UTF-8 > everything else
		try {
			JsonValue rawRoot = Json.parse(
				new InputStreamReader(new FileInputStream(file), "UTF-8"));
			root = rawRoot.asObject();
		} catch (IOException e) {
			e.printStackTrace();
		}
		prepareSections("Unidad 2 — Texto: Todos me quieren tomar el pelo");
	}
	
	// These two are temporary methods
	// Call prepareSections to convert a raw section with name sectionName to a
	// computer readable section. Mistake checking is strongly advised.
	private void prepareSections(String sectionName) {
		JsonArray section = root.get("sections").asObject().
			get(sectionName).asArray();
		
		for (JsonValue value : section) {
			JsonObject obj = value.asObject();
			obj.set("de", makeObj(obj.get("de").asString()));
			obj.set("es", makeObj(obj.get("es").asString()));
		}
		
		try (FileWriter writer = new FileWriter("result.json")){
			section.writeTo(writer, PrettyPrint.indentWithTabs());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private JsonValue makeObj(String string) {
		// Apply templates
		String[] replacements = {
			"o, -a", "%{o}",
			"etw.", "%{etw}",
			"jdm.", "%{jdm}",
			"jdn.", "%{jdn}",
		};
		for (int i = 0; i < replacements.length; i += 2) {
			string = string.replace(replacements[i], replacements[i + 1]);
		}
		
		// Multiple choice
		String[] tokens = string.split("[,;] ");
		if (tokens.length == 2) {
			string = "/{" + tokens[0] + "|" + tokens[1] + "}";
		} else if (tokens.length > 2) {
			System.out.println("special case: " + string);
		}
		
		// Optional
		if (string.contains(" (")) {
			string = string.replace(" (", "!{ (");
			string = string.replace(")", ")}");
		} else if (string.contains(") ")) {
			string = string.replace("(", "!{(");
			string = string.replace(") ", ") }");
		}
		
		// Word choice
		if (string.contains(" / ")) {
			int index = string.indexOf(" / ");
			int lastSpace = -1;
			for (int i = 0; i < index; i++) {
				if (string.charAt(i) == ' ') {
					lastSpace = i;
				}
			}
			int nextSpace = string.indexOf(" ", index + 3);
			if (nextSpace == -1) nextSpace = string.length();
			
			String original = string.substring(lastSpace + 1, nextSpace);
			String word1 = string.substring(lastSpace + 1, index);
			String word2 = string.substring(index + 3, nextSpace);
			if (word1.equals("algo") && word2.equals("a")) {
				original += " alguien";
				word2 += " alguien";
			}
			String newStr = "/{" + word1 + "|" + word2 + "}";
			System.out.println("original: " + original);
			System.out.println("new: " + newStr);
			
			string = string.replace(original, newStr);
		}
		
		return Json.value(string);
	}
}
