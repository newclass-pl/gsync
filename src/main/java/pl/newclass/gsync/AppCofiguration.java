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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.newclass.gsync.storage.FileStorage;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
@Configuration
public class AppCofiguration {

  @Bean(name = "appStorage")
  public IStorage createStorage() throws IOException {
    return new FileStorage(new File("var"));
  }
}
