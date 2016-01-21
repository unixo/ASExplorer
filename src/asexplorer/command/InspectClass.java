package asexplorer.command;

import gnu.getopt.LongOpt;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author unixo
 */
public class InspectClass extends CommandBase
{
    protected String className = null;

    @Override
    public String getCommandName()
    {
        return "inspect";
    }

    @Override
    public String getDescription()
    {
        return "Inspect specified resource by reflection";
    }
    
    @Override
    public String getHelp() {
        return "--class str";
    }

    @Override
    public void exec(InitialContext ctx)
    {
        // Sanity check
        if (this.className == null) {
            asexplorer.ASExplorer.logger.error("A class name needs to be specified.");
        } else {
            try {
                Object anObject = ctx.lookup(this.className);
                inspectFields(anObject.getClass());
                inspectMethods(anObject.getClass());
            } catch (NamingException ex) {
                asexplorer.ASExplorer.logger.error("Unable to lookup specified resource.");
            }
        }
    }

    protected void inspectFields(Class aClass)
    {
        Field[] fields = aClass.getFields();

        if (fields != null && fields.length > 0) {
            System.out.println("\nFIELDS\n-------");
            for (Field aField : fields) {
                System.out.println("  " + aField);
            }
        } else {
            System.out.println("No fields were found");
        }
    }

    protected void inspectMethods(Class aClass)
    {
        Method[] methods = aClass.getMethods();

        if (methods != null && methods.length > 0) {
            System.out.println("\nMETHODS\n-------");
            for (Method aMethod : methods) {
                System.out.println("  " + aMethod);
            }
        } else {
            System.out.println("No methods were found");
        }
    }

    @Override
    public ArrayList<LongOpt> getParameters()
    {
        ArrayList<LongOpt> params = new ArrayList<LongOpt>();

        params.add(new LongOpt("class", LongOpt.REQUIRED_ARGUMENT, null, 100));

        return params;
    }

    @Override
    public boolean parseParameter(String param, String value)
    {
        if (param.equalsIgnoreCase("class")) {
            this.className = value;

            return true;
        }
        return false;
    }
}
