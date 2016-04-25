import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Time;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;

public class ThroughputTest {

	public static void main(String[] args) {

		int n = Integer.parseInt(args[0]);
		int m = Integer.parseInt(args[1]);
		int w = Integer.parseInt(args[2]);

		Bank b = new Bank();
		b.addWorkers(w);
		Set<Bank.BankEmployee.Account> accounts = new HashSet<Bank.BankEmployee.Account>();
		Set<ForkJoinTask<?>> futures = new HashSet<ForkJoinTask<?>>();
		Random r = new Random();

		Map<Bank.BankEmployee.Account, Set<Object>> accountVarMap = new HashMap<Bank.BankEmployee.Account, Set<Object>>();
		Map<Bank.BankEmployee.Account, Callable<Boolean>> withdrawVarMap = new HashMap<Bank.BankEmployee.Account, Callable<Boolean>>();
		Map<Bank.BankEmployee.Account, Callable<Boolean>> depositVarMap = new HashMap<Bank.BankEmployee.Account, Callable<Boolean>>();
		Map<Bank.BankEmployee.Account, Callable<Integer>> checkVarMap = new HashMap<Bank.BankEmployee.Account, Callable<Integer>>();

		
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(w + "_" + (3 * m) + ".out"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for (int j = 1; j <= n; j *= 10) {

			Callable<Bank.BankEmployee.Account> accMessage = () -> ((b
					.getNewWorker()).createAccount());

			for (int i = 0; i < j; i++) {
				futures.add(b.send(accMessage, new HashSet<Object>(), "newAcc"));
			}

			for (Future<?> future : futures) {
				try {
					accounts.add((Bank.BankEmployee.Account) future.get());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			for (Bank.BankEmployee.Account account : accounts) {
				Set<Object> vars = new HashSet<Object>();
				vars.add(account);
				accountVarMap.put(account, vars);
				Callable<Boolean> message = () -> ((b.getNewWorker()).withdraw(
						account, r.nextInt(100)));
				withdrawVarMap.put(account, message);

			}

			for (Bank.BankEmployee.Account account : accounts) {
				Callable<Boolean> message = () -> ((b.getNewWorker()).deposit(
						account, r.nextInt(100)));
				depositVarMap.put(account, message);

			}

			for (Bank.BankEmployee.Account account : accounts) {
				Callable<Integer> message = () -> ((b.getNewWorker())
						.checkSavings(account));
				checkVarMap.put(account, message);

			}

			futures.clear();
			long t1 = System.currentTimeMillis();

			System.out.println(accounts.size());
			for (Bank.BankEmployee.Account account : accounts) {
				for (int i = 0; i < m; i++) {
					futures.add(b.send(withdrawVarMap.get(account),
							accountVarMap.get(account), "withdraw"));
					futures.add(b.send(depositVarMap.get(account),
							accountVarMap.get(account), "deposit"));
					futures.add(b.send(checkVarMap.get(account),
							accountVarMap.get(account), "check"));

				}
				if (m == 0)
					futures.add(b.send(withdrawVarMap.get(account),
							accountVarMap.get(account), "withdraw"));

			}
			System.out.println(futures.size());
			for (ForkJoinTask<?> future : futures) {
				future.join();
			}

			pw.println(j + " " + ((m == 0) ? "" : m) + " "
					+ (System.currentTimeMillis() - t1));
			futures.clear();
			accounts.clear();
			accountVarMap.clear();
			withdrawVarMap.clear();
			depositVarMap.clear();
			checkVarMap.clear();
			System.out.println("done with "+ j+ "actors =================");
		}
		pw.close();

	}
}
