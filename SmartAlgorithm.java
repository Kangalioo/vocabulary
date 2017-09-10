import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * This is a learning algorithm which tries to optimize the asking order
 * of the words to boost learning speed. It does that in three ways:
 * <ol>
 * <li>Keep track when words were asked: if a specific word was not
 * tested for some time, its priority goes up.
 * </li>
 * <li>The last <code>MEMORY_SIZE</code> answers are saved. This data is
 * used to ensure that words which were not answered correctly get
 * higher priority that words the user masters.
 * </li>
 * <li>
 * </li>
 * </ol>
 */
public class SmartAlgorithm extends LearningAlgorithm {
	private class Instance {
		public Word word;
		private int lastCallingTime = 0;
		private LinkedList<Boolean> history = new LinkedList<>();
		
		
		public Instance(Word word) {
			this.word = word;
			for (int i = 0; i < MEMORY_SIZE; i++) history.add(true);
		}
		
		public void call() {
			this.lastCallingTime = SmartAlgorithm.this.wordsAsked;
		}
		
		public int timeSinceLastCall() {
			return SmartAlgorithm.this.wordsAsked - lastCallingTime;
		}
		
		public void processAnswer(boolean correct) {
			history.offer(correct);
			history.removeFirst();
		}
		
		public int correctAnswers() {
			return Collections.frequency(history, true);
		}
	}
	
	// n answers are remembered
	public static final int MEMORY_SIZE = 2;
	// An always wrong word comes n times more often than an always
	// right word.
	public static final double FITNESS_BASE = 5;
	// The priority gets multiplied by n on every question until it
	// gets asked itself, then it resets.
	public static final double REFRESH_BASE = 1.1;
	
	
	private int wordsAsked = 0;
	private List<Instance> instances;
	
	
	public SmartAlgorithm(List<Word> v) {
		super(v);
		instances = new ArrayList<Instance>(amount());
		getWords().forEach(word -> instances.add(new Instance(word)));
	}
	
	public int pick() {
		double[] priorities = new double[instances.size()];
		for (int i = 0; i < priorities.length; i++) {
			Instance instance = instances.get(i);
			double result = instance.correctAnswers();
			
			// ~~~ THE FORMULA ~~~
			result = Math.pow(FITNESS_BASE,
				(double) (MEMORY_SIZE - result) / MEMORY_SIZE);
			result *= Math.pow(REFRESH_BASE,
				instance.timeSinceLastCall());
			
			priorities[i] = result;
		}
		
		System.out.println(java.util.Arrays.toString(priorities));
		System.out.println();
		wordsAsked++;
		int choice = weightedRandom(priorities);
		instances.get(choice).call();
		return choice;
	}
	
	private int weightedRandom(double[] priorities) {
		double sum = 0;
		for (double d : priorities) sum += d;
		double random = Math.random() * sum;
		
		int index = -1;
		for (int i = 0; i < priorities.length; i++) {
			if (random < priorities[i]) {
				index = i;
				break;
			} else {
				random -= priorities[i];
			}
		}
		
		return index;
	}
	
	public void processAnswer(int index, boolean answer) {
		instances.get(index).processAnswer(answer);
	}
}
