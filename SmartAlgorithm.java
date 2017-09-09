import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

public class SmartAlgorithm extends LearningAlgorithm {
	// How many answers for each word are remembered
	public static final int MEMORY_SIZE = 6;
	
	
	// I would have written List<List<...>>, but the compiler didn't allow it.
	private List<LinkedList<Boolean>> levels = new ArrayList<LinkedList<Boolean>>();
	
	
	public SmartAlgorithm(List<Word> v) {
		super(v);
		for (int i = 0; i < amount(); i++) {
			LinkedList<Boolean> list = new LinkedList<>();
			levels.add(list);
			for (int j = 0; j < MEMORY_SIZE; j++) {
				list.add(false);
			}
		}
	}
	
	public int pick() {
		double[] priorities = new double[levels.size()];
		double sum = 0;
		for (int i = 0; i < priorities.length; i++) {
			double result = Collections.frequency(levels.get(i), true);
			
			result = MEMORY_SIZE - result + 1; // ~~~ THE FORMULA ~~~
			
			priorities[i] = result;
			sum += result;
		}
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
		LinkedList<Boolean> list = levels.get(index);
		list.offer(answer);
		list.removeFirst();
	}
}

