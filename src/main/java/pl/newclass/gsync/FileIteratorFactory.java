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
import java.util.Iterator;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
public class FileIteratorFactory implements Iterable<String> {

  private final File file;

  public FileIteratorFactory(File file) {
    this.file = file;
  }

  @Override
  public Iterator<String> iterator() {
    return new FileIterator(file);
  }
}
