package Spurs;
import Spurs.CircularDoubleArrayQueue;
public class CircularStringQueueTest {

    public static double [][] queue_to_state(CircularDoubleArrayQueue q){
        double[][] s =new double[100][6];
        Object[] le = q.toArray();
        for (int i = 0; i < le.length; i++) {
            s[i] = q.get(i);
        }
        return s;
    }

    public static void main (String [] args) {
        int maxNumElementsToStore = 3;
        CircularDoubleArrayQueue words = new CircularDoubleArrayQueue(maxNumElementsToStore);
        double [] a = {1,2,3};
        double [] b = {3,3,5};
        double [] c = {43,1.3,3.4443};
        double[] d = {4,5,7};
        words.add(a);
        words.add(c);
        words.add(b);
        words.add(d);

        double[][] test;
        test = queue_to_state(words);
        System.out.println(test[0][1]);

        double[] p = words.get(0);

        System.out.printf("+++ Before removing words queue size is %d\n", p.length);

        for (int i=0; i<maxNumElementsToStore; i++) {
            System.out.printf("+++ element %d = %s%n", i, p[i]);
        }

        // Remove items from the Queue (DEQUEUE)
        while (!words.isEmpty()) {
            System.out.println(words.remove());
        }

        Object[] p2 = words.toArray();
        System.out.printf("+++ After removing words queue size is %d\n", p2.length);
    }
}
