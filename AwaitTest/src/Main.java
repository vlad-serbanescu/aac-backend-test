import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import abs.api.cwi.ABSFutureTask;
import abs.api.cwi.DeploymentComponent;
import abs.api.cwi.FutureGuard;
import abs.api.cwi.LocalActor;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		Main m = new Main();
//		m.send((Runnable) (() -> m.run()));
		m.run();
		Thread.sleep(60000);
		DeploymentComponent.shutdown();
	}

	List<ABSFutureTask<Integer>> futures = new ArrayList<>();
	long start;

	public void run() {

		start = System.currentTimeMillis();
//		for (int k = 0; k < 2; k++) {
			A master = new A();
			for (Integer i = 0; i < 2000; i++) {
				int[] a = { 10, 3, 5, 2, 1, 8 };
				int t = i;
				Integer n = i;
				Callable<Integer> m = () -> master.stack(t, a[t % 5], n);
				ABSFutureTask<Integer> f = master.send(m);
				futures.add(f);
			}
//		}
		for (ABSFutureTask<Integer> future : futures) {
			// await(new FutureGuard(future), (Runnable)(()->cont(master)));
			 future.get();
//			System.out.println("get: " + future.get());
		}
		System.out.println("Time 1: " + (System.currentTimeMillis() - start));
//		System.out.println("Counter: " + master.counter);
	}

}

// 61065
// 5785
// 1650000
