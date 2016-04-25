import java.util.Comparator;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Actor {

	protected ForkJoinPool mainExecutor;
	protected ConcurrentLinkedQueue<Message> messageQueue, lockedQueue;
	protected ConcurrentLinkedQueue<Actor.ActiveObject>availableActiveObjects;
	protected Set<ActiveObject> busyWorkers;
	protected AtomicInteger signal;
	protected Semaphore availableWorkers;

	protected Set<Object> busyVariables;

	public Actor() {

		signal = new AtomicInteger(0);
		mainExecutor = new ForkJoinPool();
		availableActiveObjects = new ConcurrentLinkedQueue<Actor.ActiveObject>();
		lockedQueue = new ConcurrentLinkedQueue<Message>();
		messageQueue = new ConcurrentLinkedQueue<Message>();

		busyWorkers = new ConcurrentSkipListSet<Actor.ActiveObject>();
		busyVariables = new HashSet<Object>();
		
		availableWorkers = new Semaphore(availableActiveObjects.size());
	}

	// message format: ()->getWorker().m()
	public <V> ForkJoinTask<V> send(Object message, Set<Object> variables, String name) {
		Message m = new Message(message, variables, name);
		lockedQueue.offer(m);
		synchronized (busyVariables) {
			if (busyVariables.isEmpty()) {
				for (Message locked : lockedQueue) {
					if (reportSynchronizedVariables(locked.syncVariables)) {
						lockedQueue.remove(locked);
						messageQueue.offer(locked);
						busyVariables.addAll(locked.syncVariables);
					}

				}
				if (this.signal.compareAndSet(0, 1)) {
					new Thread(new MultiActorListener(this)).start();
				}
			}

		}
		return (ForkJoinTask<V>) m.f;
	}

	public ActiveObject getNewWorker(Object... parameters) {
		ActiveObject selected_worker = null;
		selected_worker = availableActiveObjects.poll();
		busyWorkers.add(selected_worker);
		return selected_worker;
	}

	private boolean reportSynchronizedVariables(Set<Object> variables) {
		Set<Object> tempSet = new HashSet<Object>();
		synchronized (busyVariables) {

			tempSet.addAll(busyVariables);
			tempSet.retainAll(variables);

			if (tempSet.isEmpty()) {
				return true;
			}
			return false;
		}
	}

	protected void freeWorker(ActiveObject worker, Object... variables) {

		synchronized (busyVariables) {
			busyWorkers.remove(worker);
			availableActiveObjects.offer(worker);

			availableWorkers.release();
			
			
			for (Object object : variables) {
				busyVariables.remove(object);
			}

			
			for (Message message : lockedQueue) {
				if (reportSynchronizedVariables(message.syncVariables)) {
					lockedQueue.remove(message);
					messageQueue.offer(message);
					busyVariables.addAll(message.syncVariables);
				}

			}
			if (this.signal.compareAndSet(0, 1)) {
				new Thread(new MultiActorListener(this)).start();
			}

		}

	}

	public interface ActiveObject extends Comparable<ActiveObject> {

		default void complete(Semaphore s) {
			s.release();
		}

		default void start(Semaphore s) {
			try {
				s.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		default void resume(Semaphore s) {
			try {
				s.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
