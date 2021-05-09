/*
 * This file is part of the gsyn
 *
 * (c) Newclass
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package pl.newclass.gsync;

import java.util.Map;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
public interface IBackupProviderFactory {

  String getName();

  void configure(ConfigBuilder configBuilder);

  IBackupProvider create(Map<String,Object> config);
}
