package asexplorer;

import asexplorer.command.CommandInterface;
import asexplorer.command.CommandLocator;
import asexplorer.module.ModuleInterface;
import asexplorer.module.ModuleLocator;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;
import javax.naming.InitialContext;
import org.apache.log4j.Logger;

/**
 *
 * @author unixo
 */
public class Config
{

    static final int DEFAULT_SQL_LIMIT = -1;
    static final String DEFAULT_HOST = "127.0.0.1:0";

    private static Logger logger = Logger.getLogger(Config.class);

    /**
     * Remote application server to connect to (ip address:port)
     */
    protected String server;

    protected Integer sqlLimit;

    /**
     * Security option: username to authenticate on remote host
     */
    protected String username;

    /**
     * Security option: password to authenticate on remote host
     */
    protected String password;

    /**
     * Protocol for application server interactions
     */
    protected String protocol;

    /**
     * Selected command to be executed
     */
    protected String command;

    /**
     * Determine if output should be verbose
     */
    protected boolean verbose;

    /**
     * Interface to AS module identified by "type" parameter
     */
    protected ModuleInterface selectedModule;

    HashMap<String, CommandInterface> commands;

    /**
     * Class constructor
     */
    public Config()
    {
        setDefaults();
    }

    /**
     * Initialize class
     */
    private void setDefaults()
    {
        // default values
        this.server = Config.DEFAULT_HOST;
        this.sqlLimit = Config.DEFAULT_SQL_LIMIT;
        this.verbose = false;
        this.protocol = null;
        this.selectedModule = null;
        this.command = null;

        // load external JARs to resolve dependencies
        try {
            this.loadExternalArchives();
        } catch (Exception e) {
            logger.debug("Unable to load external archives");
        }

        // load all available commands
        this.commands = CommandLocator.getCommands();
    }

    /**
     * Dynamically loads additional JARs located in "lib/ext" directory
     *
     * @throws MalformedURLException
     */
    private void loadExternalArchives() throws MalformedURLException
    {
        File pluginDir = new File("lib/ext");
        File[] plugins = pluginDir.listFiles();
        LinkedList list = new LinkedList();

        logger.debug("plugins: "+plugins.length);

        if (plugins != null) {
            for (int i = 0; i < plugins.length; i++) {
                logger.debug("Found JAR: " + plugins[i].toURL());
                list.add(plugins[i].toURL());
            }
        }

        URL[] pluginURLs = (URL[]) list.toArray(new URL[list.size()]);
        Thread.currentThread().setContextClassLoader(new URLClassLoader(pluginURLs, Thread.currentThread().getContextClassLoader()));
    }

    @Override
    public String toString()
    {
        return "ASServer{" + "server=" + server + ", protocol=" + protocol
                + ", sqlLimit=" + sqlLimit + ", username=" + username + ", password=" + password
                + ", verbose=" + verbose + ", command=" + command + '}';
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

    public Integer getSqlLimit()
    {
        return sqlLimit;
    }

    public void setSqlLimit(Integer sqlLimit)
    {
        this.sqlLimit = sqlLimit;
    }

    public void setSqlLimit(String sqlLimit)
    {
        try {
            this.sqlLimit = Integer.parseInt(sqlLimit);
        } catch (Exception e) {
            System.err.println("Invalid limit");
            this.sqlLimit = -1;
        }
    }

    /**
     * Set type of application server to connect to. This method dinamically
     * searches a class which implements "ModuleInterface" and declares to be
     * able to manage specified type.
     *
     * @param type String representing type of application server
     */
    public void setType(String type)
    {
        // Get all active modules
        Class<ModuleInterface>[] modules = ModuleLocator.getModules();
        ArrayList<String> knownTypes = new ArrayList<String>();

        // Search for a module whose type is equal to given parameter
        for (Class<ModuleInterface> aClassModule : modules) {
            ModuleInterface mi;

            try {
                mi = aClassModule.newInstance();
            } catch (Exception ex) {
                continue;
            }

            knownTypes.add(mi.getType());

            // A proper module has been found: use it
            if (mi.getType().equalsIgnoreCase(type)) {
                this.selectedModule = mi;

                return;
            }
        }

        // If we're here, no modules were found, shows error
        this.selectedModule = null;
        System.err.println("Server type not supported (allowed values: " + knownTypes + ')');
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

    public boolean isVerbose()
    {
        return verbose;
    }

    public void setVerbose(boolean verbose)
    {
        this.verbose = verbose;
    }

    public String getProtocol()
    {
        return protocol;
    }

    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }

    public InitialContext buildContext()
    {
        InitialContext ctx = this.selectedModule.buildInitialContext(this);

        return ctx;
    }

    /**
     * Check if user-specified parameters are valid to operate
     *
     * @return True if configuration is valid
     */
    public boolean isValid()
    {
        if (this.server == null) {
            System.err.println("Invalid host\n");

            return false;
        }

        if (this.selectedModule == null) {
            return false;
        }

        if (this.command == null) {
            System.err.println("No command was specified\n");

            return false;
        }

        if ((this.username != null && this.password == null) || (this.username == null && this.password != null)) {
            System.err.println("Incomplete credentials\n");

            return false;
        }

        return true;
    }

    public void displayCommandList()
    {
        TreeSet<String> keys = new TreeSet<String>(commands.keySet());

        for (String key : keys) {
            CommandInterface value = commands.get(key);

            System.out.println(key+"\t\t"+value.getDescription());
        }

        System.exit(1);
    }

    public CommandInterface getSelectedCommand()
    {
        if (this.command == null) {
            return null;
        }

        CommandInterface ci = this.commands.get(this.command);

        return ci;
    }
}