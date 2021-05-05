/*
 * This file is part of the gsyn
 *
 * (c) Newclass
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package pl.newclass.gsync;

import java.io.IOException;
import org.springframework.stereotype.Component;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
@Component
public class SyncService {

  private final IWatchService watchService;
  private final IBackupProviderService backupProviderService;
  private final IQueueFactory queueFactory;
  private final IWorkerService workerService;

  public SyncService(IWatchService watchService, IBackupProviderService backupProviderService,
      IQueueFactory queueFactory, IWorkerService workerService) {
    this.watchService = watchService;
    this.backupProviderService = backupProviderService;
    this.queueFactory = queueFactory;
    this.workerService = workerService;
  }

  public void watchDir(String path, String providerName) throws IOException {
    var backupProvider = backupProviderService.getProvider(providerName);
    var queue = queueFactory.create();
    watchService.addDir(path, new WatchListener(path, queue));
    workerService.run(queue, backupProvider, 5);
  }

}
