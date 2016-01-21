package asexplorer.command;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Try to find data sources in JNDI global namespace
 *
 * @author unixo
 */
public class EnumDS extends CommandBase
{
    protected HashMap<String, String> versionSQL = null;    
    protected ArrayList<String> datasources = null;
    protected InitialContext context;
    protected InitialContext rootContext;

    public EnumDS()
    {
        datasources = new ArrayList<String>();
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
    public String getHelp() {
        return "";
    }
    
    @Override
    public void exec(InitialContext ctx)
    {
        this.rootContext = ctx;
        enumerate(ctx, "", "");
        
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

    protected void enumerate(InitialContext ctx, String name, String fullPath)
    {
        try {
            NamingEnumeration ne = ctx.list(name);            
            recurse(ctx, ne, name, fullPath);
        } catch (NamingException ex) {
        }
    }

    protected void recurse(InitialContext ctx, NamingEnumeration ne, String parentCtx, String fullPath) throws NamingException
    {
        if (ne == null) {
            asexplorer.ASExplorer.logger.error("'NamingEnumeration' has no elements");
            return;
        }

        while (ne.hasMoreElements()) {
            NameClassPair next = (NameClassPair) ne.nextElement();

            // Print entry name (and class name, if verbose output)
            String res = fullPath+'.'+next.getName();            
            this.discoverDsType(res);

            // recurse
            enumerate(ctx,
                      (parentCtx.length() == 0) ? next.getName() : parentCtx + "/" + next.getName(),
                      fullPath + '.' + next.getName());
        }
    }

    protected void discoverDsType(String aName)
    {
        Connection conn;
        ResultSet rs;
        Statement stmt;

        try {
            Object anObject = this.rootContext.lookup(aName);
            DataSource ds = (DataSource) anObject;
            conn = ds.getConnection();

            java.sql.DatabaseMetaData metaData = conn.getMetaData();
            System.out.format("[%s]: UserName: %s\n", aName, metaData.getUserName());

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
