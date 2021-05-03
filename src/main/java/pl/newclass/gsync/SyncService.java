/*
 * This file is part of the gsyn
 *
 * (c) Newclass
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package pl.newclass.gsync;

import java.io.File;
import java.io.IOException;
import org.springframework.stereotype.Component;
import pl.newclass.gsync.file.IWatchListener;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
@Component
public class SyncService {

  private final IWatchService watchService;
  private final IBackupProviderService backupProviderService;

  public SyncService(IWatchService watchService, IBackupProviderService backupProviderService) {
    this.watchService = watchService;
    this.backupProviderService = backupProviderService;
  }

  public void watchDir(String path, String providerName) throws IOException {
    var backupProvider = backupProviderService.getProvider(providerName);
    watchService.addDir(path, createTestListener(path, backupProvider));
  }

  private IWatchListener createTestListener(String path, IBackupProvider backupProvider) {
    File mainDir = new File(path);

    return new IWatchListener() {
      @Override
      public void onCreate(File file) {
        System.err.printf("Created file %s\n", file.getAbsolutePath());
        try {
          var destPath = file.getAbsolutePath().replaceFirst("^" + mainDir.getAbsolutePath(), "");
          backupProvider.send(file, "/test" + destPath);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      @Override
      public void onModified(File file) {
        System.err.printf("modified file %s\n", file.getAbsolutePath());
      }

      @Override
      public int hashCode() {
        return super.hashCode();
      }
    };
  }
}
