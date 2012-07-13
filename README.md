# Introduction #

ASExplorer is a tool useful to browse resources exposed by an application server, 
such as Weblogic or JBoss; especially useful if you're enforcing security.

## Main features ##
* built-in support for **Weblogic** and **JBoss**
* JNDI resources browsing
* automatic discover SQL *datasource*
* automatic loading of external libraries (*JARs*) to interact with AS

## Examples ##

### Enumerate all JNDI resources exposed by application server ###

``java -jar ASExplorer.jar --server localhost:1099 --type jboss --command browse
|
+- UserTransactionSessionFactory
+- UUIDKeyGeneratorFactory
+- MySqlDS
+- SecureManagementView
|   +- remote-org.jboss.deployers.spi.management.ManagementView
|   +- remote
``

### Automatic datasources enumeration ###

``java -jar ASExplorer.jar --server localhost:1099 --type jboss --command enumds``

### Inspect a class with reflection ###

``java -jar ASExplorer.jar --server localhost:1099 --type jboss --command inspect --class jmx/invoker``

## TODO ##
* complete log4j integration
* add GlassFish support