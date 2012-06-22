package asexplorer.module;

import asexplorer.Config;
import javax.naming.InitialContext;

/**
 *
 * @author unixo
 */
public interface ModuleInterface
{
  public String getName();

  public String getType();

  public String getDefaultProtocol();

  public InitialContext buildInitialContext(Config config);
}
