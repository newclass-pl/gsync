/*
 * This file is part of the gsyn
 *
 * (c) Newclass
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package pl.newclass.gsync.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.newclass.gsync.SyncService;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
@RestController()
public class SyncController {

  private final SyncService syncService;

  public SyncController(SyncService syncService) {
    this.syncService = syncService;
  }

  @GetMapping("/sync/dirs")
  public Map<String, String> getDirs() throws FileNotFoundException {
    Map<String, String> test = new HashMap<>();
    test.put("test", "a");
    return test;
  }

  @PostMapping(value = "/sync/dir")
  public void addDir(@RequestBody SyncDirAttribute attribute) throws IOException {
    syncService.watchDir(attribute.getName(), attribute.getPath(), attribute.getRemotePath(),
        attribute.getProvider());
  }
}
