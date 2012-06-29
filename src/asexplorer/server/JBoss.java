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
public class JBoss extends ServerBase
{

    @Override
    public String getName()
    {
        return "JBoss";
    }

    @Override
    public String getType()
    {
        return "jboss";
    }

    @Override
    public String getDefaultProtocol()
    {
        return "jnp";
    }

    @Override
    public InitialContext getInitialContext()
    {
        if (this.context == null) {
            Config config = Config.getInstance();

            try {
                Properties props = new Properties();

                // Add initial context factory type
                props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
                props.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");

                // Add credentials, if any
                if (config.getUsername() != null && config.getPassword() != null) {
                    props.setProperty(Context.SECURITY_PRINCIPAL, config.getUsername());
                    props.setProperty(Context.SECURITY_CREDENTIALS, config.getPassword());
                }

                // Set URI
                String protocol = (config.getProtocol() != null) ? config.getProtocol() : this.getDefaultProtocol();
                
                // Check if user requested http/https protocol
                if (protocol.substring(0,4).equalsIgnoreCase("http")) {
                    props.setProperty("java.naming.factory.initial","org.jboss.naming.HttpNamingContextFactory");
                }
                String url = String.format("%s://%s", protocol, config.getServer());

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
