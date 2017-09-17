import java.util.List;
import java.util.ArrayList;

public class AnswerValidator {
	// Converts my validation format to a regular expression.
	public static class Converter {
		private static final char[] reservedCharacters =
			"\\^$.|?*+()[{".toCharArray();
		
		
		private String code;
		
		private int i;
		private char[] c;
		private int bracketDepth = 0;
		private StringBuilder regex = new StringBuilder();
		private StringBuilder display = new StringBuilder();
		
		private Converter(String code) {
			this.code = code;
			
			i = 0;
			c = code.toCharArray();
			bracketDepth = 0;
			regex = new StringBuilder();
		}
		
		public static String[] convert(String code) {
			String[] result = new Converter(code).go();
			return result;
		}
		
		private String[] go() {
			for (i = 0; i < code.length(); i++) {
				processCharacter();
			}
			
			return new String[]{regex.toString(), display.toString()};
		}
		
		private void processCharacter() {
			if (i + 1 != code.length() && c[i + 1] == '{' &&
					(c[i] == '*' || c[i] == '/'
					|| c[i] == '?' || c[i] == '!'
					|| c[i] == '~')) {
				if (c[i] == '*') {
					processAsterisk();
				} else if (c[i] == '/') {
					processSlash();
				} else if (c[i] == '?') {
					processQuestionMark(false);
				} else if (c[i] == '!') {
					processQuestionMark(true);
				} else if (c[i] == '~') {
					processTilde();
				}
			} else {
				display.append(c[i]);
				for (char rc : reservedCharacters) {
					if (c[i] == rc) {
						regex.append('\\');
						break;
					}
				}
				regex.append(c[i]);
			}
		}
		
		private void processAsterisk() {
			i += 2;
			display.append(code.substring(i, (i = nextPipeOnLevel())));
			i++;
			int expressionEnd = expressionEnd();
			String string = code.substring(i, expressionEnd);
			String result = Converter.convert(string)[0];
			
			regex.append(result);
			i = expressionEnd;
		}
		
		private void processSlash() {
			i += 2;
			int expressionEnd = expressionEnd();
			List<String> optionsList = new ArrayList<>();
			int nextPipe;
			while ((nextPipe = nextPipeOnLevel()) != -1) {
				optionsList.add(code.substring(i, nextPipe));
				i = nextPipe + 1;
			}
			optionsList.add(code.substring(i, expressionEnd));
			String[] options = optionsList.toArray(new String[0]);
			
			regex.append('(');
			for (int j = 0; j < options.length; j++) {
				String[] rawConverted = Converter.convert(options[j]);
				String converted = rawConverted[0];
				display.append(rawConverted[1]);
				if (converted.length() > 1) regex.append('(');
				regex.append(converted);
				if (converted.length() > 1) regex.append(')');
				if (j + 1 != options.length) {
					display.append(" / ");
					regex.append('|');
				}
			}
			regex.append(')');
			
			i = expressionEnd;
		}
		
		private void processQuestionMark(boolean includeInDisplay) {
			i += 2;
			int expressionEnd = expressionEnd();
			String content = code.substring(i, expressionEnd);
			String contentRegex = Converter.convert(content)[0];
			
			if (includeInDisplay) display.append(content);
			if (contentRegex.length() > 1) regex.append('(');
			regex.append(contentRegex);
			if (contentRegex.length() > 1) regex.append(')');
			regex.append('?');
			
			i = expressionEnd;
		}
		
		private void processTilde() {
			i += 2;
			int expressionEnd = expressionEnd();
			int pipe = nextPipeOnLevel();
			String hint = code.substring(i, pipe == -1 ? expressionEnd : pipe);
			String[] hintConverted = Converter.convert(hint);
			hint = hintConverted[0];
			String hintDisplay = hintConverted[1];
			int nextHashtag = nextOnLevel('#');
			String placement0, placement1;
			if (pipe == -1) {
				placement0 = "";
				placement1 = "";
			} else {
				placement0 = code.substring(pipe + 1, nextHashtag);
				placement1 = code.substring(nextHashtag + 1, expressionEnd);
			}
			display.append(placement0 + "(" + hintDisplay + ")" + placement1);
			placement0 = Converter.convert(placement0)[0];
			placement1 = Converter.convert(placement1)[0];
			regex.append(placement0 + "\\(?" + hint + "\\)?" + placement1);
			
			i = expressionEnd;
		}
		
		private int expressionEnd() {
			int depth = 1;
			for (int j = i; j < code.length(); j++) {
				if (c[j] == '{') depth++;
				else if (c[j] == '}') depth--;
				if (depth == 0) {
					return j;
				}
			}
			System.err.println("ERROR: NO EXPRESSION END");
			return -1;
		}
		
		private int nextPipeOnLevel() {
			return nextOnLevel('|');
		}
		
		private int nextOnLevel(char character) {
			int depth = 0;
			for (int j = i; j < code.length(); j++) {
				if (c[j] == '{') depth++;
				else if (c[j] == '}') depth--;
				else if (depth == 0 && c[j] == character) {
					return j;
				} else if (depth < 0) {
					break;
				}
			}
			return -1;
		}
	}
	
	
	private String code;
	private String regex;
	private String display;
	
	
	public AnswerValidator(String code) {
		this.code = code;
		String[] result = Converter.convert(code);
		regex = result[0];
		display = result[1];
	}
	
	public boolean validate(String answer) {
		return (answer.matches(regex));
	}
	
	public String getDisplayString() {
		return display;
	}
}
