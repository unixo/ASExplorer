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
 * DDL (Data Definition Language) refers to the CREATE, ALTER and DROP statements
 *
 * @author unixo
 */
public class DDLCommand extends CommandBase {

    protected String dsName = null;
    protected String ddl = null;
    protected String ddlFilename = null;
    
    @Override
    public String getCommandName() {
        return "ddl";
    }

    @Override
    public String getDescription() {
        return "Execute a DDL command to a datasource";
    }

    @Override
    public String getHelp() {
        return "--ddl-datasource string --ddl-file input | --ddl string";
    }

    @Override
    public ArrayList<LongOpt> getParameters()
    {
        ArrayList<LongOpt> params = new ArrayList<>();

        params.add(new LongOpt("ddl-datasource", LongOpt.REQUIRED_ARGUMENT, null, 'd'));
        params.add(new LongOpt("ddl-file", LongOpt.REQUIRED_ARGUMENT, null, 101));
        params.add(new LongOpt("ddl", LongOpt.REQUIRED_ARGUMENT, null, 102));

        return params;
    }
    
    @Override
    public boolean parseParameter(String param, String value)
    {
        boolean retValue = false;

        if (param.equalsIgnoreCase("ddl-datasource")) {
            this.dsName = value;
            retValue = true;
        } else if (param.equalsIgnoreCase("ddl-file")) {
            this.ddlFilename = value;
            retValue = true;
        } else if (param.equalsIgnoreCase("ddl")) {
            this.ddl = value;
            retValue = true;
        }

        return retValue;
    }
    
    @Override
    public void exec(InitialContext ctx) {
        String command = this.getDDL();
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
                Logger.getLogger(DQLCommand.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }
    
    protected String getDDL() {
        String result;
        
        if (this.ddl == null) {
            try {
                result = new Scanner(new File(this.ddlFilename)).useDelimiter("\\Z").next();
            } catch (FileNotFoundException ex) {
                asexplorer.ASExplorer.logger.error("Unable to open or read specified file");
                result = null;
            }
        } else {
            result = this.ddl;
        }
        
        return result;
    }
    
}
