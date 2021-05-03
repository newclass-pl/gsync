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
import pl.newclass.gsync.provider.GoogleDriveBackupProvider;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
@Component
public class BackupProviderService implements IBackupProviderService {

  private final Map<String, IBackupProvider> providers = new HashMap<>();

  public BackupProviderService() { //fixme add storage service
    providers.put("test",
        new GoogleDriveBackupProvider("var/credential.json", "var/token.json", 8088));
  }

  @Override
  public IBackupProvider getProvider(String providerName) throws IOException {
    var provider = providers.get(providerName);

    if (null != provider) {
      return provider;
    }

    throw new IOException(String.format("Provider %s not found.", providerName));
  }
}
