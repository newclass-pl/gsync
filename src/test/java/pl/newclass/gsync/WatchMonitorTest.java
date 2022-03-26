package pl.newclass.gsync;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.newclass.gsync.file.IWatchListener;

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

  @BeforeEach
  public void setUp() {
    Path path = Path.of("var", "test");
    path.toFile().delete();
    path.toFile().mkdirs();
    watchMonitor = new WatchMonitor(storage, Path.of("var", "test"), watchListener);

  }

  @Test
  void executeAddNewDir() throws InterruptedException, IOException {
    when(storage.size(ArgumentMatchers.eq("watch"))).thenReturn(1);
    var watchFind = new ArrayList<String>();
    watchFind.add("{\"path\":\"var/test\",\"lastModified\":1648329857086}");
    Iterable watchFindIterator = new StringIteratorFactory(watchFind);
    when(storage.find("watch")).thenReturn(watchFindIterator);

    watchMonitor.execute();

    Path.of("var", "test", "new_dir").toFile().mkdir();
    TimeUnit.SECONDS.sleep(1);
    verify(watchListener).onCreate(ArgumentMatchers.eq(new File("var/test/new_dir")));
  }

  @Test
  void executeRenameDir() throws InterruptedException, IOException {
    File newDir = Path.of("var", "test", "new_dir").toFile();
    if(!newDir.mkdir()){
      throw new RuntimeException("Cannot create dir");
    }

    File testDir = Path.of("var", "test").toFile();

    when(storage.size(ArgumentMatchers.eq("watch"))).thenReturn(1);
    var watchFind = new ArrayList<String>();
    watchFind.add("{\"path\":\"var/test\",\"lastModified\":" + testDir.lastModified() + "}");
    watchFind.add("{\"path\":\"var/test/new_dir\",\"lastModified\":" + newDir.lastModified() + "}");
    Iterable watchFindIterator = new StringIteratorFactory(watchFind);
    when(storage.find("watch")).thenReturn(watchFindIterator);
    when(
        storage.has(ArgumentMatchers.eq("watch"), ArgumentMatchers.contains("new_dir"))).thenReturn(
        true);
    when(
        storage.has(ArgumentMatchers.eq("watch"),
            ArgumentMatchers.contains("renamed_dir"))).thenReturn(
        false);
    watchMonitor.execute();

    Path.of("var", "test", "new_dir").toFile()
        .renameTo(Path.of("var", "test", "renamed_dir").toFile());
    TimeUnit.SECONDS.sleep(1);

    verify(watchListener).onDelete(ArgumentMatchers.eq(new File("var/test/new_dir")));
    verify(watchListener).onCreate(ArgumentMatchers.eq(new File("var/test/renamed_dir")));
  }

}