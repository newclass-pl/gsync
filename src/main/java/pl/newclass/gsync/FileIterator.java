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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
public class FileIterator implements Iterator<String> {

  private final File file;
  private BufferedReader stream;
  private String line;

  public FileIterator(File file) {
    this.file = file;
  }

  @Override
  public boolean hasNext() {
    if (null == stream) {
      createStream();
    }

    try {
      line = stream.readLine();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    if (null != line) {
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
      stream = new BufferedReader(new FileReader(file));
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String next() {
    return line;
  }
}
