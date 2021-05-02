/*
 * This file is part of the gsyn
 *
 * (c) Newclass
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package pl.newclass.gsync;

import java.io.FileNotFoundException;
import pl.newclass.gsync.file.IWatchListener;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
public interface IWatchService {

  void addDir(String path, IWatchListener watchListener) throws FileNotFoundException;
}
