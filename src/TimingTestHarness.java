import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Harness for testing runtime implementations of runqueue
 *
 * @author Luke Harris
 */

public class TimingTestHarness {
    private static final int VT_MAX = 100;

    private static Scanner console;
    private static Runqueue runqueue;
    private static String implementationType;
    private static int[] nSet = {100, 500, 1000, 5000, 10000, 15000, 20000, 25000, 30000, 35000, 40000, 45000};


    public static void main(String[] args) throws IOException {
        console = new Scanner(System.in);

        System.out.print("Runqueue Implementation: ");
        implementationType = console.next();

        System.out.print("N size: ");
        int n = console.nextInt();

        System.out.print("Reps: ");
        int reps = console.nextInt();

        StringBuilder nLabel;
        if(n > 0)
            nLabel = new StringBuilder(String.valueOf(n));
        else{
            nLabel = new StringBuilder("{");
            for(int nInitial: nSet)
                nLabel.append(nInitial).append(",");

            nLabel.append("}");
        }

        String fileOut = implementationType + "-n" + nLabel + "-r" + reps + "-" + System.currentTimeMillis() + ".csv";
        PrintWriter out = new PrintWriter(new FileWriter(fileOut), true);

        if(n > 0) {
            System.out.println("Starting testRun n: " + n + " reps: " + reps);
            runqueue = newRunqueu();
            testRun(n, reps, out);
            System.out.println("Run finished\n\n");
        }else{
            for(int nInitial: nSet){
                System.out.println("Starting testRun n: " + nInitial + " reps: " + reps);
                runqueue = newRunqueu();
                testRun(nInitial, reps, out);
                System.out.println("Run finished\n\n");
            }
        }

        System.out.println("Testing finished, results writen to " + fileOut);
        out.close();
    }

    private static Runqueue newRunqueu(){
        switch(implementationType) {
            case "array":
                return new OrderedArrayRQ();
            case "linkedlist":
                return new OrderedLinkedListRQ();
            case "tree":
                return new BinarySearchTreeRQ();
            default:
                System.err.println("Unknown implmementation type.");
                return null;
        }
    }

    private static void testRun(int n, int reps, PrintWriter out){
        ProcPair[] procPool = new ProcPair[n + reps];

        long[][] results = new long[3][];
        for(int i = 0; i < results.length; i++)
            results[i] = new long[reps];

        //Creating pool of n+reps processes
        for (int i = 0; i < n + reps; i++) {
            int randVT = ThreadLocalRandom.current().nextInt(1, VT_MAX + 1);
            ProcPair pair = new ProcPair("p" + i, randVT);
            procPool[i] = pair;

            //Enqueuing n elements into runqueue
            if(i < n)
                runqueue.enqueue(pair.name, pair.vt);
        }


        for(int i = 0; i < reps; i++){
            long startTime, totalTime;

            String tempName = procPool[i].name;
            startTime = System.nanoTime();
            int time = runqueue.precedingProcessTime(tempName);
            totalTime = System.nanoTime() - startTime;
            if(time == -1)
                System.out.println("NOT FOUND: i:" + i);


            results[0][i] = totalTime;

            tempName = procPool[i + n].name;
            int tempVT = procPool[i + n].vt;
            startTime = System.nanoTime();
            runqueue.enqueue(tempName, tempVT);
            totalTime = System.nanoTime() - startTime;

            results[1][i] = totalTime;


            startTime = System.nanoTime();
            String dequeue = runqueue.dequeue();
            totalTime = System.nanoTime() - startTime;

            results[2][i] = totalTime;
        }

        //Writing individual runs to file
        for (long[] result : results) {
            for (int j = 0; j < reps; j++) {
                if (j != 0)
                    out.print(",");
                else
                    out.print("\n" + n + ",");
                out.print(result[j]);
            }
        }

    }

    static class ProcPair{
        String name;
        int vt;

        ProcPair(String name, int vt){
            this.name = name;
            this.vt = vt;
        }
    }
}
