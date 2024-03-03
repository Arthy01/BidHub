package de.hwrberlin.bidhub.util;

import java.util.concurrent.*;

public abstract class TimeoutNetworkAction {
    public static void execute(Runnable task, Runnable onTimeout, int timeoutMs){
        Runnable runnable = () -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<?> future = executor.submit(task);

            try {
                future.get(timeoutMs, TimeUnit.MILLISECONDS);
            }
            catch (TimeoutException e) {
                future.cancel(true);

                if (onTimeout != null)
                    onTimeout.run();
            }
            catch (ExecutionException e) {
                System.err.println("Ausführungsfehler: " + e.getCause().getMessage());
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            finally {
                executor.shutdownNow();
            }
        };

        new Thread(runnable).start();
    }
}
