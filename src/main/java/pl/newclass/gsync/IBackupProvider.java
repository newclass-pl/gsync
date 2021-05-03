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
import java.io.IOException;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
public interface IBackupProvider {

  void send(File file, String path) throws IOException;
}
