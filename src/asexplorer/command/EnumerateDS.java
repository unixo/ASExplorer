package asexplorer.command;

/**
 *
 * @author unixo
 */
public class EnumerateDS implements CommandInterface
{

  @Override
  public String getCommandName() {
    return "enumDS";
  }

  @Override
  public String getDescription() {
    return "Enumerate configured data source";
  }

}
