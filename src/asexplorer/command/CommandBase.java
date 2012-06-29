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

    /**
     * Returns the list of command-line arguments supported by the module
     *
     * @return Array of long parameter(s)
     */
    public ArrayList<LongOpt> getParameters()
    {
        return null;
    }

    /**
     * This method is invoked by Config.parseArguments whenever a unrecognized
     * parameter is found: in this case, all modules are asked to handle the
     * parameter.
     *
     * @param param The parameter name to be analyzed
     * @param value The parameter value
     * @return True if parameter was handled by the module
     * @see Config
     */
    public boolean parseParameter(String param, String value)
    {
        return false;
    }

    /**
     * Returns the command name representing this module: user can specify this
     * command with --command <name> parameter
     *
     * @return The command name
     */
    abstract public String getCommandName();

    /**
     * Returns a brief description of the module to display when help is requested
     *
     * @return A string description
     */
    abstract public String getDescription();

    /**
     * Once connection was established with application server, main asks the
     * module to execute its code
     *
     * @param ctx The initial context representing connection
     */
    abstract public void exec(InitialContext ctx);
}
