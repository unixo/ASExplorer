package asexplorer.command;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Try to find datasources in JNDI global namespace
 *
 * @author unixo
 */
public class EnumDS extends CommandBase
{

    //ArrayList<String> versionSQL = null;
    protected HashMap<String, String> versionSQL = null;
    protected ArrayList<String> datasources = null;
    protected InitialContext context;

    public EnumDS()
    {
        datasources = new ArrayList<String>();

        /*
        this.versionSQL = new ArrayList<String>();
        this.versionSQL.add("SELECT @@version");
        this.versionSQL.add("SELECT * FROM v$version");
        */

        versionSQL = new HashMap<String, String>();
        versionSQL.put("MySQL", "SELECT @@version");
        versionSQL.put("Oracle", "SELECT * FROM v$version");
    }

    @Override
    public String getCommandName()
    {
        return "enumds";
    }

    @Override
    public String getDescription()
    {
        return "Look for datasources";
    }

    @Override
    public void exec(InitialContext ctx)
    {
        this.datasources.clear();
        this.context = ctx;

        // Browse JNDI tree looking for datasources
        enumerate("");

        // Print enumeration results, if any
        System.out.println("Found " + this.datasources.size() + " datasource(s)");
        if (this.datasources.size() > 0) {
            Iterator<String> it = this.datasources.iterator();

            while (it.hasNext()) {
                String ds = (String) it.next();
                System.out.println(ds);
            }
        }
    }

    protected void enumerate(String name)
    {
        NamingEnumeration ne = null;

        try {
            ne = this.context.list(name);
        } catch (NamingException ex) {
        }

        while (ne.hasMoreElements()) {
            try {
                NameClassPair next = (NameClassPair) ne.nextElement();

                String resName = next.getName();
                discoverDsType(resName);

                // recurse on children
                enumerate((name.length() == 0) ? next.getName() : name + "/" + next.getName());
            } catch (Exception ex) {
            }
        }
    }

    protected void discoverDsType(String aName)
    {
        Connection conn;
        ResultSet rs;
        Statement stmt;

        try {
            Object anObject = this.context.lookup(aName);
            DataSource ds = (DataSource) anObject;
            conn = ds.getConnection();
            stmt = conn.createStatement();
        } catch (Exception ex) {
            return;
        }

        try {
            TreeSet<String> keys = new TreeSet<String>(versionSQL.keySet());

            for (String key : keys) {
                String aSql = versionSQL.get(key);

                try {
                    rs = stmt.executeQuery(aSql);
                    StringBuilder sbuff = new StringBuilder();

                    while (rs.next()) {
                        sbuff.append(rs.getString(1));
                    }
                    this.datasources.add(aName + " - " + key + " " + sbuff.toString());
                } catch (SQLException sex) {
                }
            }
        } catch (Exception ex) {
        }

    }
}
