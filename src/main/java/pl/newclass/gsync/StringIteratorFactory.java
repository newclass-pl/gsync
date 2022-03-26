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
import java.util.List;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
public class StringIteratorFactory implements Iterable<String> {

  private final List<String> list;

  public StringIteratorFactory(List<String> list) {
    this.list = list;
  }

  @Override
  public Iterator<String> iterator() {
    return list.iterator();
  }
}
