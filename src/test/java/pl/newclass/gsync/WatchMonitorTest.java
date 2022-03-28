package pl.newclass.gsync;

import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.FileSystemUtils;
import pl.newclass.gsync.file.IWatchListener;
import pl.newclass.gsync.storage.FileStorage;

/*
 * This file is part of the gsyn
 *
 * (c) Newclass
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

/**
 *
 */
@ExtendWith(MockitoExtension.class)
class WatchMonitorTest {

  private WatchMonitor watchMonitor;

  @Mock
  private IStorage storage;

  @Mock
  private IWatchListener watchListener;

  private final ObjectMapper objectMapper;
  private File filePath;

  public WatchMonitorTest() {
    objectMapper = new ObjectMapper();
  }

  @BeforeEach
  public void setUp() throws IOException {
    Path path = Path.of("var", "test");
    FileSystemUtils.deleteRecursively(path.toFile());
    path.toFile().mkdirs();
    filePath = Path.of("var", "test", "file").toFile();
    filePath.mkdirs();

    storage = new FileStorage(path.toFile());
    watchMonitor = new WatchMonitor(storage, Path.of("var", "test", "file"), watchListener);
  }

  @AfterEach
  public void tearDown() {
    watchMonitor.stop();
  }

  @Test
  void executeAddNewDir() throws InterruptedException, IOException {
    var data = serialize(filePath);
    storage.add("watch", filePath.getAbsolutePath(), data);
    watchMonitor.start();

    TimeUnit.SECONDS.sleep(2);
    var newDir = Path.of(filePath.getAbsolutePath(), "new_dir").toFile();
    newDir.mkdir();
    TimeUnit.SECONDS.sleep(2);

    verify(watchListener).onCreate(ArgumentMatchers.eq(newDir));
    verify(watchListener, never()).onDelete(ArgumentMatchers.any());
    verify(watchListener, never()).onModified(ArgumentMatchers.any());
  }

  @Test
  void executeRenameDir() throws InterruptedException, IOException {
    var data = serialize(filePath);
    storage.add("watch", filePath.getAbsolutePath(), data);

    var newDir = Path.of(filePath.getAbsolutePath(), "new_dir").toFile();
    newDir.mkdir();
    storage.add("watch", newDir.getAbsolutePath(), serialize(newDir));

    watchMonitor.start();

    TimeUnit.SECONDS.sleep(2);

    var renamedDir = Path.of(filePath.getAbsolutePath(), "renamed_dir").toFile();
    newDir.renameTo(renamedDir);

    TimeUnit.SECONDS.sleep(2);

    verify(watchListener).onDelete(ArgumentMatchers.eq(newDir));
    verify(watchListener).onCreate(ArgumentMatchers.eq(renamedDir));
    verify(watchListener, never()).onModified(ArgumentMatchers.any());
  }

  @Test
  void executeDeleteDir() throws InterruptedException, IOException {
    var data = serialize(filePath);
    storage.add("watch", filePath.getAbsolutePath(), data);

    var newDir = Path.of(filePath.getAbsolutePath(), "new_dir").toFile();
    newDir.mkdir();
    storage.add("watch", newDir.getAbsolutePath(), serialize(newDir));

    watchMonitor.start();

    TimeUnit.SECONDS.sleep(2);

    newDir.delete();

    TimeUnit.SECONDS.sleep(2);

    verify(watchListener).onDelete(ArgumentMatchers.eq(newDir));
    verify(watchListener, never()).onCreate(ArgumentMatchers.any());
    verify(watchListener, never()).onModified(ArgumentMatchers.any());
  }

  @Test
  void executeModifiedFile() throws InterruptedException, IOException {
    var data = serialize(filePath);
    storage.add("watch", filePath.getAbsolutePath(), data);

    var newFile = Path.of(filePath.getAbsolutePath(), "new_file").toFile();
    var fileWriter = new FileWriter(newFile);
    fileWriter.write("current data");
    fileWriter.flush();

    storage.add("watch", newFile.getAbsolutePath(), serialize(newFile));

    watchMonitor.start();

    TimeUnit.SECONDS.sleep(2);

    fileWriter.write("updated data");
    fileWriter.flush();

    TimeUnit.SECONDS.sleep(2);

    verify(watchListener, never()).onDelete(ArgumentMatchers.any());
    verify(watchListener, never()).onCreate(ArgumentMatchers.any());
    verify(watchListener).onModified(ArgumentMatchers.eq(newFile));
  }

  private String serialize(File file) throws JsonProcessingException {
    var fileInfo = new FileInfo();
    fileInfo.setLastModified(file.lastModified());
    fileInfo.setPath(file.getAbsolutePath());
    return objectMapper.writeValueAsString(fileInfo);
  }

}