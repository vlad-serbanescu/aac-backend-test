import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class StackerObject extends ScheduledThreadPoolExecutor {

	Semaphore s = new Semaphore(1);
	
	

	public StackerObject(int corePoolSize) {
		super(corePoolSize);
		
		// Always create 1 Thread
		
	}

	public class Stacker implements Callable<Void> {
		int numberToStack = 10,id;
		
		public Stacker(int n, int i) {
			// TODO Auto-generated constructor stub
			numberToStack=n;
			id=i;
		}
		
		public void m(int i) {
			ComputationObject c = null;
			if (i > 0)
				this.m(i - 1);
			else{
				c = new ComputationObject(1);
				ComputationObject.Computation call = c.new Computation();
				Future<Integer> f = c.submit(call);
				try {
					s.release();
					StackerObject.this.setCorePoolSize(StackerObject.this.getCorePoolSize()+1);
					f.get();
					StackerObject.this.setCorePoolSize(StackerObject.this.getCorePoolSize()-1);
					s.acquire();
					
					//System.out.println(id);
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				c.shutdown();
			}

		}

		@Override
		public Void call() throws Exception {
			// TODO Auto-generated method stub
			//s.acquire();
			m(numberToStack);
			//s.release();
			return null;
		}

		
	}
}
