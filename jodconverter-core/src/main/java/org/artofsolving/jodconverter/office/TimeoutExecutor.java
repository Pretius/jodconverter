package org.artofsolving.jodconverter.office;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TimeoutExecutor {

	private ExecutorService executor = Executors.newSingleThreadExecutor();

	public void shudown() {
		executor.shutdown();
	}

	public void executeWithTimeout(Runnable r, long timeoutMs) {
		Future<?> future = executor.submit(r);
		try {
			future.get(timeoutMs, TimeUnit.MILLISECONDS);
		} catch (ExecutionException e) {
			throw new OfficeException("unexpected error", e.getCause());
		} catch (InterruptedException e) {
			throw new OfficeException("failed to wait for task", e);
		} catch (TimeoutException e) {
			// task will be cancelled in finally
			throw new OfficeException("failed to complete within timeout", e);
		} finally {
			// harmless if already completed
			future.cancel(true);
		}
	}
}
