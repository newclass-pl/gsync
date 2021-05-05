/*
 * This file is part of the gsyn
 *
 * (c) Newclass
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package pl.newclass.gsync.api;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
public class SyncDirAttribute {

  private String path;
  private String name;
  private String provider;
  private String remotePath;

  public String getPath() {
    return path;
  }

  public String getName() {
    return name;
  }

  public String getProvider() {
    return provider;
  }

  public String getRemotePath() {
    return remotePath;
  }
}
