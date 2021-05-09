/*
 * This file is part of the gsyn
 *
 * (c) Newclass
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package pl.newclass.gsync;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import pl.newclass.gsync.provider.GoogleDriveBackupProviderFactory;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
@Component
public class BackupProviderFactoryCollection {

  private final Map<String, IBackupProviderFactory> factories = new HashMap<>();

  public BackupProviderFactoryCollection(
      GoogleDriveBackupProviderFactory googleDriveBackupProviderFactory) {
    add(googleDriveBackupProviderFactory);
  }

  private void add(IBackupProviderFactory factory) {
    factories.put(factory.getName(), factory);
  }

  public IBackupProviderFactory get(String name) {
    return factories.get(name); //fixme check if exists
  }
}
