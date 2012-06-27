Introduction
------------

ASExplorer is a tool to interact with an application server, such as Weblogic
or JBoss; especially useful if you're enforcing security.

Main features
-------------
* built-in support for **Weblogic** and **JBoss**
* JNDI resources browsing
* automatic discover SQL *datasource*
* automatic loading of external libraries (*JARs*) to interact with AS

Examples
--------

``java -jar ASExplorer.jar --server localhost:1099 --type jboss --command browse --verbose
/
  |- UserTransactionSessionFactory ($Proxy218)
  |- UUIDKeyGeneratorFactory (org.jboss.ejb.plugins.keygenerator.uuid.UUIDKeyGeneratorFactory)
  |- SecureManagementView (org.jnp.interfaces.NamingContext)
  [...]
``

TODO
----
* complete log4j integration
