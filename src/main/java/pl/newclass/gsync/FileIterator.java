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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
public class FileIterator implements Iterator<String> {

  private final File file;
  private DirectoryStream<Path> stream;
  private Iterator<Path> iterator;

  public FileIterator(File file) {
    this.file = file;
  }

  @Override
  public boolean hasNext() {
    if (null == stream) {
      createStream();
    }

    if (iterator.hasNext()) {
      return true;
    }

    closeStream();

    return false;
  }

  private void closeStream() {
    try {
      stream.close();
    } catch (IOException e) {
      //ignore
    }
  }

  private void createStream() {
    try {
      stream = Files.newDirectoryStream(Paths.get(file.getAbsolutePath()));
      iterator = stream.iterator();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String next() {
    try {
      return Files.readString(iterator.next());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
