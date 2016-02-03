package asexplorer.command;

import asexplorer.Config;
import gnu.getopt.LongOpt;
import java.util.ArrayList;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * Browse global JNDI namespace
 *
 * @author unixo
 */
public class BrowseJNDI extends CommandBase
{
    private static final int indentLevel = 2;
    private static final int UNLIMITED = -1;

    protected String root;

    protected int depth;

    private int currentDepth;

    public BrowseJNDI()
    {
        this.root = "";
        this.depth = UNLIMITED;
        this.currentDepth = 0;
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
    public String getHelp() {
        return "[--root str] [--depth num]";
    }
    
    @Override
    public void exec(InitialContext ctx)
    {
        this.currentDepth = 0;
        System.out.println("|" + this.root);
        enumerate(ctx, this.root, 0);
    }

    protected void enumerate(InitialContext ctx, String name, int indent)
    {
        try {
            NamingEnumeration ne = ctx.list(name);
            if (this.depth != UNLIMITED && this.currentDepth >= this.depth) {
                return;
            }
            this.currentDepth++;
            recurse(ctx, ne, name, indent);
        } catch (NamingException ex) {
        }
    }

    protected void recurse(InitialContext ctx, NamingEnumeration ne, String parentCtx, int indent) throws NamingException
    {
        if (ne == null) {
            asexplorer.ASExplorer.logger.error("'NamingEnumeration' has no elements");
            return;
        }

        while (ne.hasMoreElements()) {
            NameClassPair next = (NameClassPair) ne.nextElement();

            // indent entry
            if (indent > 0) {
                System.out.print("| ");
                for (int i = 0; i < indent; i++) {
                    System.out.print(' ');
                }
            }

            // Print entry name (and class name, if verbose output)
            if (Config.getInstance().isVerbose()) {                
                System.out.println("+- "+next.getName() + " (" + next.getClassName() + ')');
            } else {
                System.out.println("+- "+next.getName());                
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

            this.depth = Integer.parseInt(value);
        } else {
            return false;
        }

        return true;
    }
}
