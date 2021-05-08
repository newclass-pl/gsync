/*
 * This file is part of the gsyn
 *
 * (c) Newclass
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package pl.newclass.gsync.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.LinkedList;
import pl.newclass.gsync.IQueue;
import pl.newclass.gsync.IStorage;
import pl.newclass.gsync.SyncEvent;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
public class CacheQueue implements IQueue {

  private final LinkedList<SyncEvent> events = new LinkedList<>();
  private final IStorage storage;
  private final ObjectMapper objectMapper;

  public CacheQueue(IStorage storage) {
    this.storage = storage;
    objectMapper = new ObjectMapper();
    //fixme load queue from storage
  }

  @Override
  public synchronized void add(SyncEvent syncEvent) {
    var id = getId(syncEvent);
    try {
      storage.add("queue", id, serializeEvent(syncEvent));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    events.add(syncEvent);
    notify();
  }

  private String serializeEvent(SyncEvent syncEvent) {
    try {
      return objectMapper.writeValueAsString(syncEvent);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private String getId(SyncEvent syncEvent) {
    return syncEvent.getFile().getAbsolutePath();
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

  @Override
  public void flush(SyncEvent event) {
    var id = getId(event);
    try {
      storage.remove("queue", id);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
