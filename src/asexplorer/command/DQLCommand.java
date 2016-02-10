package asexplorer.command;

import gnu.getopt.LongOpt;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * Execute a DQL command to a given data source
 *
 * @author unixo
 */
public class DQLCommand extends CommandBase {

    protected String dsName = null;
    protected String dql = null;
    protected String filename = null;
    
    @Override
    public String getCommandName() {
        return "dql";
    }

    @Override
    public String getDescription() {
        return "Execute a DQL command to a datasource";
    }

    @Override
    public String getHelp() {
        return "--dql-datasource string --dql-file input | --dql string";
    }

    @Override
    public ArrayList<LongOpt> getParameters()
    {
        ArrayList<LongOpt> params = new ArrayList<>();

        params.add(new LongOpt("dql-datasource", LongOpt.REQUIRED_ARGUMENT, null, 'd'));
        params.add(new LongOpt("dql-file", LongOpt.REQUIRED_ARGUMENT, null, 101));
        params.add(new LongOpt("dql", LongOpt.REQUIRED_ARGUMENT, null, 102));

        return params;
    }
    
    @Override
    public boolean parseParameter(String param, String value)
    {
        boolean retValue = false;

        if (param.equalsIgnoreCase("dql-datasource")) {
            this.dsName = value;
            retValue = true;
        } else if (param.equalsIgnoreCase("dql-file")) {
            this.filename = value;
            retValue = true;
        } else if (param.equalsIgnoreCase("dql")) {
            this.dql = value;
            retValue = true;
        }

        return retValue;
    }
    
    @Override
    public void exec(InitialContext ctx) {
        String command = this.getDQL();
        if (command == null || this.dsName == null) {
            asexplorer.ASExplorer.logger.error("Incorrect parameters");
        } else {
            try {
                DataSource ds = (DataSource) ctx.lookup(this.dsName);
                Connection conn = ds.getConnection();
                Statement stmt = conn.createStatement();
                int ret = stmt.executeUpdate(command);
                System.out.println("Row affected: " + ret);
            } catch (Exception e) {
                Logger.getLogger(DDLCommand.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }
    
    protected String getDQL() {
        String result;
        
        if (this.dql == null) {
            try {
                result = new Scanner(new File(this.filename)).useDelimiter("\\Z").next();
            } catch (FileNotFoundException ex) {
                asexplorer.ASExplorer.logger.error("Unable to open or read specified file");
                result = null;
            }
        } else {
            result = this.dql;
        }
        
        return result;
    }
    
}
