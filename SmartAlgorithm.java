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
		private boolean isNew;
		private LinkedList<Boolean> history = new LinkedList<>();
		
		
		public Instance(Word word, boolean isNew) {
			this.word = word;
			this.isNew = isNew;
			for (int i = 0; i < MEMORY_SIZE; i++) {
				// // If the word is now, the answer should NOT be correct,
				// so the history gets prefilled with wrong answers
				history.add(!isNew);
			}
		}
		
		public void call() {
			this.lastCallingTime = SmartAlgorithm.this.wordsAsked;
		}
		
		public int timeSinceLastCall() {
			return SmartAlgorithm.this.wordsAsked - lastCallingTime;
		}
		
		public boolean lastAnswer() {
			return history.get(0);
		}
		
		public void processAnswer(boolean correct) {
			history.offerFirst(correct);
			history.removeLast();
		}
		
		public double priority() {
			double sum = 0;
			double currentFactor = 1;
			double sumOfFactors = 0;
			for (boolean b : history) {
				sumOfFactors += currentFactor;
				if (b) sum += currentFactor;
				currentFactor *= HISTORY_BASE;
			}
			double result = sum / sumOfFactors;
			
			result = Math.pow(FITNESS_BASE,
				(sumOfFactors - sum) / sumOfFactors);
			result *= Math.pow(
				(lastAnswer() == false) ? DYNAMIC_REFRESH_BASE : REFRESH_BASE,
				timeSinceLastCall());
			
			return result;
		}
	}
	
	// n answers are remembered per word
	public static final int MEMORY_SIZE = 3;
	// (Exponential) decrease in history priority
	public static final double HISTORY_BASE = 0.8;
	// An always wrong word comes n times more often than an always
	// right word.
	public static final double FITNESS_BASE = 15;
	// The priority gets multiplied by n on every question until it
	// gets asked itself, then it resets.
	public static final double REFRESH_BASE = 1.1;
	// REFRESH_BASE for words whose last answer was wrong ("new" words".
	// <not-yet>The "dynamic", because the more words there are the higher the value gets
	// to ensure that "new" words take (roughly) the same time to repeat.</not-yet>
	public static final double DYNAMIC_REFRESH_BASE = 4;
	// Number of words the user must be able to
	// remember until he answers them correctly.
	public static final int MAX_NEW_WORDS = 2;
	
	
	private int wordsAsked = 0;
	private int lastChoice = -1;
	private List<Instance> instances;
	private boolean areWordsNew;
	
	
	public SmartAlgorithm(List<Word> v, boolean areWordsNew) {
		super(v);
		this.areWordsNew = areWordsNew;
		instances = new ArrayList<Instance>(amount());
		getWords().forEach(word -> instances.add(new Instance(word, areWordsNew)));
	}
	
	public SmartAlgorithm(List<Word> v) {
		this(v, true);
	}
	
	public int pick() {
		//~ int newWords = instances.stream()
			//~ .filter(e -> e.lastAnswer() == false)
			//~ .count();
		
		int newWords = 0;
		double[] priorities = new double[instances.size()];
		for (int i = 0; i < priorities.length; i++) {
			priorities[i] = instances.get(i).priority();
			if (instances.get(i).lastAnswer() == false) {
				if (newWords >= MAX_NEW_WORDS) priorities[i] = 0;
				newWords++;
			}
			if (lastChoice == i) priorities[i] = 0;
		}
		
		// REMEMBER
		for (double d : priorities) {
			System.out.print(Math.round(d * 100) / 100.0 + ", ");
		}
		
		System.out.println();
		
		wordsAsked++;
		int choice = weightedRandom(priorities);
		instances.get(choice).call();
		
		lastChoice = choice;
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
