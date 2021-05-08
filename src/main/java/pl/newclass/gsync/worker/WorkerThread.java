/*
 * This file is part of the gsyn
 *
 * (c) Newclass
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package pl.newclass.gsync.worker;

import java.io.IOException;
import pl.newclass.gsync.ActionEvent;
import pl.newclass.gsync.IBackupProvider;
import pl.newclass.gsync.IQueue;
import pl.newclass.gsync.SyncEvent;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
public class WorkerThread implements Runnable {

  private final IQueue queue;
  private final IBackupProvider backupProvider;

  public WorkerThread(IQueue queue, IBackupProvider backupProvider) {
    this.queue = queue;
    this.backupProvider = backupProvider;
  }

  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        round();
      } catch (InterruptedException e) {
        e.printStackTrace(); //fixme add log
      }
    }
  }

  private void round() throws InterruptedException {
    var event = queue.poll();

    try{
      if (event.getAction() == ActionEvent.DELETE) {
        onDelete(event);
        queue.flush(event);
        return;
      }

      onUpdate(event);
      queue.flush(event);
    }
    catch (IOException e){
      e.printStackTrace(); //fixme add log
    }
  }

  private void onUpdate(SyncEvent event) throws IOException {
    if (event.getLastModified() != event.getFile().lastModified()) {
      System.err
          .printf("%s changed last modified", event.getFile().getAbsolutePath()); //fixme add log
      return;
    }
    backupProvider.send(event.getFile(), event.getDestPath());

  }

  private void onDelete(SyncEvent event) {
    //fixme detect if exists file. When exists and lastTime file is different that event lastTime then ignore
    System.err.println("delete file " + event.getFile().getAbsolutePath());
    queue.flush(event);
  }
}
