/*
 * This file is part of the gsyn
 *
 * (c) Newclass
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package pl.newclass.gsync.worker;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.stereotype.Component;
import pl.newclass.gsync.IBackupProvider;
import pl.newclass.gsync.IQueue;
import pl.newclass.gsync.IWorkerService;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
@Component
public class WorkerService implements IWorkerService {

  private final Executor executor;

  public WorkerService() {
    executor = Executors.newWorkStealingPool();
  }

  @Override
  public void run(IQueue queue, IBackupProvider backupProvider, int quantity) {
    for (int i = 0; i < quantity; i++) {
      executor.execute(new WorkerThread(queue, backupProvider));
    }
  }
}
