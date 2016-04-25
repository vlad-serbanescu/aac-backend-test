import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicInteger;

public class Message {

	static final AtomicInteger queuePriority = new AtomicInteger(0);

	String name;
	Object lambdaExpression;
	Set<Object> syncVariables;
	ForkJoinTask<?> f;
	AtomicInteger preemptPriority;
	int priority = 0;

	public Message(Object message, Set<Object> variables, String name) {
		this.lambdaExpression = message;
		this.syncVariables = variables;
		this.name = name;
		this.preemptPriority = new AtomicInteger(0);
		priority = queuePriority.getAndAdd(1);

		f = null;
		if (message instanceof Runnable)
			f = ForkJoinTask.adapt((Runnable) message);
		if (message instanceof Callable<?>)
			f = ForkJoinTask.adapt((Callable<?>) message);
	}

	public Message(Message m) {
		// TODO Auto-generated constructor stub
		this(m.lambdaExpression, m.syncVariables, m.name);
		this.preemptPriority.set(m.preemptPriority.get());
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name + " " + syncVariables + " :< " + priority + ","
				+ preemptPriority + " >";
	}
}
