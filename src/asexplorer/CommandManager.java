package asexplorer;

import asexplorer.command.CommandBase;
import gnu.getopt.LongOpt;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import javax.naming.InitialContext;

/**
 *
 * @author unixo
 */
public class CommandManager
{
    protected HashMap<String, CommandBase> allCommands;

    private CommandManager()
    {
        allCommands = new HashMap<String, CommandBase>();
    }

    public static CommandManager getInstance()
    {
        return CommandManagerHolder.INSTANCE;
    }

    private static class CommandManagerHolder
    {
        private static final CommandManager INSTANCE = new CommandManager();
    }

    public void loadCommands()
    {
        try {
            Class[] classes = ClassFinder.getClassesInPackage("asexplorer.command");

            for (Class c : classes) {
                if (c.getSuperclass().equals(CommandBase.class)) {
                    CommandBase aCmd = (CommandBase) c.newInstance();
                    allCommands.put(aCmd.getCommandName(), aCmd);
                }
            }
        } catch (Exception ex) {
            ASExplorer.logger.error("Unable to load commands");
        }
    }

    public void displayCommandList()
    {
        TreeSet<String> keys = new TreeSet<String>(allCommands.keySet());

        for (String key : keys) {
            CommandBase value = allCommands.get(key);

            System.out.println(key+"\t\t"+value.getDescription());
        }
    }

    public List<LongOpt> getCommandParameters()
    {
        ArrayList<LongOpt> params = new ArrayList<LongOpt>();
        TreeSet<String> keys = new TreeSet<String>(allCommands.keySet());

        for (String key : keys) {
            CommandBase value = allCommands.get(key);

            params.addAll(value.getParameters());
        }

        return params;
    }

    public boolean parseParameter(String param, String value)
    {
        TreeSet<String> keys = new TreeSet<String>(allCommands.keySet());

        for (String key : keys) {
            CommandBase aCmd = allCommands.get(key);

            if (aCmd.parseParameter(param, value)) {
                return true;
            }
        }

        return false;
    }

    public void exec()
    {
        String aCmdName = Config.getInstance().getCommand();
        if (aCmdName == null || allCommands.keySet().contains(aCmdName) == false) {
            System.err.println("No command was specified\n");
        } else {
            CommandBase aCommand = allCommands.get(aCmdName);
            InitialContext ctx = Config.getInstance().getServerType().getInitialContext();
            aCommand.exec(ctx);
        }
    }
}
