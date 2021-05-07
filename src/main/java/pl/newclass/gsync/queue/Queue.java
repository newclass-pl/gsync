/*
 * This file is part of the gsyn
 *
 * (c) Newclass
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package pl.newclass.gsync.queue;

import java.util.LinkedList;
import pl.newclass.gsync.IQueue;
import pl.newclass.gsync.SyncEvent;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
public class Queue implements IQueue {

  private final LinkedList<SyncEvent> events = new LinkedList<>();

  @Override
  public synchronized void add(SyncEvent syncEvent) {
    events.add(syncEvent);
    notify();
  }

  @Override
  public synchronized SyncEvent poll() throws InterruptedException {
    while (true) {
      var event = events.poll();

      if (null != event) {
        return event;
      }
      wait();
    }
  }
}
