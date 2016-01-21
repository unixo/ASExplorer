package asexplorer.server;

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
public class Tomcat extends ServerBase
{

    @Override
    public String getName()
    {
        return "Tomcat";
    }

    @Override
    public String getType()
    {
        return "tomcat";
    }

    @Override
    public String getDefaultProtocol()
    {
        return "ejbd";
    }

    @Override
    public InitialContext getInitialContext()
    {
        if (this.context == null) {
            Config config = Config.getInstance();

            try {
                Properties props = new Properties();


                // Set URI: add initial context factory type
                String protocol = (config.getProtocol() != null) ? config.getProtocol() : this.getDefaultProtocol();

                // Check if user requested http/https protocol
                if (protocol.equalsIgnoreCase("http") || protocol.equalsIgnoreCase("https")) {
                    props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.HttpNamingContextFactory");
                } else {
                    props.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.RemoteInitialContextFactory");
                }

                // Add credentials, if any
                if (config.getUsername() != null && config.getPassword() != null) {
                    props.setProperty(Context.SECURITY_PRINCIPAL, config.getUsername());
                    props.setProperty(Context.SECURITY_CREDENTIALS, config.getPassword());
                }


                //String url = String.format("%s://%s", protocol, config.getServer());
                String url = String.format("%s", config.getServer());
                System.out.println(">>>>>>>>>>>url: "+url);
                asexplorer.ASExplorer.logger.debug('('+ props.toString() + ')');

                props.put(Context.PROVIDER_URL, url);

                this.context = new InitialContext(props);
            } catch (CommunicationException cex) {
                System.err.println("Unable to connect to remote server");
            } catch (NamingException nex) {
                System.err.println("Unable to create initial context (missing libraries?)");
                System.out.println(nex);
            }
        }

        return this.context;
    }
}
