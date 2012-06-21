package asexplorer.modules;

import asexplorer.ASConfig;
import javax.naming.InitialContext;

/**
 *
 * @author unixo
 */
public interface ApplicationServerInterface
{
  public String getName();

  public String getTypeCode();

  public String getDefaultProtocol();

  public Integer getDefaultPort();

  public InitialContext buildInitialContext(ASConfig config);
}
