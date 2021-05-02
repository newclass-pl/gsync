/*
 * This file is part of the gsyn
 *
 * (c) Newclass
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package pl.newclass.gsync.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
public abstract class AbstractMonitor implements Runnable {

  private final ExecutorService executor;

  public AbstractMonitor() {
    executor = Executors
        .newSingleThreadExecutor(t -> new Thread(t, this.getClass().getCanonicalName()));
  }

  public void start() {
    executor.execute(this);
  }

  public void stop(){
    executor.shutdownNow();
  }

  @Override
  public void run() {
    while (Thread.currentThread().isAlive()) {
      try {
        execute();
      } catch (InterruptedException e) {
        return;
      }
    }
  }

  abstract public void execute() throws InterruptedException;
}
