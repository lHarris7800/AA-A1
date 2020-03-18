import java.io.PrintWriter;
import java.lang.String;

/**
 * Implementation of the run queue interface using an Ordered Link List.
 *
 * Your task is to complete the implementation of this class.
 * You may add methods and attributes, but ensure your modified class compiles and runs.
 *
 * @author Sajal Halder, Minyi Li, Jeffrey Chan.
 */
public class OrderedLinkedListRQ implements Runqueue {

    private ProcNode head;
    /**
     * Constructs empty linked list
     */
    public OrderedLinkedListRQ() {
        head = null;

    }  // end of OrderedLinkedList()


    @Override
    public void enqueue(String procLabel, int vt) {
        //Search linkedList for proc with greater vt
        ProcNode curNode = head;
        ProcNode lastNode = null;

        while(curNode != null && curNode.proc.vt() <= vt){ //<= used to respect FIFO for procs with same vt
            lastNode = curNode;
            curNode = curNode.next;
        }

        //Insert new node into linked list
        ProcNode newNode = new ProcNode(head, new Proc(procLabel,vt));

        if(lastNode != null) //Updating last node if new proc isn't at top of queue
            lastNode.next = newNode;
    } // end of enqueue()


    @Override
    public String dequeue() {
        //Empty queue check
        if(head == null)
            return "";

        String topProcLabel = head.proc.procLabel();
        //pop head from queue, head removed as reference to it is lost
        head = head.next;

        return topProcLabel;
    } // end of dequeue()


    @Override
    public boolean findProcess(String procLabel) {
        //Search queue for procLabel
        ProcNode curNode = head;

        while(curNode != null ){
            if(curNode.proc.procLabel().equals(procLabel)) //return true if pracLabel found
                return true;

            curNode = curNode.next;
        }

        //Return false if loop gets to end of queue (procLabel not found)
        return false;
    } // end of findProcess()


    @Override
    public boolean removeProcess(String procLabel) {
        //Search queue for procLabel
        ProcNode curNode = head;
        ProcNode lastNode = null;

        while(curNode != null ){
            if(curNode.proc.procLabel().equals(procLabel)){
                if(lastNode != null)
                    lastNode.next = curNode.next; //curNode removed as reference to it is lost
                else
                    head = curNode.next; //curNode removed as reference to it is lost
                return true;
            }

            lastNode = curNode;
            curNode = curNode.next;
        }

        //Return false if loop gets to end of queue
        return false;
    } // End of removeProcess()


    @Override
    public int precedingProcessTime(String procLabel) {
        //Search queue for procLabel
        ProcNode curNode = head;
        int totalVT = 0;

        while(curNode != null ){
            if(curNode.proc.procLabel().equals(procLabel)) //return totalVT if pracLabel found
                return totalVT;

            totalVT += curNode.proc.vt();
            curNode = curNode.next;
        }

        //Return -1 if loop gets to end of queue (procLabel not found)
        return -1;
    } // end of precedingProcessTime()


    @Override
    public int succeedingProcessTime(String procLabel) {
        //Search queue for procLabel
        ProcNode curNode = head;
        int totalVT = 0;
        boolean procFound = false;

        while(curNode != null ){
            if(procFound)
                totalVT += curNode.proc.vt(); //Increment totalVT if procLabel has already been found

            if(curNode.proc.procLabel().equals(procLabel))
                procFound = true; //

            curNode = curNode.next;
        }

        if(procFound)
            return totalVT;
        else
            return -1;
    } // end of precedingProcessTime()


    @Override
    public void printAllProcesses(PrintWriter os) {
        //Iterate over all queue
        ProcNode curNode = head;

        while(curNode != null ){
            os.write(curNode.proc.procLabel() + " "); //Append proc to line

            curNode = curNode.next;
        }

        os.flush();
    } // end of printAllProcess()

} // end of class OrderedLinkedListRQ

class ProcNode{
    ProcNode next;
    Proc proc;

    ProcNode(ProcNode next, Proc proc){
        this.next = next;
        this.proc = proc;
    }
}
