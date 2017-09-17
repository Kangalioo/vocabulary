public class Word {
	private AnswerValidator primaryValidator;
	private AnswerValidator secondaryValidator;
	private String primary;
	private String secondary;
	
	public Word(AnswerValidator primaryValidator,
			AnswerValidator secondaryValidator,
			String primary, String secondary) {
		this.primaryValidator = primaryValidator;
		this.secondaryValidator = secondaryValidator;
		this.primary = primary;
		this.secondary = secondary;
	}
	
	public Word() {}
	
	
	public String toString() {
		return "\"" + getPrimary() + ": " + getSecondary() + "\"";
	}
	
	public void setPrimary(String primary) {
		this.primary = primary;
	}
	
	public void setSecondary(String secondary) {
		this.secondary = secondary;
	}
	
	public void setPrimaryValidator(AnswerValidator primaryValidator) {
		this.primaryValidator = primaryValidator;
	}
	
	public void setSecondaryValidator(AnswerValidator secondaryValidator) {
		this.secondaryValidator = secondaryValidator;
	}
	
	public AnswerValidator getPrimaryValidator() {
		return primaryValidator;
	}
	
	public AnswerValidator getSecondaryValidator() {
		return secondaryValidator;
	}
	
	public String getPrimary() {
		return primary;
	}
	
	public String getSecondary() {
		return secondary;
	}
	
	public boolean isPrimaryCorrect(String answer) {
		return primaryValidator.validate(answer);
	}
	
	public boolean isSecondaryCorrect(String answer) {
		return secondaryValidator.validate(answer);
	}
}
