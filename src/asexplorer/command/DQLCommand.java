package asexplorer.command;

import gnu.getopt.LongOpt;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.nocrala.tools.texttablefmt.Table;

/**
 * DQL (Data Query Language) refers to the SELECT, SHOW and HELP statements (queries)
 *
 * @author unixo
 */
public class DQLCommand extends CommandBase
{
    protected Integer limit = -1;
    protected Integer colSize = 255;
    protected String dql = null;
    protected String dqlFilename = null;
    protected String dsName = null;
    protected boolean isCSV = false;
    protected String csvChar = ";";

    @Override
    public String getCommandName()
    {
        return "dql";
    }

    @Override
    public String getDescription()
    {
        return "DQL instruction for SQL datasource";
    }
    
    @Override
    public String getHelp() {
        return "--dql-datasource str --dql-file filename | --dql str [--limit num] [--colsize num] [--csv str]";
    }

    @Override
    public ArrayList<LongOpt> getParameters()
    {
        ArrayList<LongOpt> params = new ArrayList<>();

        params.add(new LongOpt("dql-datasource", LongOpt.REQUIRED_ARGUMENT, null, 'd'));
        params.add(new LongOpt("limit", LongOpt.REQUIRED_ARGUMENT, null, 101));
        params.add(new LongOpt("dql", LongOpt.REQUIRED_ARGUMENT, null, 102));        
        params.add(new LongOpt("colsize", LongOpt.REQUIRED_ARGUMENT, null, 103));
        params.add(new LongOpt("csv", LongOpt.REQUIRED_ARGUMENT, null, 1004));
        params.add(new LongOpt("dql-file", LongOpt.REQUIRED_ARGUMENT, null, 105));

        return params;
    }

    @Override
    public boolean parseParameter(String param, String value)
    {
        boolean retValue = false;

        if (param.equalsIgnoreCase("dql-datasource")) {
            this.dsName = value;
            retValue = true;
        } else if (param.equalsIgnoreCase("limit")) {
            this.limit = Integer.parseInt(value);
            retValue = true;
        } else if (param.equalsIgnoreCase("dql")) {
            this.dql = value;
            retValue = true;
        } else if (param.equalsIgnoreCase("dql-file")) {
            this.dqlFilename = value;
            retValue = true;
        } else if (param.equalsIgnoreCase("colsize")) {
            this.colSize = Integer.parseInt(value);
            retValue = true;
        } else if (param.equalsIgnoreCase("csv")) {
            this.isCSV = true;
            retValue = true;
            if (value != null) {
                this.csvChar = value;
            }
        }

        return retValue;
    }

    @Override
    public void exec(InitialContext ctx)
    {
        String command = this.getDQL();
        if (command == null || this.dsName == null) {
            asexplorer.ASExplorer.logger.error("A datasource/sql needs to be specified.");
        } else {
            try {
                DataSource ds = (DataSource) ctx.lookup(this.dsName);
                Connection conn = ds.getConnection();
                Statement stmt = conn.createStatement();

                // limit result set size, if user specified an upper limit
                if (this.limit != -1) {
                    stmt.setMaxRows(this.limit);
                }

                ResultSet rs = stmt.executeQuery(command);

                // Print all columns
                ResultSetMetaData rsMetaData = rs.getMetaData();
                int cols = rsMetaData.getColumnCount();
                Table t = new Table(cols);
                for (int i=1; i<=cols; i++) {
                    if (this.isCSV) {
                        System.out.format("%s%s", rsMetaData.getColumnName(i), this.csvChar);
                    } else {
                        t.addCell(rsMetaData.getColumnName(i));
                    }
                }
                if (this.isCSV) {
                    System.out.println();
                }
                
                // Print all returned records
                while(rs.next()) {
                    for (int i=1; i<=cols; i++) {
                        String value = rs.getString(i);
                        if (value == null) {
                            value = "NULL";
                        }

                        if (this.isCSV) {
                            System.out.format("%."+this.colSize +"s%s",value, this.csvChar);
                        } else {
                            t.addCell(value);
                        }
                    }
                    if (this.isCSV) {
                        System.out.println();
                    }
                }
                if (!this.isCSV) {
                    System.out.println(t.render());
                }

                // Finally close the connection
                conn.close();
            } catch (NamingException | SQLException ex) {
                Logger.getLogger(DQLCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    protected String getDQL() {
        String result;
        
        if (this.dql == null) {
            try {
                result = new Scanner(new File(this.dqlFilename)).useDelimiter("\\Z").next();
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
