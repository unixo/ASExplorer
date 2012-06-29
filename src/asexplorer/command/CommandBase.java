package asexplorer.command;

import gnu.getopt.LongOpt;
import java.util.ArrayList;
import javax.naming.InitialContext;

/**
 *
 * @author unixo
 */
abstract public class CommandBase
{

    public ArrayList<LongOpt> getParameters()
    {
        return null;
    }

    public boolean parseParameter(String param, String value)
    {
        return false;
    }

    abstract public String getCommandName();

    abstract public String getDescription();

    abstract public void exec(InitialContext ctx);
}
