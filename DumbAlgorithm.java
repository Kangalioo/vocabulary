import java.util.List;

public class DumbAlgorithm extends LearningAlgorithm {
	public DumbAlgorithm(List<Word> v) {
		super(v);
	}
	
	public int pick() {
		return (int) (Math.random() * amount());
	}
	
	public void processAnswer(int index, boolean answer) {
		// This is a dumb algorithm -> no intelligence needed.
	}
}
