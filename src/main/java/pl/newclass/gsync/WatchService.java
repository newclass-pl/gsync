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
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import pl.newclass.gsync.file.IWatchListener;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
@Component
public class WatchService implements IWatchService {

  private final IWatchMonitorFactory watchMonitorFactory;
  private final List<WatchMonitor> watchMonitors = new ArrayList<>();

  public WatchService(WatchMonitorFactory watchMonitorFactory) {
    this.watchMonitorFactory = watchMonitorFactory;
  }

  @Override
  public void addDir(String path, IWatchListener watchListener) throws FileNotFoundException {
    var patchObj = Path.of(path);
    if (!patchObj.toFile().exists()) {
      throw new FileNotFoundException(path);
    }

    var watchMonitor = watchMonitorFactory.create(patchObj, watchListener);
    watchMonitors.add(watchMonitor);
    watchMonitor.start();
  }
}
