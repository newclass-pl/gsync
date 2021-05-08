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

  int size(String category) throws IOException;

  void add(String category, String name, Object value) throws IOException;

  boolean has(String category, String name);

  Iterable<? extends String> find(String category);

  String get(String category, String name) throws IOException;

  void remove(String category, String name) throws IOException;
}
