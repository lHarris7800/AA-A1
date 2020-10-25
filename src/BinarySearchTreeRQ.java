import java.io.PrintWriter;
import java.io.Writer;
import java.lang.String;

/**
 * Implementation of the Runqueue interface using a Binary Search Tree.
 *
 * Your task is to complete the implementation of this class.
 * You may add methods and attributes, but ensure your modified class compiles and runs.
 *
 * @author Sajal Halder, Minyi Li, Jeffrey Chan
 */
class BinarySearchTreeRQ implements Runqueue {

    BinarySearchNode<Proc> root;


    BinarySearchTreeRQ() {
        root = null;
    }  // end of BinarySearchTreeRQ()


    @Override
    public void enqueue(String procLabel, int vt) {
        BinarySearchNode<Proc> newProc = new BinarySearchNode<>(null,
                null, new Proc(procLabel, vt));

        BinarySearchNode<Proc> currParent = root;

        if(currParent == null){
            root = newProc; //Special case for empty tree, add root and leftmost
            return;
        }

        boolean enqueued = false;

        do {
            if(vt < currParent.element.vt()) {
                if (currParent.leftChild == null) {
                    currParent.leftChild = newProc; //enqueue proc if a space is available

                    enqueued = true;
                } else
                    currParent = currParent.leftChild; //traversing left down tree

            } else {
                if (currParent.rightChild == null){
                    currParent.rightChild = newProc; //enqueue proc if a space is available

                    enqueued = true;
                } else
                    currParent = currParent.rightChild; //traversing right down tree
            }
        } while (!enqueued);
    } // end of enqueue()


    @Override
    public String dequeue() {
        BinarySearchNode<Proc> leftmost;
        BinarySearchNode<Proc> parentNode = root;

        if(root.leftChild == null) {
             leftmost = root;
             root = root.rightChild; //node dequeued as reference is lost to it
             return leftmost.element.procLabel();
        }

        while(parentNode.leftChild.leftChild != null)
            parentNode = parentNode.leftChild;

        leftmost = parentNode.leftChild;
        parentNode.leftChild = parentNode.leftChild.rightChild; //Last node dequeued as reference is lost to it

        return leftmost.element.procLabel();
    }

    @Override
    public boolean findProcess(String procLabel) {
        //PreOrder traversal used to find proc
        return POS(root, procLabel);
    } // end of findProcess()

    private boolean POS(BinarySearchNode<Proc> node, String searchLabel){
        if(node == null)
            return false;

        boolean found = node.element.procLabel().equals(searchLabel);

        if(!found)
            found = POS(node.leftChild, searchLabel);

        if(!found)
            found = POS(node.rightChild, searchLabel);

        return found;
    }


    @Override
    public boolean removeProcess(String procLabel) {
        //PreOrder traversal used to find (and return) proc
        BinarySearchNode parentNode = PORemove(root, procLabel);

        if(parentNode != null){
            BinarySearchNode targetNode;
            boolean isleftChild;

            if(parentNode.leftChild != null){
                Proc leftProc = (Proc)parentNode.leftChild.element;
                if(leftProc.procLabel().equals(procLabel)) {
                    targetNode = parentNode.leftChild;
                    isleftChild = true;
                } else{
                    targetNode = parentNode.rightChild;
                    isleftChild = false;
                }
            } else {
                targetNode = parentNode.rightChild;
                isleftChild = false;
            }

            if(targetNode.leftChild == null){//Scenario 1 or 2, targetnode has no left children
                if(isleftChild)
                    parentNode.leftChild = targetNode.rightChild; //node deleted as reference is lost to it
                else
                    parentNode.rightChild = targetNode.rightChild; //node deleted as reference is lost to it

            } else if(targetNode.rightChild == null){//Scenario 1 or 2, targetnode has no right children
                if(isleftChild)
                    parentNode.leftChild = targetNode.leftChild; //node deleted as reference is lost to it
                else
                    parentNode.rightChild = targetNode.leftChild; //node deleted as reference is lost to it

            } else { //Scenario 3, targetNode has 2 children
                BinarySearchNode bottomParent = targetNode.rightChild;
                if(bottomParent.leftChild == null){
                    if(isleftChild)
                        parentNode.leftChild = bottomParent; //node deleted as reference is lost to it
                    else
                        parentNode.rightChild = bottomParent; //node deleted as reference is lost to it
                } else {
                    while(bottomParent.leftChild.leftChild != null)
                        bottomParent = bottomParent.leftChild;

                    if(isleftChild)
                        parentNode.leftChild = bottomParent.leftChild; //node deleted as reference is lost to it
                    else
                        parentNode.rightChild = bottomParent.leftChild; //node deleted as reference is lost to it

                    bottomParent.leftChild = bottomParent.leftChild.rightChild; //Repair tree from leftchild being inserted at targetNode
                }
            }
            return true;
        }
        return false;
    } // end of removeProcess()

    private BinarySearchNode PORemove(BinarySearchNode<Proc> node, String searchLabel){
        if(node == null)
            return null;

        BinarySearchNode targetParent = null;

        if(node.leftChild != null){
            Proc leftProc = (Proc)node.leftChild.element;
            if (leftProc.procLabel().equals(searchLabel))
                targetParent = node;
        } else if(node.rightChild != null){
            Proc rightProc = (Proc)node.rightChild.element;
            if(rightProc.procLabel().equals(searchLabel))
                targetParent = node;
        }



        if(targetParent == null)
            targetParent = PORemove(node.leftChild, searchLabel);

        if(targetParent == null)
            targetParent = PORemove(node.rightChild, searchLabel);

        return targetParent;
    }

    @Override
    public int precedingProcessTime(String procLabel) {
        //In order traversal used to add up vt until procLabel is found
        int totalVT = IOPrecedTime(root, procLabel);

        if(totalVT < 0)
            return -1*(totalVT + 1); //vt offset undone (see DFSProcessTime for where an extra -1 is added to vt)
        else
            return -1; //procLabel not found
    } // end of precedingProcessTime()

    private int IOPrecedTime(BinarySearchNode<Proc> node, String searchLabel){
        if(node == null)
            return 0;

        boolean found = node.element.procLabel().equals(searchLabel);
        int leftVt, rightVt;
        int totalVt = 0;

        if(found)
            totalVt += 1; //Found node returns -1 (as -0 is not possible), undone by precedingProcessTime call


        leftVt = IOPrecedTime(node.leftChild, searchLabel);
        if(leftVt < 0) //-ve vt denotes search label has been found in subtree
            found = true;

        totalVt += Math.abs(leftVt); //Add total vt of left subtree to totalVT


        if(!found)
            totalVt += node.element.vt(); //Add current vt to totalVT
        //not added if search label has been found in left subtree
        //(as current vt isn't less than any node in left subtree)

        //Right subtree is not searched if search label has been found
        //ie only elements preceding search label are searched
        if(!found){
            rightVt = IOPrecedTime(node.rightChild, searchLabel);
            if(rightVt < 0)
                found = true;

            totalVt += Math.abs(rightVt); //Add total vt of right subtree to totalVT
            //not added if search label has been found in left subtree or current node
            //(as right subtree is greater than any node in left subtree or the current node)
        }

        if(found)
            return -1*totalVt; //Negative vt returned to indicate searchLabel found in subtree
        else
            return totalVt;
    }


    @Override
    public int succeedingProcessTime(String procLabel) {
        //In order (in reverse) traversal used to add vt until procLabel is found
        int totalVT = IOSucceedTime(root, procLabel);

        if(totalVT < 0)
            return -1*(totalVT + 1); //vt offset undone (see DFSSucceedTime for where an extra -1 is added to vt)
        else
            return -1; //procLabel not found
    } // end of precedingProcessTime()

    private int IOSucceedTime(BinarySearchNode<Proc> node, String searchLabel){
        if(node == null)
            return 0;

        boolean found = node.element.procLabel().equals(searchLabel);
        int leftVt, rightVt;
        int totalVt = 0;

        if(found)
            totalVt += 1; //Found node returns -1 (as -0 is not possible), undone by succeedingProcessTime call


        rightVt = IOSucceedTime(node.rightChild, searchLabel);
        if(rightVt < 0) //-ve vt denotes search label has been found in subtree
            found = true;

        totalVt += Math.abs(rightVt); //Add total vt of left subtree to totalVT


        if(!found)
            totalVt += node.element.vt(); //Add current vt to totalVT
        //not added if search label has been found in right subtree
        //(as current vt is less than any node in right subtree)

        //Right subtree is not searched if search label has been found
        //ie only elements preceding search label are searched
        if(!found){
            leftVt = IOSucceedTime(node.leftChild, searchLabel);
            if(leftVt < 0)
                found = true;

            totalVt += Math.abs(leftVt); //Add total vt of left subtree to totalVT
            //not added if search label has been found in right subtree or current node
            //(as right subtree is greater than any node in left subtree or the current node)
        }

        if(found)
            return -1*totalVt; //Negative vt returned to indicate searchLabel found in subtree
        else
            return totalVt;
    }

    @Override
    public void printAllProcesses(PrintWriter os) {
        DFSPrint(root, os);
        os.write("\n");
        os.flush();
    } // end of printAllProcess()

    private void DFSPrint(BinarySearchNode<Proc> node, PrintWriter os){
        if(node == null)
            return;

        DFSPrint(node.leftChild, os); //Recursive call to all nodes to left (less vt)

        os.write(node.element.procLabel() +" "); //Add current node

        DFSPrint(node.rightChild, os); //Recursive call to all nodes to right (greater vt)
    }

} // end of class BinarySearchTreeRQ
