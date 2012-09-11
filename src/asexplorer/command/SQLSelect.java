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

/**
 *
 * @author unixo
 */
public class SQLSelect extends CommandBase
{
    protected Integer limit = -1;
    protected String sql = null;
    protected String dsName = null;

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
    public ArrayList<LongOpt> getParameters()
    {
        ArrayList<LongOpt> params = new ArrayList<LongOpt>();

        params.add(new LongOpt("datasource", LongOpt.REQUIRED_ARGUMENT, null, 100));
        params.add(new LongOpt("limit", LongOpt.REQUIRED_ARGUMENT, null, 101));
        params.add(new LongOpt("sql", LongOpt.REQUIRED_ARGUMENT, null, 102));

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
        } if (param.equalsIgnoreCase("sql")) {
            this.sql = value;
            retValue = true;
        }

        return retValue;
    }

    @Override
    public void exec(InitialContext ctx)
    {
        if (this.sql == null || this.dsName == null) {
            asexplorer.ASExplorer.logger.error("A datasource/sql needs to be specified.");
        } else {
            Statement stmt = null;
            ResultSet rs = null;
            DataSource ds = null;
            Connection conn = null;

            try {
                ds = (DataSource) ctx.lookup(this.dsName);
                conn = ds.getConnection();
                stmt = conn.createStatement();
                if (this.limit != -1)
                    stmt.setMaxRows(this.limit);
                rs = stmt.executeQuery(this.sql);

                // Print all columns
                ResultSetMetaData rsMetaData = rs.getMetaData();
                int cols = rsMetaData.getColumnCount();
                for (int i=1; i<=cols; i++) {
                    int type = rsMetaData.getColumnType(i);
                    System.out.print(rsMetaData.getColumnName(i)+" ("+type+") - ");
                }
                System.out.println();

                // Print all records returned
                while(rs.next()) {
                    for (int i=1; i<=cols; i++) {
                	System.out.print(rs.getString(i)+" - ");
                    }
                }
                System.out.println();

            } catch (NamingException ex) {
                Logger.getLogger(SQLSelect.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(SQLSelect.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
