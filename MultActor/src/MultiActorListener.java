import java.util.Queue;
import java.util.concurrent.ForkJoinTask;

public class MultiActorListener implements Runnable {

	Actor toListen;

	public MultiActorListener(Actor toListen) {
		this.toListen = toListen;
	}

	@Override
	public void run() {
		while (!toListen.messageQueue.isEmpty()) {

			try {
				toListen.availableWorkers.acquire();

				Queue<Message> q = toListen.messageQueue;
				Message m = q.poll();
				ForkJoinTask<?> next = m.f;
				toListen.mainExecutor.submit(next);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		toListen.signal.set(0);
	}
}
