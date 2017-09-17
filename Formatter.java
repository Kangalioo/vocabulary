import com.eclipsesource.json.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.io.FileWriter;

public class Formatter {
	private JsonObject root;
	
	public Formatter(String file, String section) {
		try {
			JsonValue rawRoot = Json.parse(
				new InputStreamReader(new FileInputStream(file), "UTF-8"));
			root = rawRoot.asObject();
			prepareSections(section);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
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
			string = string.replace(" (", "~{");
			string = string.replace(")", "| #}");
		} else if (string.contains(") ")) {
			string = string.replace("(", "~{");
			string = string.replace(") ", "|# }");
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
