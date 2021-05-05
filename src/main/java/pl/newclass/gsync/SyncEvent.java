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

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
public class SyncEvent {

  private final File file;
  private final String destPath;
  private final ActionEvent action;
  private final long lastModified;

  public SyncEvent(File file, String remotePath, ActionEvent action) {
    this.file = file;
    this.destPath = remotePath;
    this.action = action;
    this.lastModified = file.lastModified();
  }

  public File getFile() {
    return file;
  }

  public String getDestPath() {
    return destPath;
  }

  public ActionEvent getAction() {
    return action;
  }

  public long getLastModified() {
    return lastModified;
  }
}
