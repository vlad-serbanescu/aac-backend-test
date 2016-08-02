import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

import abs.api.cwi.ABSFutureTask;
import abs.api.cwi.FutureGuard;
import abs.api.cwi.LocalActor;


public class A extends LocalActor {
	int counter=0;
	
	public Integer stack(int i, int a, int b){
		
		//if(i>0)
			//stack(i-1,a,b);
		
		//System.out.println("Message "+i);
		ComputationObject co = new ComputationObject();
		Callable<Integer> msg = (Callable<Integer>)(()->co.call(i));
		ABSFutureTask<Integer> m = co.send(msg);
		
		this.counter++;
		
		System.out.println("Message "+i+" sets counter to "+this.counter);
		
		Callable<Integer> cont= (Callable<Integer>)(()->{
			int s =a+b;
			System.out.println("Outer await of message"+i);
			
			this.counter++;
			
			System.out.println("Message "+i+" sets counter to "+this.counter);
			
			Callable<Integer> msg1 = (Callable<Integer>)(()->co.call(i*11));
			ABSFutureTask<Integer> m1 = co.send(msg1);
			
			
			Callable<Integer> cont1= (Callable<Integer>)(()->{
				System.out.println("Inner await of message "+i);
				
				this.counter++;
				
				System.out.println("Message "+i+" sets counter to "+this.counter);
				
				
				return this.counter;
			});
			
			await(new FutureGuard(m1), cont1);
			System.out.println("Inner await of message "+i);
			
			this.counter++;
			
			System.out.println("Message "+i+" sets counter to "+this.counter);
			
			
			return this.counter;
		});
		
		await(new FutureGuard(m), cont);
		int s =a+b;
		System.out.println("Outer await of message"+i);
		
		this.counter++;
		
		System.out.println("Message "+i+" sets counter to "+this.counter);
		
		Callable<Integer> msg1 = (Callable<Integer>)(()->co.call(i*11));
		ABSFutureTask<Integer> m1 = co.send(msg1);
		
		
		Callable<Integer> cont1= (Callable<Integer>)(()->{
			System.out.println("Inner await of message "+i);
			
			this.counter++;
			
			System.out.println("Message "+i+" sets counter to "+this.counter);
			
			
			return this.counter;
		});
		
		await(new FutureGuard(m1), cont1);
		System.out.println("Inner await of message "+i);
		
		this.counter++;
		
		System.out.println("Message "+i+" sets counter to "+this.counter);
		
		
		return this.counter;
	}
	
//	public Integer repeat (Integer i){
//		List<ABSFutureTask<?>> repetitive = new ArrayList<ABSFutureTask<?>>();
//		
//		int a=0;
//		int b=1;
//		int c= a+b;
//		System.out.println("Message "+i);
//		for (int j = 0; j < 3; j++) {
//			ComputationObject co = new ComputationObject();
//			int d = j;
//			Callable<Integer> msg = (Callable<Integer>)(()->co.call(d));
//			ABSFutureTask<Integer> m = co.send(msg);
//			repetitive.add(m);
//		}
//		
//		System.out.println(repetitive.size());
//		Iterator<ABSFutureTask<?>> it = repetitive.iterator();
//		Runnable before= ()->{
//			int x=a;
//			int y=b;
//			x=a+y;
//			y=x;
//			System.out.println("Message "+i+" before await");
//		};
//		
//		Runnable after = ()->{
//			System.out.println("Message "+i+" after await");
//		};
//		
//		Callable<Integer> end = ()->{
//			System.out.println("Message "+i+" end of loop");
//			return a;
//		};
//		
//		Supplier<Boolean> sup = new Supplier<Boolean>() {
//
//			@Override
//			public Boolean get() {
//				System.out.println("Message "+i+" is at iteration "+ it.hasNext()+" of "+repetitive.size());
//				return it.hasNext();
//			}
//			
//		};
//		
//		Supplier<Future<?>> fut = new Supplier<Future<?>>() {
//			
//			@Override
//			public Future<?> get() {
//				return it.next().f;
//			}
//		};
//		
//		System.out.println("Created all statements for Message "+i);
//		
//		return awaitRep(sup, before, after, end, fut);
//		
//	}
//	
//	public Integer repetitive(Integer i) {
//		System.out.println(i);
//		List<Message<Integer>> repetitive = new ArrayList<Message<Integer>>();
//		
//		
//		for (int j = 0; j < 3; j++) {
//			ComputationObject co = new ComputationObject();
//			int d = j;
//			Callable<Integer> msg = (Callable<Integer>)(()->co.call(d));
//			Message<Integer> m = co.send(msg);
//			repetitive.add(m);
//		}
//		
//		Iterator<Message<Integer>> it = repetitive.iterator();
//		return recursive(it, it.next().f, i,1);
//		
//	}
//	
//	public Integer recursive(Iterator<Message<Integer>> it, Future<?> f, int i, int a){
//		if(it.hasNext()){
//			System.out.println("Message "+i+" before await at iteration "+ a+ "/"+ 3);
//			Callable<Integer> loopContinuation = (Callable<Integer>) (() -> {
//				System.out.println("Message "+i+" after await");
//				return recursive(it, it.next().f, i,a+1);
//			});
//			return await(f, loopContinuation);			
//		}
//		else{
//			System.out.println("Message "+i+" end of loop");
//			Supplier<Boolean> s = new Supplier<Boolean>() {
//
//				@Override
//				public Boolean get() {
//					// TODO Auto-generated method stub
//					return null;
//				}
//			};
//			return a;
//		}
//		
//	}
}
