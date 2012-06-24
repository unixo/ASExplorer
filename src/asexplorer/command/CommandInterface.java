package asexplorer.command;

import javax.naming.InitialContext;

/**
 *
 * @author unixo
 */
public interface CommandInterface
{
  public String getCommandName();

  public String getDescription();

  public boolean exec(InitialContext ctx);
}
