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
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import pl.newclass.gsync.file.IWatchListener;
import pl.newclass.gsync.thread.AbstractMonitor;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
public class WatchMonitor extends AbstractMonitor {

  private final Path path;
  private final IWatchListener watchListener;
  private Map<String, Long> indexes = new HashMap<>();

  public WatchMonitor(Path path, IWatchListener watchListener) {
    this.path = path;
    this.watchListener = watchListener;
  }

  @Override
  public void execute() throws InterruptedException {
    Map<String, Long> cacheIndexes = new HashMap<>(indexes);
    if (0 == indexes.size()) {
      indexDir(path.toFile(), cacheIndexes);
    }

    checkChanged(cacheIndexes);

    indexes = cacheIndexes;
    TimeUnit.SECONDS.sleep(1);
  }

  private void checkChanged(Map<String, Long> changedIndexes) {
    for (Entry<String, Long> index : indexes.entrySet()) {
      var file = new File(index.getKey());

      if (file.lastModified() == index.getValue()) {
        continue;
      }

      changedIndexes.put(file.getAbsolutePath(), file.lastModified());

      if (file.isFile()) {
        watchListener.onModified(file);
        continue;
      }

      indexDir(file, changedIndexes);
    }
  }

  private void indexDir(File file, Map<String, Long> cacheIndexes) {
    cacheIndexes.put(file.getAbsolutePath(), file.lastModified());

    for (File children : Objects.requireNonNull(file.listFiles())) {
      var name = children.getAbsolutePath();

      if (cacheIndexes.containsKey(name)) {
        continue;
      }

      watchListener.onCreate(children);

      if (children.isDirectory()) {
        indexDir(children, cacheIndexes);
        continue;
      }

      cacheIndexes.put(name, children.lastModified());
    }

    //todo detect delete
  }
}
