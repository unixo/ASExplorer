/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package asexplorer;

import javax.media.jai.Histogram;

/**
 *
 * @author unixo
 */
public class ASServer {

  static final int DEFAULT_SQL_LIMIT = 20;

  public enum ASType { UNKNOWN, WEBLOGIC, JBOSS }

  protected String server;

  protected Integer port;

  protected ASType type;

  protected Integer sqlLimit;

  protected String username;

  protected String password;

  protected boolean verbose;

  protected boolean status;

  public void ASServer()
  {
    this.setDefaults();
  }

  protected void setDefaults()
  {
    this.status = false;
    this.verbose = false;
    this.type = ASType.UNKNOWN;
    this.server = "127.0.0.1";
    this.port = 7001;
    this.sqlLimit = ASServer.DEFAULT_SQL_LIMIT;
  }

  @Override
  public String toString() {
    return "ASServer{" + "server=" + server + ", port=" + port + ", type=" + type +
           ", sqlLimit=" + sqlLimit + ", username=" + username + ", password=" + password +
           ", verbose=" + verbose + ", status=" + status + '}';
  }


  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public Integer getPort()
  {
    return port;
  }

  public void setPort(Integer port)
  {
    this.port = port;
  }

  public void setPort(String port)
  {
    try {
      this.port = Integer.parseInt(port);
    } catch (Exception e) {
      System.err.println("Invalid port number");
      this.status = false;
    }
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

  public boolean isStatus()
  {
    return status;
  }

  public void setStatus(boolean status)
  {
    this.status = status;
  }

  public ASType getType()
  {
    return type;
  }

  public void setType(ASType type)
  {
    this.type = type;
  }

  public void setType(String type)
  {
    if (type.equalsIgnoreCase("weblogic")) {
      this.type = ASType.WEBLOGIC;
    } else if (type.equalsIgnoreCase("jboss")) {
      this.type = ASType.JBOSS;
    } else {
      System.err.println("Allowed types: weblogic, jboss");
      this.status = false;
    }
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

}
