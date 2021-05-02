package pl.newclass.gsync;/*
 * This file is part of the gsyn
 *
 * (c) Newclass
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

import java.nio.file.Path;
import org.springframework.stereotype.Component;
import pl.newclass.gsync.file.IWatchListener;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
@Component
public class WatchMonitorFactory implements IWatchMonitorFactory {

  @Override
  public WatchMonitor create(Path patchObj, IWatchListener watchListener) {
    return new WatchMonitor(patchObj, watchListener);
  }
}
