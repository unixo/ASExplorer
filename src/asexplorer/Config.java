package asexplorer;

import asexplorer.module.ModuleInterface;
import asexplorer.module.ModuleLocator;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;

/**
 *
 * @author unixo
 */
public class Config
{

    static final int DEFAULT_SQL_LIMIT = -1;
    static final String DEFAULT_HOST = "127.0.0.1:0";
    protected String server;
    protected Integer sqlLimit;
    protected String username;
    protected String password;
    protected String protocol;
    protected String command;
    protected boolean verbose;
    protected ModuleInterface selectedModule;

    public Config()
    {
        setDefaults();
    }

    private void setDefaults()
    {
        this.server = Config.DEFAULT_HOST;
        this.sqlLimit = Config.DEFAULT_SQL_LIMIT;
        this.verbose = false;
        this.protocol = null;
        this.selectedModule = null;
        this.command = null;
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

    public void setType(String type)
    {
        Class<ModuleInterface>[] modules = ModuleLocator.getModules();
        ArrayList<String> knownTypes = new ArrayList<String>();

        for (Class<ModuleInterface> aClassModule : modules) {
            ModuleInterface mi;

            try {
                mi = aClassModule.newInstance();
            } catch (Exception ex) {
                continue;
            }

            knownTypes.add(mi.getType());

            if (mi.getType().equalsIgnoreCase(type)) {
                this.selectedModule = mi;

                return;
            }
        }

        this.selectedModule = null;
        System.err.println("Server type not supported (allowed values: " + knownTypes + ')');
    }

    /*
     * Iterator<ApplicationServerInterface> itr = this.modules.iterator();
     *
     * while (itr.hasNext()) { ModuleInterface asi = itr.next();
     *
     * if (asi.getTypeCode().equalsIgnoreCase(type)) { this.selectedModule =
     * asi;
     *
     * return; } }
     *
     * System.err.println("Unkown server type"); this.selectedModule = null;
     *
     */

    /*
     * public ModuleInterface getSelectedModule() { return selectedModule; }
     */
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

    public InitialContext getBuildContext()
    {
        InitialContext ctx = this.selectedModule.buildInitialContext(this);

        return ctx;
    }

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

        if ((this.username != null && this.password == null)
                || (this.username == null && this.password != null)) {
            System.err.println("Incomplete credentials\n");

            return false;
        }

        return true;
    }
}
