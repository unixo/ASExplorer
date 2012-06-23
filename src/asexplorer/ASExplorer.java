package asexplorer;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import javax.naming.InitialContext;

/**
 *
 * @author unixo
 */
public class ASExplorer
{

    /**
     * Main class
     *
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        Config as = ASExplorer.parseArguments(args);

        if (as.isValid() == false) {
            ASExplorer.showUsage();
            System.exit(1);
        }

        InitialContext ctx = as.getBuildContext();
        if (ctx == null) {
            System.exit(1);
        }
    }

    /**
     * Parse command line arguments and return remote server configuration
     *
     * @param args
     * @return ASServer
     */
    public static Config parseArguments(String[] args)
    {
        Config config = new Config();
        int c;

        // Build allowed arguments list
        LongOpt[] longOptions = {
            new LongOpt("command", LongOpt.REQUIRED_ARGUMENT, null, 'c'),
            new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'),
            new LongOpt("limit", LongOpt.REQUIRED_ARGUMENT, null, 'l'),
            new LongOpt("password", LongOpt.REQUIRED_ARGUMENT, null, 'p'),
            new LongOpt("protocol", LongOpt.REQUIRED_ARGUMENT, null, 'P'),
            new LongOpt("server", LongOpt.REQUIRED_ARGUMENT, null, 's'),
            new LongOpt("type", LongOpt.REQUIRED_ARGUMENT, null, 't'),
            new LongOpt("user", LongOpt.REQUIRED_ARGUMENT, null, 'u'),
            new LongOpt("verbose", LongOpt.NO_ARGUMENT, null, 'v'),};

        // Start parsing
        Getopt localGetopt = new Getopt("ASExplorer", args, "hl:p:s:t:v", longOptions);
        localGetopt.setOpterr(false);

        while ((c = localGetopt.getopt()) != -1) {
            switch (c) {
                case 'c':
                    config.setCommand(localGetopt.getOptarg());
                    break;

                case 'h':
                    ASExplorer.showUsage();
                    break;

                case 'l':
                    config.setSqlLimit(localGetopt.getOptarg());
                    break;

                case 'p':
                    config.setPassword(localGetopt.getOptarg());
                    break;

                case 'P':
                    config.setProtocol(localGetopt.getOptarg());
                    break;

                case 's':
                    config.setServer(localGetopt.getOptarg());
                    break;

                case 't':
                    config.setType(localGetopt.getOptarg());
                    break;

                case 'u':
                    config.setUsername(localGetopt.getOptarg());
                    break;

                case 'v':
                    config.setVerbose(true);
                    break;

                default:
                    System.err.println("Invalid parameter(s)\n");
                    ASExplorer.showUsage();
            }
        }

        return config;
    }

    public static void showUsage()
    {
        System.err.println(
                "usage: ASExplorer --server host --command cmd [OPTIONS]...\n\n"
                + "Context control:\n"
                + "  -h, --help             Show this message\n"
                + "  -s, --server socket    Specifies the socket to connect to\n"
                + "  -P, --protocol proto   Specifies the protocol to communicate with AS\n"
                + "  -t, --type as-type     Set application server type\n"
                + "\nCommand list:\n"
                + "  --command cmd          Specify which operation will be issued\n"
                + "\nOutput control:\n"
                + "  -v, --verbose          Be verbose\n"
                + "  -l, --limit num        Set maxinum numeber of rows returned by a SELECT\n");

        System.exit(0);
    }
}
