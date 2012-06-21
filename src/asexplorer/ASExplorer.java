package asexplorer;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import java.util.ArrayList;

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
    ASServer as = ASExplorer.parseArguments(args);
  }

  /**
   * Parse command line arguments and return remote server configuration
   *
   * @param args
   * @return ASServer
   */
  public static ASServer parseArguments(String[] args)
  {
    ASServer config = new ASServer();
    int c;

    // Build allowed arguments list
    LongOpt[] longOptions = {
      new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'),
      new LongOpt("verbose", LongOpt.NO_ARGUMENT, null, 'v'),
      new LongOpt("server", LongOpt.REQUIRED_ARGUMENT, null, 's'),
      new LongOpt("port", LongOpt.REQUIRED_ARGUMENT, null, 'p'),
      new LongOpt("type", LongOpt.REQUIRED_ARGUMENT, null, 't'),
      new LongOpt("limit", LongOpt.REQUIRED_ARGUMENT, null, 'l'),
    };

    // Start parsing
    Getopt localGetopt = new Getopt("ASExplorer", args, "hvs:p:t:l:", longOptions);
    localGetopt.setOpterr(false);

    while ((c = localGetopt.getopt()) != -1) {
      switch (c) {
        case 'h':
          break;

        case 's':
          config.setServer(localGetopt.getOptarg());
          break;

        case 'p':
          config.setPort(localGetopt.getOptarg());
          break;

        case 't':
          config.setType(localGetopt.getOptarg());
          break;
      }

    }

    System.out.println(config);

    return config;
  }
}
