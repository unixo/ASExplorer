package asexplorer;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Main class
 *
 * @author unixo
 */
public class ASExplorer
{
    public static Logger logger = Logger.getLogger(Config.class);

    /**
     * Main class
     *
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        // Init log4j
        BasicConfigurator.configure();
        ASExplorer.logger.setLevel((Level) Level.INFO);

        // Load available commands
        CommandManager.getInstance().loadCommands();

        // Parse command line arguments
        Config.getInstance().parseArguments(args);

        // Execute requested command
        CommandManager.getInstance().exec();
    }


    public static void showUsage()
    {
        System.err.println(
                "usage: ASExplorer --server host --command cmd [OPTIONS]...\n\n" +
                "Context control:\n" +
                "  -h, --help             Show this message\n" +
                "  -s, --server socket    Specifies the socket to connect to\n" +
                "  -P, --protocol proto   Specifies the protocol to communicate with AS\n" +
                "  -u, --user username    Username (principal)\n"+
                "  -p, --password pwd     Password (credential)\n"+
                "  -t, --type as-type     Set application server type\n" +
                "\nCommand list:\n" +
                "  --command cmd          Specify which operation will be issued\n" +
                "  --commlist             Display all available commands and exit\n" +
                "  --commhelp cmd         Show help of selected command and exit\n" +
                "\nOutput control:\n" +
                "  -v, --verbose          Be verbose\n");

        System.exit(0);
    }
}
