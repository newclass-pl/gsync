/*
 * This file is part of the gsyn
 *
 * (c) Newclass
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package pl.newclass.gsync.storage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Objects;
import org.springframework.stereotype.Component;
import pl.newclass.gsync.FileIteratorFactory;
import pl.newclass.gsync.IStorage;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
public class FileStorage implements IStorage {

  private final File dir;

  public FileStorage(File dir) throws IOException {
    this.dir = dir;

    if (!dir.exists() && !dir.mkdirs()) {
      throw new IOException(String.format("Can not create dir %s.", dir));
    }
  }

  @Override
  public int size(String category) {
    String path = getPath(category);
    var file = new File(path);

    if (!file.exists()) {
      return 0;
    }

    return Objects.requireNonNull(file.list()).length;
  }

  private String getPath(String category) {
    return String.format("%s/%s", dir.getAbsolutePath(), category);
  }

  private String getPath(String category, String name) {
    return String.format("%s/%s/%d", dir.getAbsolutePath(), category, name.hashCode());
  }

  @Override
  public void add(String category, String name, Object value) throws IOException {
    var path = getPath(category);
    var categoryFile = new File(path);

    if (!categoryFile.exists() && !categoryFile.mkdirs()) {
      throw new IOException(String.format("Can not create dir %s.", path));
    }

    path = getPath(category, name);

    var option = StandardOpenOption.CREATE;

    Files.write(Paths.get(path),
        (value.toString() + System.lineSeparator()).getBytes(StandardCharsets.UTF_8), option);
  }

  @Override
  public boolean has(String category, String name) {
    var path = getPath(category, name);
    var file = new File(path);
    return file.exists();
  }

  @Override
  public Iterable<String> find(String category) {
    var path = getPath(category);
    var file = new File(path);

    if (file.exists()) {
      return new FileIteratorFactory(file);
    }

    return Collections.emptyList();
  }

  @Override
  public String get(String category, String name) throws IOException {
    var path = getPath(category, name);
    return Files.readString(Paths.get(path));
  }

  @Override
  public void remove(String category, String name) throws IOException {
    var path = getPath(category, name);
    Files.deleteIfExists(Paths.get(path));
  }
}
