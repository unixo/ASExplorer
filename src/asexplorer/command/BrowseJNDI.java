package asexplorer.command;

import asexplorer.Config;
import gnu.getopt.LongOpt;
import java.util.ArrayList;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 *
 * @author unixo
 */
public class BrowseJNDI extends CommandBase
{
    private static final int indentLevel = 2;

    protected String root;

    protected int depth;

    public BrowseJNDI()
    {
        this.root = "";
        this.depth = -1;
    }

    @Override
    public String getCommandName()
    {
        return "browse";
    }

    @Override
    public String getDescription()
    {
        return "Enumerate configured JNDI resources";
    }

    @Override
    public boolean exec(InitialContext ctx)
    {
        System.out.println("/" + this.root);
        enumerate(ctx, this.root, 2);

        return true;
    }

    protected void enumerate(InitialContext ctx, String name, int indent)
    {
        try {
            NamingEnumeration e = ctx.list(name);
            recurse(ctx, e, name, indent);
        } catch (NamingException ex) {
        }
    }

    protected void recurse(InitialContext ctx, NamingEnumeration ne, String parentCtx, int indent) throws NamingException
    {
        while (ne.hasMoreElements()) {
            NameClassPair next = (NameClassPair) ne.nextElement();

            // indent entry
            for (int i = 0; i < indent; i++) {
                System.out.print(' ');
            }

            // Print entry name (and class name, if verbose output)
            if (Config.getInstance().isVerbose()) {
                System.out.println("|- "+next.getName() + " (" + next.getClassName() + ')');
            } else {
                System.out.println("|- "+next.getName());
            }

            // recurse
            enumerate(ctx,
                     (parentCtx.length() == 0) ? next.getName() : parentCtx + "/" + next.getName(),
                     indent+indentLevel);
        }
    }

    @Override
    public ArrayList<LongOpt> getParameters()
    {
        ArrayList<LongOpt> params = new ArrayList<LongOpt>();

        params.add(new LongOpt("root", LongOpt.REQUIRED_ARGUMENT, null, 100));
        params.add(new LongOpt("depth", LongOpt.REQUIRED_ARGUMENT, null, 101));

        return params;
    }

    @Override
    public boolean parseParameter(String param, String value)
    {
        if (param.equalsIgnoreCase("root")) {
            if (value == null) {
                asexplorer.ASExplorer.logger.error("'root' parameter needs a value");

                return false;
            }
            if (value.equals("/")) {
                this.root = "";
            } else {
                this.root = value;
            }
        } else if (param.equalsIgnoreCase("depth")) {
            if (value == null) {
                asexplorer.ASExplorer.logger.error("'depth' parameter needs a value");

                return false;
            }
        } else {
            return false;
        }

        return true;
    }
}
