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
import pl.newclass.gsync.file.IWatchListener;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
public class WatchListener implements IWatchListener {

  private final String path;
  private final String remotePath;
  private final IQueue queue;

  public WatchListener(String path, String remotePath, IQueue queue) {
    this.path = path;
    this.remotePath = remotePath.replaceAll("^/|/$", "");
    this.queue = queue;
  }

  @Override
  public void onCreate(File file) {
    var destPath = getDestPath(file);
    queue.add(new SyncEvent(file, destPath, ActionEvent.UPDATE));
  }

  private String getDestPath(File file) {
    var destPath = file.getAbsolutePath().replaceFirst("^" + new File(path).getAbsolutePath(), "");

    return String.format("/%s%s", remotePath, destPath);
  }

  @Override
  public void onModified(File file) {
    onCreate(file);
  }
}
