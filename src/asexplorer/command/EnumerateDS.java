package asexplorer.command;

import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 *
 * @author unixo
 */
public class EnumerateDS implements CommandInterface
{

    @Override
    public String getCommandName()
    {
        return "enumDS";
    }

    @Override
    public String getDescription()
    {
        return "Enumerate configured data source";
    }

    @Override
    public boolean exec(InitialContext ctx)
    {
        String name = "/";

        try {
              NamingEnumeration e = ctx.list(name);
              while (e.hasMore()) {
                NameClassPair ncPair = (NameClassPair)e.next();
                System.out.print(ncPair.getName() + " (type ");
                System.out.println(ncPair.getClassName() + ")");
              }
        } catch (NamingException ne) {
          System.err.println("Couldn't list " + name);
        }

        return true;
    }
}
