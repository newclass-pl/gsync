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
import java.util.HashMap;
import java.util.Map;
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
  private final Map<String, SyncConfig> configs = new HashMap<>();

  public SyncService(IWatchService watchService, IBackupProviderService backupProviderService,
      IQueueFactory queueFactory, IWorkerService workerService) {
    this.watchService = watchService;
    this.backupProviderService = backupProviderService;
    this.queueFactory = queueFactory;
    this.workerService = workerService;
  }

  public synchronized void watchDir(String name, String path, String remotePath,
      String providerName)
      throws IOException {
    //fixme check if exists name
    var backupProvider = backupProviderService.getProvider(providerName);
    var queue = queueFactory.create();
    watchService.addDir(path, new WatchListener(path, remotePath, queue));
    workerService.run(queue, backupProvider, 5);

    configs.put(name, new SyncConfig(name, path, remotePath, providerName));
    //fixme save config to file
  }

}
