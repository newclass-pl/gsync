/*
 * This file is part of the gsyn
 *
 * (c) Newclass
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package pl.newclass.gsync.api;

import java.io.IOException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.newclass.gsync.BackupProviderService;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
@RestController()
public class BackupProviderController {

  private final BackupProviderService backupProviderService;

  public BackupProviderController(BackupProviderService backupProviderService){
    this.backupProviderService = backupProviderService;
  }

  @PostMapping(value = "/backup/add")
  public void add(@RequestBody SyncDirAttribute attribute) throws IOException {
    //todo
  }
}
