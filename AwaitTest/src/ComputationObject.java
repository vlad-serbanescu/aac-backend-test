import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Semaphore;

import abs.api.cwi.ABSFutureTask;
import abs.api.cwi.LocalActor;

public class ComputationObject extends LocalActor {

	public Integer call(Integer x) {
		double a = 0;
		int n = 1000;
		for (int i = 0; i < Math.abs(n); i++)
			a = Math.tan(i);
		
		//System.out.println(x);
		//System.out.println("function that message "+x+" awaits on(single digits for outer await:double digits for inner await)");
		
		return 1;

	}
	
	public ABSFutureTask<Integer> _call(Integer x) {
		Callable<Integer> m = ()->this.call(x);
		return send(m);
	}
}