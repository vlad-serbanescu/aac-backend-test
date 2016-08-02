import java.util.concurrent.Callable;

import abs.api.cwi.ABSFutureTask;

public class DirectMain {
	public static void main(String[] args) {

		long start = System.currentTimeMillis();
		int counter = 0;
		
		for (Integer i = 0; i < 50000; i++) {
			int[] a = { 10, 3, 5, 2, 1, 8 };
			int t = i;
			Integer n = i;
			ComputationObject o = new ComputationObject();
			counter ++;
			int aa = o.call(i);
			counter ++;
			int bb = o.call(11*i);
			counter ++;
			int c = aa+bb;
		}
		System.out.println(System.currentTimeMillis()-start);
		System.out.println(counter);
	}
}
