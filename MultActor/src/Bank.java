import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Bank extends Actor {

	public Bank() {

	}

	public void addWorkers(int n) {
		for (int i = 0; i < n; i++) {
			availableActiveObjects.offer(new BankEmployee());
		}
		
		availableWorkers.release(n);
	}

	@Override
	public BankEmployee getNewWorker(Object... parameters) {
		BankEmployee selected_worker = null;
		selected_worker = (BankEmployee) availableActiveObjects.poll();
		busyWorkers.add(selected_worker);
		return selected_worker;
	}

	class BankEmployee implements ActiveObject {

		private Semaphore s;

		public BankEmployee() {
			s = new Semaphore(1);
		}

		public Account createAccount() {
			Account a = new Account();
			Bank.this.freeWorker(this);
			return a;
		}

		public boolean withdraw(Account n, int x) {
			boolean b = n.withdraw(x);
			Bank.this.freeWorker(this, n);
			return b;
		}

		protected boolean deposit(Account n, int x) {
			boolean b = n.deposit(x);
			Bank.this.freeWorker(this, n);
			return b;
		}

		public boolean transfer(Account n1, Account n2, int amount) {
			boolean b = n1.transfer(n2, amount);
			Bank.this.freeWorker(this, n1, n2);
			return b;
		}

		public int checkSavings(Account n) {
			int res = n.checkSavings();
			Bank.this.freeWorker(this, n);
			return res;
		}

		@Override
		public int compareTo(ActiveObject o) {
			return 0;
		}

		class Account {

			int i;
			int sum = 1000;

			protected boolean withdraw(int x) {
				//System.out.println(this + ": With + " + x);
				if (sum > x) {
					sum -= x;
					return true;
				}
				return false;
			}

			protected boolean deposit(int x) {
				//System.out.println(this + ": Depo" + x);
				sum += x;
				return true;

			}

			protected boolean transfer(Account n, int x) {

				if (this.withdraw(x)) {
					n.deposit(x);
					return true;
				}
				return false;
			}

			protected int checkSavings() {
				//System.out.println(this + ": Savings are " + +this.sum);
				return sum;
			}

			@Override
			public boolean equals(Object obj) {
				return super.equals(obj);
			}

			@Override
			public int hashCode() {
				return super.hashCode();

			}

			@Override
			public String toString() {
				return Integer.toString(this.hashCode() % 1000);
			}
		}

	}

}
