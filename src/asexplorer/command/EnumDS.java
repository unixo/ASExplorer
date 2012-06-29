package asexplorer.command;

import javax.naming.Reference;
import java.util.ArrayList;
import java.util.Iterator;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 *
 * @author unixo
 */
public class EnumDS extends CommandBase
{
    ArrayList<String> datasources = null;

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
        datasources = new ArrayList<String>();
        enumerate(ctx, "");

        System.out.println("Found " + this.datasources.size() + " datasource(s)");
        if (this.datasources.size() > 0) {
            Iterator<String> it = this.datasources.iterator();
            while (it.hasNext()) {
                String ds = (String) it.next();
                System.out.println(ds);
            }
        }
    }

    protected void enumerate(InitialContext ctx, String name)
    {
        System.out.println("Trying to enumerate: "+name);
        try {

            NamingEnumeration ne = ctx.list(name);

            while (ne.hasMoreElements()) {
                NameClassPair next = (NameClassPair) ne.nextElement();

                String resName = next.getName();
                Object anObject = ctx.lookup(resName);

                analyzeResource(anObject, resName);

                enumerate(ctx, (name.length() == 0) ? next.getName() : name + "/" + next.getName());
            }

        } catch (Exception ex) {
            System.err.println("naming exception: "+ex.toString());
        }
    }

    protected void analyzeResource(Object anObject, String resName)
    {
        System.out.println("anObject.getClass: "+anObject.getClass());

        try {
            Reference aRef = (Reference) anObject;

            System.out.println("Class name: "+aRef.getClassName());
            if (aRef.getClassName().equals("javax.sql.DataSource")) {
                this.datasources.add(resName);
            }
        } catch (ClassCastException cce) {

        }
    }

}
