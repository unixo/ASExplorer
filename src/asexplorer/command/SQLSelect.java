/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package asexplorer.command;

import gnu.getopt.LongOpt;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.nocrala.tools.texttablefmt.Table;

/**
 *
 * @author unixo
 */
public class SQLSelect extends CommandBase
{
    protected Integer limit = -1;
    protected Integer colSize = 255;
    protected String sql = null;
    protected String dsName = null;
    protected boolean isCSV = false;
    protected String csvChar = ";";

    @Override
    public String getCommandName()
    {
        return "sql-select";
    }

    @Override
    public String getDescription()
    {
        return "SELECT instruction for SQL datasource";
    }
    
    @Override
    public String getHelp() {
        return "--datasource str --sql str [--limit num] [--colsize num] [--csv str]";
    }

    @Override
    public ArrayList<LongOpt> getParameters()
    {
        ArrayList<LongOpt> params = new ArrayList<LongOpt>();

        params.add(new LongOpt("datasource", LongOpt.REQUIRED_ARGUMENT, null, 'd'));
        params.add(new LongOpt("limit", LongOpt.REQUIRED_ARGUMENT, null, 101));
        params.add(new LongOpt("sql", LongOpt.REQUIRED_ARGUMENT, null, 102));
        params.add(new LongOpt("colsize", LongOpt.REQUIRED_ARGUMENT, null, 103));
        params.add(new LongOpt("csv", LongOpt.REQUIRED_ARGUMENT, null, 1004));

        return params;
    }

    @Override
    public boolean parseParameter(String param, String value)
    {
        boolean retValue = false;

        if (param.equalsIgnoreCase("datasource")) {
            this.dsName = value;
            retValue = true;
        } else if (param.equalsIgnoreCase("limit")) {
            this.limit = Integer.parseInt(value);
            retValue = true;
        } else if (param.equalsIgnoreCase("sql")) {
            this.sql = value;
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
        if (this.sql == null || this.dsName == null) {
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

                ResultSet rs = stmt.executeQuery(this.sql);

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
            } catch (NamingException ex) {
                Logger.getLogger(SQLSelect.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(SQLSelect.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
