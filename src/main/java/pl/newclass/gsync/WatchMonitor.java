/*
 * This file is part of the gsyn
 *
 * (c) Newclass
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package pl.newclass.gsync;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
  private final ObjectMapper objectMapper;

  public WatchMonitor(IStorage storage, Path path, IWatchListener watchListener) {
    this.storage = storage;
    this.path = path;
    this.watchListener = watchListener;
    objectMapper = new ObjectMapper();
  }

  @Override
  public void execute() throws InterruptedException {

    try {
      if (0 == storage.size("watch")) {
        indexDir(path.toFile());
      }

      checkChanged();
    } catch (IOException e) {
      e.printStackTrace();//fixme add log
    }

    TimeUnit.SECONDS.sleep(1);
  }

  private void checkChanged() throws IOException {
    for (String index : storage.find("watch")) {
      var fileInfo= unserializable(index);
      var file = new File(fileInfo.getPath());

      if (!file.exists()) {
        storage.remove("watch", index);
        watchListener.onDelete(file);
        continue;
      }

      long lastModified = fileInfo.getLastModified();
      if (file.lastModified() == lastModified) {
        continue;
      }

      var data=serialize(file);
      storage.add("watch", file.getAbsolutePath(), data);

      if (file.isFile()) {
        watchListener.onModified(file);
        continue;
      }

      indexDir(file);
    }
  }

  private FileInfo unserializable(String data) throws IOException {
    return objectMapper.readValue(data,FileInfo.class);
  }

  private void indexDir(File file) throws IOException {
    var data = serialize(file);
    storage.add("watch", file.getAbsolutePath(), data);

    for (File children : Objects.requireNonNull(file.listFiles())) {
      var name = children.getAbsolutePath();

      if (storage.has("watch", name)) {
        continue;
      }

      watchListener.onCreate(children);

      if (children.isDirectory()) {
        indexDir(children);
        continue;
      }

      data = serialize(children);
      storage.add("watch", name, data);
    }
  }

  private String serialize(File file) throws JsonProcessingException {
    var fileInfo = new FileInfo();
    fileInfo.setLastModified(file.lastModified());
    fileInfo.setPath(file.getAbsolutePath());
    return objectMapper.writeValueAsString(fileInfo);
  }
}
