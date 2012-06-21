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
    ASConfig as = ASExplorer.parseArguments(args);

    if (as.getStatus() == false) {
      ASExplorer.showUsage();
      System.exit(1);
    }

    InitialContext ctx = as.getBuildContext();
    System.out.println(ctx);
  }

  /**
   * Parse command line arguments and return remote server configuration
   *
   * @param args
   * @return ASServer
   */
  public static ASConfig parseArguments(String[] args)
  {
    ASConfig config = new ASConfig();
    int c;

    // Build allowed arguments list
    LongOpt[] longOptions = {
      new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'),
      new LongOpt("verbose", LongOpt.NO_ARGUMENT, null, 'v'),
      new LongOpt("server", LongOpt.REQUIRED_ARGUMENT, null, 's'),
      new LongOpt("protocol", LongOpt.REQUIRED_ARGUMENT, null, 'p'),
      new LongOpt("type", LongOpt.REQUIRED_ARGUMENT, null, 't'),
      new LongOpt("limit", LongOpt.REQUIRED_ARGUMENT, null, 'l'),
    };

    // Start parsing
    Getopt localGetopt = new Getopt("ASExplorer", args, "hvs:p:t:l:", longOptions);
    localGetopt.setOpterr(false);

    while ((c = localGetopt.getopt()) != -1) {
      switch (c) {
        case 'h':
            ASExplorer.showUsage();
          break;

        case 's':
          config.setServer(localGetopt.getOptarg());
          break;

        case 'p':
          config.setProtocol(localGetopt.getOptarg());
          break;

        case 't':
          config.setType(localGetopt.getOptarg());
          break;

        case 'l':
          config.setSqlLimit(localGetopt.getOptarg());
          break;

        case 'v':
          config.setVerbose(true);
          break;

        default:
          System.out.println(new StringBuilder().append("Invalid parameter ").append(c).toString());
      }
    }

    return config;
  }

  public static void showUsage()
  {
    System.err.println(
            "usage: ASExplorer --server host [OPTIONS]...\n\n"+
            "Context control:\n"+
            "  -h, --help             Show this message\n"+
            "  -s, --server socket    Specifies the socket to connect to\n"+
            "  -P, --protocol proto   Specifies the protocol to communicate with AS\n"+
            "  -t, --type astype      Set application server type\n"+
            "\nOutput control:\n"+
            "  -v, --verbose          Be verbose\n"+
            "  -l, --limit num        Set maxinum numeber of rows returned by a SELECT\n"
            );

    System.exit(0);
  }
}
