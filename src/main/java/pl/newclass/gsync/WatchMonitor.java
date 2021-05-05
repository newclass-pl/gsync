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
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import pl.newclass.gsync.file.IWatchListener;
import pl.newclass.gsync.thread.AbstractMonitor;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
public class WatchMonitor extends AbstractMonitor {

  private final IStorage storage;
  private final Path path;
  private final IWatchListener watchListener;

  public WatchMonitor(IStorage storage, Path path, IWatchListener watchListener) {
    this.storage = storage;
    this.path = path;
    this.watchListener = watchListener;
  }

  @Override
  public void execute() throws InterruptedException {

    try{
      if (0 == storage.size(String.format("path-%s", path))) {
        indexDir(path.toFile());
      }

      checkChanged();
    }
    catch (IOException e){
      e.printStackTrace();//fixme add log
    }

    TimeUnit.SECONDS.sleep(1);
  }

  private void checkChanged() throws IOException {
    for (String index : storage.find(String.format("path-%s", path))) {
      var file = new File(index);

      long lastModified = Long.parseLong(storage.get(index));
      if (file.lastModified() == lastModified) {
        continue;
      }

      storage.put(file.getAbsolutePath(), file.lastModified());

      if (file.isFile()) {
        watchListener.onModified(file);
        continue;
      }

      indexDir(file);
    }
  }

  private void indexDir(File file) throws IOException {
    storage.add(String.format("path-%s", path), file.getAbsolutePath());
    storage.put(file.getAbsolutePath(), file.lastModified());

    for (File children : Objects.requireNonNull(file.listFiles())) {
      var name = children.getAbsolutePath();

      if (storage.has(name)) {
        continue;
      }

      watchListener.onCreate(children);

      if (children.isDirectory()) {
        indexDir(children);
        continue;
      }

      storage.add(String.format("path-%s", path), name);
      storage.put(name, children.lastModified());

    }

    //todo detect delete
  }
}
