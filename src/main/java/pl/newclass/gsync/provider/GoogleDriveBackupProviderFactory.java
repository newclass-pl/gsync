/*
 * This file is part of the gsyn
 *
 * (c) Newclass
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package pl.newclass.gsync.provider;

import java.util.Map;
import org.springframework.stereotype.Component;
import pl.newclass.gsync.ConfigBuilder;
import pl.newclass.gsync.IBackupProvider;
import pl.newclass.gsync.IBackupProviderFactory;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
@Component
public class GoogleDriveBackupProviderFactory implements IBackupProviderFactory {

  @Override
  public String getName() {
    return "google-drive";
  }

  @Override
  public void configure(ConfigBuilder configBuilder) {
  }

  @Override
  public IBackupProvider create(Map<String, Object> config) {
    //fixme add absolute path
    return new GoogleDriveBackupProvider("var/credential.json", "var/token.json", 8088);
  }
}
