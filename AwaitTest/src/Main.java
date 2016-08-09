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

public class Main extends LocalActor{

	public static void main(String[] args) throws InterruptedException {
		Main m = new Main();
		m.send((Runnable)(()->m.run()));
		Thread.sleep(60000);
		DeploymentComponent.shutdown();
	}
	
	List<ABSFutureTask<Integer>> futures = new ArrayList<>();
	long start;
	
	public void run() {
		A master = new A();

		start = System.currentTimeMillis();
		
		for (Integer i = 0; i < 50000; i++) {
			int[] a = { 10, 3, 5, 2, 1, 8 };
			int t = i;
			Integer n = i;
//			Callable<Integer> m = (Callable<Integer>) () -> master.stack(t, a[t%5], n);
			ComputationObject co = new ComputationObject();
			Callable<Integer> comp = (Callable<Integer>) () -> co.call(t);
			ABSFutureTask<Integer> f = co.send(comp);
			master.counter++;
			ComputationObject co1 = new ComputationObject();
			ABSFutureTask<Integer> f2 = co1.send(comp);
			master.counter++;
			//System.out.println("Stack "+i);
			futures.add(f);
			futures.add(f2);
			
		}

		for (ABSFutureTask<Integer> future : futures) {
//			await(new FutureGuard(future), (Runnable)(()->cont(master)));
			future.get();
//			System.out.println(future.get());
		}
		System.out.println("Time 1: "+(System.currentTimeMillis()-start));
		System.out.println("Counter: "+master.counter);
	}
	
	public void cont(A master) {
		for (ABSFutureTask<Integer> future : futures) {
			await(new FutureGuard(future), (Runnable)(()->cont(master)));
		}
		System.out.println("Time 1: "+(System.currentTimeMillis()-start));
		System.out.println("Counter: "+master.counter);
		
	}
}
