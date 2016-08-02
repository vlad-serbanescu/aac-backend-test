import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import abs.api.cwi.ABSFutureTask;
import abs.api.cwi.DeploymentComponent;

public class Main {

	public static void main(String[] args) {
		A master = new A();

		List<ABSFutureTask<Integer>> futures = new ArrayList<>();

		long start = System.currentTimeMillis();
		
		for (Integer i = 0; i < 50000; i++) {
			int[] a = { 10, 3, 5, 2, 1, 8 };
			int t = i;
			Integer n = i;
			Callable<Integer> m = (Callable<Integer>) () -> master.stack(t, a[t%5], n);
			ABSFutureTask<Integer> f = master.send(m);
			//System.out.println("Stack "+i);
			futures.add(f);
		}

		for (ABSFutureTask<Integer> future : futures) {
			future.get();
//			System.out.println(future.get());
		}

		System.out.println(System.currentTimeMillis()-start);

		start = System.currentTimeMillis();
		
		for (Integer i = 0; i < 50000; i++) {
			int[] a = { 10, 3, 5, 2, 1, 8 };
			int t = i;
			Integer n = i;
			Callable<Integer> m = (Callable<Integer>) () -> master.stack(t, a[t%5], n);
			ABSFutureTask<Integer> f = master.send(m);
			//System.out.println("Stack "+i);
			futures.add(f);
		}

		for (ABSFutureTask<Integer> future : futures) {
			future.get();
//			System.out.println(future.get());
		}

		System.out.println(System.currentTimeMillis()-start);		// System.out.println(DeploymentComponent.actorMap);
//		System.out.println(AbstractActor.localActorMap);
		// System.out.println(master.futureContinuations);
		System.out.println(master.counter);
		DeploymentComponent.shutdown();
	}
}
