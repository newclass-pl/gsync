/*
 * This file is part of the gsyn
 *
 * (c) Newclass
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package pl.newclass.gsync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import org.springframework.stereotype.Component;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
@Component
public class FileStorage implements IStorage {

  private String dir;

  public FileStorage() throws IOException {
    dir = "var";

    var file = new File(dir);

    if (!file.exists() && !file.mkdirs()) {
      throw new IOException(String.format("Can not create dir %s.", dir));
    }
  }

  @Override
  public int size(String name) throws IOException {
    String path = getPath(name);
    var file = new File(path);

    if (!file.exists()) {
      return 0;
    }
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      int quantity = 0;
      while (br.readLine() != null) {
        ++quantity;
      }

      return quantity;
    }
  }

  private String getPath(String name) {
    return String.format("%s/%d", dir, name.hashCode());
  }

  @Override
  public void add(String name, Object value) throws IOException {
    var path = getPath(name);
    var option = StandardOpenOption.CREATE;
    if (has(name)) {
      option = StandardOpenOption.APPEND;
    }

    Files.write(Paths.get(path),
        (value.toString() + System.lineSeparator()).getBytes(StandardCharsets.UTF_8), option);
  }

  @Override
  public void put(String name, Object value) throws IOException {
    var path = getPath(name);
    Files.write(Paths.get(path),
        (value.toString()).getBytes(StandardCharsets.UTF_8),
        StandardOpenOption.CREATE);
  }

  @Override
  public boolean has(String name) {
    var path = getPath(name);
    var file = new File(path);
    return file.exists();
  }

  @Override
  public Iterable<String> find(String name) {
    var path = getPath(name);
    var file = new File(path);

    if (file.exists()) {
      return new FileIteratorFactory(file);
    }

    return Collections.emptyList();
  }

  @Override
  public String get(String name) throws IOException {
    var path = getPath(name);
    return Files.readString(Paths.get(path));
  }

  @Override
  public void remove(String name) throws IOException {
    var path = getPath(name);
    Files.deleteIfExists(Paths.get(path));
  }

  @Override
  public void remove(String name, String line) throws IOException {
    var path = getPath(name);
    File file = new File(path);
    File temp = new File(String.format("%s.tmp", path));
    PrintWriter out = new PrintWriter(new FileWriter(temp));
    Files.lines(file.toPath())
        .filter(record -> !record.equals(line))
        .forEach(out::println);
    out.flush();
    out.close();
    if (!temp.renameTo(file)) {
      throw new IOException("Can not flush changes.");
    }
  }
}
