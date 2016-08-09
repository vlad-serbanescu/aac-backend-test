import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import abs.api.cwi.ABSFutureTask;
import abs.api.cwi.DeploymentComponent;

public class Main2 {
	private static ExecutorService mainExecutor = Executors.newFixedThreadPool(8);

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		long start = System.currentTimeMillis();
		int counter = 0;
		
		List<Future<Integer>> futures = new LinkedList<>();
		
		for (Integer i = 0; i < 2000; i++) {
			int[] a = { 10, 3, 5, 2, 1, 8 };
			int t = i;
			Integer n = i;
			ComputationObject o = new ComputationObject();
			counter ++;
			Callable<Integer> f = () -> o.call(n);
			futures.add(mainExecutor.submit(f));
			counter ++;
//			ComputationObject o1 = new ComputationObject();
			
			f = () -> o.call(n);
			futures.add(mainExecutor.submit(f));
		}
		
		int sum = 0;
//		for (Future<Integer> f : futures) {
//			sum += f.get();
//		}
//		for (Integer i = 0; i < 50000; i++) {
//			int[] a = { 10, 3, 5, 2, 1, 8 };
//			int t = i;
//			Integer n = i;
//			ComputationObject o = new ComputationObject();
//			Callable<Integer> f = () -> o.call(n);
//			futures.add(mainExecutor.submit(f));
//			counter ++;
//		}
//		
		for (Future<Integer> f : futures) {
			sum += f.get();
		}
		System.out.println(System.currentTimeMillis()-start);
		System.out.println(counter);
		mainExecutor.shutdown();
	}
}
