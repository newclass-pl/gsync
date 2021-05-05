/*
 * This file is part of the gsyn
 *
 * (c) Newclass
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package pl.newclass.gsync;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
public class SyncConfig {

  private final String name;
  private final String path;
  private final String remotePath;
  private final String providerName;

  public SyncConfig(String name, String path, String remotePath, String providerName) {

    this.name = name;
    this.path = path;
    this.remotePath = remotePath;
    this.providerName = providerName;
  }

  public String getName() {
    return name;
  }

  public String getPath() {
    return path;
  }

  public String getRemotePath() {
    return remotePath;
  }

  public String getProviderName() {
    return providerName;
  }
}
