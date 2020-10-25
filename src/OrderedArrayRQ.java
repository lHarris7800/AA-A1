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
	private int endArray;
	private final int startLength = 10;
	private final float resizeFactor = 0.2f;

	/**
	 * Constructs empty queue
	 */
	OrderedArrayRQ() {
		processes = new Proc[startLength];
		endArray = -1;

	} // end of OrderedArrayRQ()

	@Override
	public void enqueue(String procLabel, int vt){
		boolean enqueued = false;

		if (endArray == processes.length - 1){
			//Resize array
			Proc[] newProcesses = new Proc[(int)(processes.length * (1+resizeFactor/2))];

			for(int i = 0; i <= endArray; i++){
				if(processes[i].vt() <= vt){
					newProcesses[i] = new Proc(procLabel,vt);//Insert new Proc

					for(int j = i; j <= endArray; j++)
						newProcesses[j+1] = processes[j]; //Copy procs shallower then newProc

					i = endArray+1;
				}else
					newProcesses[i] = processes[i]; //Copy procs deeper than newProc
			}

			if(processes[endArray].vt() > vt)
				newProcesses[endArray + 1] = new Proc(procLabel, vt);
			processes = newProcesses;
		} else {

			for (int i = endArray + 1; i >= 0 && !enqueued; i--) {
				if (i > 0) {
					if (processes[i - 1].vt() > vt) {
						processes[i] = new Proc(procLabel, vt); // new proc inserted
						enqueued = true;
					}
				} else {
					processes[i] = new Proc(procLabel, vt); // new proc inserted
					enqueued = true;
				}

				if (!enqueued)
					processes[i] = processes[i - 1];
			}

			if (endArray == -1)
				processes[0] = new Proc(procLabel, vt);
		}

		endArray++;
	}

	@Override
	public String dequeue() {
		String returnLabel = processes[endArray].procLabel();
		processes[endArray] = null; //dequeue lowest element from end of list

		if((float)endArray/processes.length < 1-resizeFactor){
			Proc[] newProcesses = new Proc[(int)(processes.length * (1-resizeFactor/2))];
			//shrink array

			for(int i = 0; i < endArray; i++)
				newProcesses[i] = processes[i];

			processes = newProcesses;
		}

		endArray--;

		return returnLabel;
	} // end of dequeue()

	@Override
	public boolean findProcess(String procLabel) {
		for (int i = 0; i <= endArray; i++) {
			if (processes[i].procLabel().equals(procLabel)) {
				return true;
			}
		}
		// return false if procLabel not found
		return false;
	} // end of findProcess()

	@Override
	public boolean removeProcess(String procLabel) {
		for (int i = 0; i <= endArray; i++) {
			// find process
			if (processes[i].procLabel().equals(procLabel)) {
				// overwrite current process with next process in queue, repeat til end
				for (int j = i; j <= endArray; j++) {
					processes[j] = processes[j + 1];
				}
				// reduce endArray
				endArray--;
				i = endArray+1;
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
		boolean found = false;
		// start counting
		for (int i = endArray; i >= 0 && !found; i--) {
			// if we find process match
			// counting
			if (processes[i].procLabel().equals(procLabel))
				found = true;
			else
				vtpCount += processes[i].vt(); // add vt count of preceding element
		}

		if(found)
			return vtpCount;
		else
			return -1;
	}// end of precedingProcessTime()

	@Override
	public int succeedingProcessTime(String procLabel) {
		// this assume from given element to end of array
		int vtsCount = 0;
		boolean found = false;

		for (int i = 0; i <= endArray && !found; i++) {
			if (processes[i].procLabel().equals(procLabel))
				found = true;
			else
				vtsCount += processes[i].vt(); //add vt count of succeeding element
		}

		if(found)
			return vtsCount;
		else
			return -1;
	} // end of precedingProcessTime()

	@Override
	public void printAllProcesses(PrintWriter os) {
		for (int i = endArray; i >= 0; i--)
			os.write(processes[i].procLabel() + " ");

		os.write("\n");
		os.flush();
	} // end of printAllProcesses()
// end of class OrderedArrayRQ	
}
