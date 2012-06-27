package asexplorer.server;

import javax.naming.InitialContext;

/**
 *
 * @author unixo
 */
abstract public class ServerBase
{

    protected InitialContext context = null;

    abstract public String getName();

    abstract public String getType();

    abstract protected String getDefaultProtocol();

    abstract public InitialContext getInitialContext();

}
