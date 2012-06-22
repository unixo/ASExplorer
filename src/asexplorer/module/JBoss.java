package asexplorer.module;

import asexplorer.Config;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author unixo
 */
public class JBoss implements ModuleInterface
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
  public InitialContext buildInitialContext(Config config)
  {
    InitialContext ctx = null;
    Properties props = null;

    try {
      props = new Properties();

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
      String url = String.format("%s://%s", protocol, config.getServer());
      props.put(Context.PROVIDER_URL, url);

      ctx = new InitialContext(props);
    } catch (NamingException ex) {
      Logger.getLogger(Weblogic.class.getName()).log(Level.SEVERE, null, ex);
    }

    return ctx;
  }
}
