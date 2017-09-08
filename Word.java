public class Word {
	private String[] primary;
	private String[] secondary;
	private String primaryDisplay = null;
	private String secondaryDisplay = null;
	
	public Word(String[] primary, String[] secondary) {
		this.primary = primary;
		this.secondary = secondary;
	}
	
	public Word() {}
	
	
	public String toString() {
		return "\"" + getPrimaryString() + ": " + getSecondaryString() + "\"";
	}
	
	public void setPrimary(String[] primary) {
		this.primary = primary;
	}
	
	public void setSecondary(String[] secondary) {
		this.secondary = secondary;
	}
	
	public String[] getPrimary() {
		return primary;
	}
	
	public String[] getSecondary() {
		return secondary;
	}
	
	public void setPrimaryDisplay(String primaryDisplay) {
		this.primaryDisplay = primaryDisplay;
	}
	
	public void setSecondaryDisplay(String secondaryDisplay) {
		this.secondaryDisplay = secondaryDisplay;
	}
	
	public String getPrimaryString() {
		if (primaryDisplay != null) return primaryDisplay;
		return String.join(", ", primary);
	}
	
	public String getSecondaryString() {
		if (secondaryDisplay != null) return secondaryDisplay;
		return String.join(", ", secondary);
	}
	
	private static boolean isCorrect(String[] solution, String answer) {
		for (String possibleSolution : solution) {
			if (answer.equalsIgnoreCase(possibleSolution)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isPrimaryCorrect(String answer) {
		return isCorrect(primary, answer);
	}
	
	public boolean isSecondaryCorrect(String answer) {
		return isCorrect(secondary, answer);
	}
}
