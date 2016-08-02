import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import abs.api.cwi.ABSFutureTask;

public class Main {

	public static void main(String[] args) {
		A master = new A();

		List<ABSFutureTask<Integer>> futures = new ArrayList<>();

		for (Integer i = 1; i < 3; i++) {
			int[] a = { 10, 3, 5, 2, 1, 8 };
			int t = i;
			Integer n = i;
			Callable<Integer> m = (Callable<Integer>) () -> master.stack(t, a[t], n);
			ABSFutureTask<Integer> f = master.send(m);
			//System.out.println("Stack "+i);
			futures.add(f);
		}

		for (ABSFutureTask<Integer> future : futures) {
			System.out.println(future.get());
		}

		// System.out.println(DeploymentComponent.actorMap);
//		System.out.println(AbstractActor.localActorMap);
		// System.out.println(master.futureContinuations);
		System.out.println(master.counter);

	}
}
