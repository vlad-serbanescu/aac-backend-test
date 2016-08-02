import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


public class Tester {
	static ExecutorService mainExecutor = Executors.newFixedThreadPool(8);
	static int n = 4;
	static AtomicBoolean[] bools = new AtomicBoolean[n];
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		for (int i=0; i<n; i++) {
			bools[i] = new AtomicBoolean();
		}
		List<Future<Integer>> futures = new LinkedList<>();
		long start = System.currentTimeMillis();
		for (int i=0; i<n*200; i++){
			CompletableFuture<Integer> f = new CompletableFuture<Integer>();
			futures.add(f);
			mainExecutor.submit(new TestCallable(i, f));
		}
		System.out.println("Started all.");
		for (Future<Integer> f:futures) {
//			System.out.println(f.get());
			f.get();
		}
		mainExecutor.shutdown();
		try {
			while (!mainExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("Time in ms: " + (System.currentTimeMillis()-start));
	}
}

class TestCallable implements Runnable {
	final int id;
	final CompletableFuture<Integer> f;
	
	public TestCallable(int id, CompletableFuture<Integer> f) {
		this.id = id;
		this.f = f;
	}

	@Override
	public void run() {
		if (!Tester.bools[id % Tester.n].compareAndSet(false, true)) {
//			if (id % 100 == 0) 
				System.out.println("resubmitting "+id);
			Tester.mainExecutor.submit(this);
		}
		int sum = 0;
		for (int i=0; i<500000000; i++){
			sum += i;
		}
		if (id % 10 == 0) System.out.println(id);
		Tester.bools[id % Tester.n].set(false);
		f.complete(sum);
	}
	
}