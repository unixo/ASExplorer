package asexplorer;

import asexplorer.server.ServerBase;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedList;
import org.apache.log4j.Level;

/**
 *
 * @author unixo
 */
public class Config
{

    /**
     * Remote application server to connect to (IP address:port)
     */
    protected String server = "127.0.0.1:0";

    /**
     * Security option: username to authenticate on remote host
     */
    protected String username = null;

    /**
     * Security option: password to authenticate on remote host
     */
    protected String password = null;

    /**
     * Protocol for application server interactions
     */
    protected String protocol = null;

    /**
     * Selected command to be executed
     */
    protected String command = null;

    /**
     * If true, application logs will be more verbose
     */
    protected boolean verbose = false;

    /**
     * Interface to AS module identified by "type" parameter
     */
    protected ServerBase serverType = null;

    /**
     * Class constructor
     */
    private Config()
    {
        // load external JARs to resolve dependencies
        loadExternalArchives();
    }

    /**
     * Returns (unique) instance of configuration
     *
     * @return Singleton instance
     */
    public static Config getInstance()
    {
        return ConfigHolder.INSTANCE;
    }

    private static class ConfigHolder
    {
        private static final Config INSTANCE = new Config();
    }

    @Override
    public String toString()
    {
        return "ASServer{" + "server=" + server + ", protocol=" + protocol
                + ", username=" + username + ", password=" + password
                + ", command=" + command + '}';
    }

    /**
     * Parse command line arguments
     * @param args
     */
    public void parseArguments(String[] args)
    {
        // Build allowed arguments list
        ArrayList<LongOpt> knownParameters = new ArrayList<LongOpt>();

        knownParameters.add(new LongOpt("commhelp", LongOpt.REQUIRED_ARGUMENT, null, 'H'));
        knownParameters.add(new LongOpt("commlist", LongOpt.NO_ARGUMENT, null, 'C'));
        knownParameters.add(new LongOpt("command", LongOpt.REQUIRED_ARGUMENT, null, 'c'));
        knownParameters.add(new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'));
        knownParameters.add(new LongOpt("password", LongOpt.REQUIRED_ARGUMENT, null, 'p'));
        knownParameters.add(new LongOpt("protocol", LongOpt.REQUIRED_ARGUMENT, null, 'P'));
        knownParameters.add(new LongOpt("server", LongOpt.REQUIRED_ARGUMENT, null, 's'));
        knownParameters.add(new LongOpt("type", LongOpt.REQUIRED_ARGUMENT, null, 't'));
        knownParameters.add(new LongOpt("user", LongOpt.REQUIRED_ARGUMENT, null, 'u'));
        knownParameters.add(new LongOpt("verbose", LongOpt.NO_ARGUMENT, null, 'v'));

        // Get list of parameters parsed by commands
        knownParameters.addAll(CommandManager.getInstance().getCommandParameters());

        // Start parsing
        LongOpt[] longOpts = new LongOpt[knownParameters.size()];
        knownParameters.toArray(longOpts);
        Getopt localGetopt = new Getopt("ASExplorer", args, "hc:CP:p:s:t:u:v", longOpts);
        localGetopt.setOpterr(false);

        int c;
        while ((c = localGetopt.getopt()) != -1) {
            switch (c) {
                case 'C':
                    CommandManager.getInstance().displayCommandList();
                    System.exit(1);
                    break;

                case 'c':
                    setCommand(localGetopt.getOptarg());
                    break;

                case 'h':
                    ASExplorer.showUsage();
                    break;

                case 'H':                    
                    String aName = localGetopt.getOptarg();
                    CommandManager.getInstance().displayCommandHelp(aName);
                    System.exit(1);
                    break;
                    
                case 'p':
                    setPassword(localGetopt.getOptarg());
                    break;

                case 'P':
                    setProtocol(localGetopt.getOptarg());
                    break;

                case 's':
                    setServer(localGetopt.getOptarg());
                    break;

                case 't':
                    setType(localGetopt.getOptarg());
                    break;

                case 'u':
                    setUsername(localGetopt.getOptarg());
                    break;

                case 'v':
                    this.verbose = true;
                    ASExplorer.logger.setLevel((Level) Level.DEBUG);
                    break;

                default:
                    boolean success;
                    int index = localGetopt.getLongind();

                    if (index != -1) {
                        String aParam = longOpts[index].getName();
                        String aValue = localGetopt.getOptarg();
                        success = CommandManager.getInstance().parseParameter(aParam, aValue);
                    } else {
                        success = false;
                    }
                    if (success == false) {
                        System.err.println("Unmanaged parameter(s)\n");
                        ASExplorer.showUsage();
                    }
            }
        }

        if (this.isValid() == false) {
            ASExplorer.showUsage();
            System.exit(1);
        }
    }

    /**
     * Dynamically loads additional JARs located in "lib/ext" directory
     *
     * @throws MalformedURLException
     */
    private void loadExternalArchives()
    {
        File pluginDir = new File("lib/ext");
        LinkedList<URL> list = new LinkedList<URL>();
        File[] plugins = pluginDir.listFiles();

        if (plugins == null) {
            ASExplorer.logger.warn("Unable to load external archives (lib/ext directory not found)");

            return;
        }

        ASExplorer.logger.debug("plugins: " + plugins.length);
        
        try {
            for (File plugin : plugins) {
                ASExplorer.logger.debug("Found JAR: " + plugin.toURI().toURL());
                list.add(plugin.toURI().toURL());
            }
        } catch (MalformedURLException me) {
            ASExplorer.logger.debug("Unable to load external archives");
        }        

        URL[] pluginURLs = (URL[]) list.toArray(new URL[list.size()]);
        Thread.currentThread().setContextClassLoader(new URLClassLoader(pluginURLs, Thread.currentThread().getContextClassLoader()));
    }

    /**
     * Set type of application server to connect to. This method dynamically
     * searches a class which extends "ServerBase" and declares to be
     * able to manage specified type.
     *
     * @param type String representing type of application server
     */
    public void setType(String type)
    {
        Class[] classes = ClassFinder.getClassesInPackage("asexplorer.server");
        ArrayList<String> knownTypes = new ArrayList<>();

        for (Class aClass : classes) {
            if (aClass.getSuperclass().equals(ServerBase.class)) {
                try {
                    ServerBase aServer = (ServerBase) aClass.newInstance();
                    knownTypes.add(aServer.getType());

                    if (aServer.getType().equalsIgnoreCase(type)) {
                        this.serverType = aServer;

                        return;
                    }
                } catch (Exception ex) {
                    ASExplorer.logger.error("Server type error");
                }
            }
        }

        this.serverType = null;
        System.err.println("Server type not supported (allowed values: " + knownTypes + ')');
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getServer()
    {
        return server;
    }

    public void setServer(String server)
    {
        this.server = server;
    }

    public String getCommand()
    {
        return command;
    }

    public void setCommand(String command)
    {
        this.command = command;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getProtocol()
    {
        return protocol;
    }

    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }

    public ServerBase getServerType()
    {
        return serverType;
    }

    public boolean isVerbose()
    {
        return verbose;
    }

    /**
     * Check if user-specified parameters are valid to operate
     *
     * @return True if configuration is valid
     */
    protected boolean isValid()
    {
        if (this.server == null) {
            System.err.println("Invalid host\n");

            return false;
        }

        if (this.serverType == null) {
            return false;
        }

        if ((this.username != null && this.password == null) || (this.username == null && this.password != null)) {
            System.err.println("Incomplete credentials\n");

            return false;
        }

        return true;
    }
}
