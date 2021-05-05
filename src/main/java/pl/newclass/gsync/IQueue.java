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

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
public interface IQueue {

  void add(SyncEvent syncEvent);

  SyncEvent poll() throws InterruptedException;
}
