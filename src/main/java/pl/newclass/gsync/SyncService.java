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
import java.io.FileNotFoundException;
import org.springframework.stereotype.Component;
import pl.newclass.gsync.file.IWatchListener;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
@Component
public class SyncService {

  private final IWatchService watchService;

  public SyncService(IWatchService watchService) {
    this.watchService = watchService;
  }

  public void watchDir(String path) throws FileNotFoundException {
    watchService.addDir(path, createTestListener());
  }

  private IWatchListener createTestListener() {
    return new IWatchListener() {
      @Override
      public void onCreate(File file) {
        System.err.printf("Created file %s\n", file.getAbsolutePath());
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
