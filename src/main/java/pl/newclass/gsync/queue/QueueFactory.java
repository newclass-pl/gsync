/*
 * This file is part of the gsyn
 *
 * (c) Newclass
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package pl.newclass.gsync.queue;

import org.springframework.stereotype.Component;
import pl.newclass.gsync.IQueue;
import pl.newclass.gsync.IQueueFactory;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
@Component
public class QueueFactory implements IQueueFactory {

  @Override
  public IQueue create() {
    return new Queue();
  }
}
