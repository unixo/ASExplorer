package asexplorer.module;

import asexplorer.Config;
import java.util.Properties;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author unixo
 */
public class Weblogic implements ModuleInterface
{

    @Override
    public String getName()
    {
        return "Weblogic";
    }

    @Override
    public String getType()
    {
        return "weblogic";
    }

    @Override
    public String getDefaultProtocol()
    {
        return "t3";
    }

    @Override
    public InitialContext buildInitialContext(Config config)
    {
        InitialContext ctx = null;
        Properties props = null;

        try {
            props = new Properties();

            // Add initial context factory type
            props.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");

            // Add credentials, if any
            if (config.getUsername() != null && config.getPassword() != null) {
                props.setProperty(Context.SECURITY_PRINCIPAL, config.getUsername());
                props.setProperty(Context.SECURITY_CREDENTIALS, config.getPassword());
            }

            // Set URI
            String protocol = (config.getProtocol() != null) ? config.getProtocol() : this.getDefaultProtocol();
            String url = String.format("%s://%s", protocol, config.getServer());
            props.put(Context.PROVIDER_URL, url);

            ctx = new InitialContext(props);
        } catch (CommunicationException cex) {
            System.err.println("Unable to connect to remote server");
        } catch (NamingException nex) {
            System.err.println("Unable to create initial context (missing libraries?)");
            if (config.isVerbose()) {
                System.err.println('('+ props.toString() + ')');
            }
        }

        return ctx;
    }
}
