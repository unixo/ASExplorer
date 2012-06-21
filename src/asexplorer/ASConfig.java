package asexplorer;

import asexplorer.modules.ApplicationServerInterface;
import asexplorer.modules.JBoss;
import asexplorer.modules.Weblogic;
import java.util.ArrayList;
import java.util.Iterator;
import javax.naming.InitialContext;

/**
 *
 * @author unixo
 */
public class ASConfig
{

  static final int DEFAULT_SQL_LIMIT = 20;

  protected String server;

  protected Integer sqlLimit;

  protected String username;

  protected String password;

  protected String protocol;

  protected String type;

  protected boolean verbose;

  protected boolean status;

  protected ArrayList<ApplicationServerInterface> modules;

  public ASConfig()
  {
    setDefaults();
  }

  private void setDefaults()
  {
    this.status = true;
    this.verbose = false;
    this.server = "127.0.0.1:0";
    this.protocol = null;
    this.type = null;
    this.sqlLimit = ASConfig.DEFAULT_SQL_LIMIT;

    // build and fill modules list
    this.modules = new ArrayList<ApplicationServerInterface>();
    this.modules.add( new JBoss() );
    this.modules.add( new Weblogic() );
  }

  @Override
  public String toString() {
    return "ASServer{" + "server=" + server +  ", type=" + type +
           ", protocol=" + protocol + ", sqlLimit=" + sqlLimit + ", username=" + username +
           ", password=" + password + ", verbose=" + verbose + ", status=" + status + '}';
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public String getServer()
  {
    return server;
  }

  public void setServer(String server)
  {
    this.server = server;
  }

  public Integer getSqlLimit()
  {
    return sqlLimit;
  }

  public void setSqlLimit(Integer sqlLimit)
  {
    this.sqlLimit = sqlLimit;
  }

  public void setSqlLimit(String sqlLimit)
  {
    try {
      this.sqlLimit = Integer.parseInt(sqlLimit);
    } catch (Exception e) {
      System.err.println("Invalid limit");
      this.status = false;
    }
  }

  public boolean getStatus()
  {
    return status;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  public boolean isVerbose()
  {
    return verbose;
  }

  public void setVerbose(boolean verbose)
  {
    this.verbose = verbose;
  }

  public String getProtocol()
  {
    return protocol;
  }

  public void setProtocol(String protocol)
  {
    this.protocol = protocol;
  }

  public InitialContext getBuildContext()
  {
    InitialContext ctx = null;

    if (this.type == null) {
      System.err.println("You need to specify application server type");
    } else {
      Iterator<ApplicationServerInterface> itr = this.modules.iterator();
      while (itr.hasNext()) {
        ApplicationServerInterface asi = itr.next();
        if (asi.getTypeCode().equalsIgnoreCase(this.type)) {
          ctx = asi.buildInitialContext(this);
        }
      }
    }

    return ctx;
  }

}
