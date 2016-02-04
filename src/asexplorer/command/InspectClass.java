package asexplorer.command;

import gnu.getopt.LongOpt;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author unixo
 */
public class InspectClass extends CommandBase
{
    protected String className = null;
    protected boolean remoteEJB = false;

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
        return "--class str [--remote]";
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
                Class aClass;
                if (this.remoteEJB) {
                    aClass = anObject.getClass();
                    Method aMethod = aClass.getMethod("getEJBMetaData");
                    Object remoteObject = aMethod.invoke(anObject, (Object) null);
                    aClass =  ((javax.ejb.EJBMetaData)remoteObject).getRemoteInterfaceClass();
                } else {
                    aClass = anObject.getClass();
                }
                inspectFields(aClass);
                inspectMethods(aClass);
            } catch (NamingException ex) {
                asexplorer.ASExplorer.logger.error("Unable to lookup specified resource.");
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(InspectClass.class.getName()).log(Level.SEVERE, null, ex);
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
        ArrayList<LongOpt> params = new ArrayList<>();

        params.add(new LongOpt("class", LongOpt.REQUIRED_ARGUMENT, null, 100));
        params.add(new LongOpt("remote", LongOpt.NO_ARGUMENT, null, 100));        

        return params;
    }

    @Override
    public boolean parseParameter(String param, String value)
    {
        if (param.equalsIgnoreCase("class")) {
            this.className = value;
            return true;
        } else if (param.equalsIgnoreCase("remote")) {
            this.remoteEJB = true;
            return true;
        }
        return false;
    }
}
