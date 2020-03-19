import java.io.PrintWriter;
import java.lang.String;

/**
 * Implementation of the Runqueue interface using an Ordered Array.
 *
 * Your task is to complete the implementation of this class. You may add
 * methods and attributes, but ensure your modified class compiles and runs.
 *
 * @author Sajal Halder, Minyi Li, Jeffrey Chan
 */
public class OrderedArrayRQ implements Runqueue {
	private Proc[] processes;
	private int startArray = 0;
	private int endArray = 0;
	// TODO currently array set to max length of 10, need to confirm what correct
	// length should be?
	private int maxLength = 10;

	/**
	 * Constructs empty queue
	 */
	public OrderedArrayRQ() {
		processes = new Proc[maxLength];

	} // end of OrderedArrayRQ()

	@Override
	public void enqueue(String procLabel, int vt) {
		boolean running = true;
		// checking array not at max length;
		// TODO rearrange to enter them in order of priority by vt
		if (endArray == startArray) {
			processes[endArray] = new Proc(procLabel, vt);
			endArray++;
		}
		// checking if array has elements && if array is not at max length and has an
		// additional free spot
		while (running) {
			if (endArray != startArray && endArray <= maxLength - 2) {
				for (int i = startArray; i <= endArray; i++) {
					// if vt is less than vt of current element
					if (vt < processes[i].vt()) {
						// set j to end of array, and decrement until it reaches i
						for (int j = endArray; j >= i; j--) {
							// move the value at element j to the next element over (j+1)
							processes[j + 1] = processes[j];
						}
					}
					// set new element in to its position
					processes[i] = new Proc(procLabel, vt);
					// increment endArray to account for new process
					endArray++;
					running = false;
				}
			}
		}

	} // end of enqueue()

	@Override
	public String dequeue() {
		// checking array is not empty
		if (startArray != endArray) {
			// removes front of the array
			for (int i = startArray; i < endArray - 1; i++) {
				processes[i] = processes[i + 1];
			}
			// decrement end array to account for lost slot
			endArray--;
		}
		// TODO
		return "procLabel.....";
	} // end of dequeue()

	@Override
	public boolean findProcess(String procLabel) {
		// Implement me
		for (int i = startArray; i < endArray; i++) {
			if (processes[i].procLabel().equals(procLabel)) {
				return true;
			}
		}
		// return false if procLabel not found
		return false;
	} // end of findProcess()

	@Override
	public boolean removeProcess(String procLabel) {
		for (int i = startArray; i < endArray; i++) {
			// find process
			if (processes[i].procLabel().equals(procLabel)) {
				// overwrite current process with next process in queue, repeat til end
				for (int j = i; j < endArray - 1; j++) {
					processes[i] = processes[i + 1];
				}
				// reduce endArray
				endArray--;
				return true;
			}
		}
		// process not found
		return false;
	} // end of removeProcess()

	@Override
	public int precedingProcessTime(String procLabel) {
		// this assumes preceding is from front of array up to given element
		int vtpCount = 0;
		boolean counting = true;
		// start counting
		while (counting) {
			for (int i = startArray; i < endArray; i++) {
				// add vt count of every element
				vtpCount += processes[i].vt();
				// if we find process match - stop adding, remove current process vt, and stop
				// counting
				if (processes[i].procLabel().equals(procLabel)) {
					vtpCount -= processes[i].vt();
					counting = false;
				}
			}
		}
		return vtpCount;

	}// end of precedingProcessTime()

	@Override
	public int succeedingProcessTime(String procLabel) {
		// this assume from given element to end of array
		int vtsCount = 0;
		for (int i = startArray; i < endArray; i++) {
			// if we find process match - add vt of all elements after it
			if (processes[i].procLabel().equals(procLabel)) {
				for (int j = i; j < endArray; j++) {
					vtsCount += processes[i].vt();
				}
			}
		}

		return vtsCount;
	} // end of precedingProcessTime()

	@Override
	public void printAllProcesses(PrintWriter os) {
		for (int i = startArray; i < endArray; i++) {
			os.write(processes[i].procLabel());
		}

		// os.write(curNode.proc.procLabel());

	} // end of printAllProcesses()
// end of class OrderedArrayRQ	
}
