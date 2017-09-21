import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.List;
import java.util.ArrayList;

public class Translator {
	private static class Resource {
		private List<String> translations = new ArrayList<>();
		
		
		public void addTranslation(String translation) {
			translations.add(translation);
		}
		
		public String getTranslation(int index) {
			return translations.get(index);
		}
	}
	
	
	private static List<String> languages = new ArrayList<>();
	private static int currentLanguage = 0;
	private static Map<String, Resource> resources = new HashMap<>();
	
	
	public static void load(File file) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(file), "UTF-8"))) {
			String line;
			boolean readingLanguages = true;
			int index = -1;
			Resource currentResource = null;
			while ((line = reader.readLine()) != null) {
				if (readingLanguages) {
					if (line.equals("")) {
						readingLanguages = false;
					} else {
						languages.add(line);
					}
				} else {
					if (line.equals("") || line.charAt(0) == '#') continue;
					if (index == -1) {
						currentResource = new Resource();
						resources.put(line, currentResource);
					} else {
						currentResource.addTranslation(line);
					}
					
					index++;
					if (index == languages.size()) index = -1;
				}
			}
		}
	}
	
	public static void setLanguage(String language) {
		if (!languages.contains(language)) {
			throw new IllegalArgumentException("This language does not exist.");
		}
		currentLanguage = languages.indexOf(language);
	}
	
	public static boolean hasLanguage(String language) {
		return languages.contains(language);
	}
	
	public static String get(String string) {
		if (resources.get(string) == null) {
			throw new IllegalArgumentException(
				"This resource does not exist: " + string);
		}
		return resources.get(string).getTranslation(currentLanguage);
	}
}
