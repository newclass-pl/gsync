/*
 * This file is part of the gsyn
 *
 * (c) Newclass
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package pl.newclass.gsync;

import java.io.IOException;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
public interface IStorage {

  int size(String name) throws IOException;

  void add(String name, Object value) throws IOException;

  void put(String absolutePath, Object value) throws IOException;

  boolean has(String name);

  Iterable<? extends String> find(String format);

  String get(String name) throws IOException;

  void remove(String name) throws IOException;

  void remove(String name, String line) throws IOException;
}
