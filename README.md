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

```bash
java -jar ASExplorer.jar --server localhost:1099 --type jboss --command browse
|
+- UserTransactionSessionFactory
+- UUIDKeyGeneratorFactory
+- MySqlDS
+- SecureManagementView
|   +- remote-org.jboss.deployers.spi.management.ManagementView
|   +- remote
```

### Automatic datasources enumeration ###

```bash 
java -jar ASExplorer.jar --server localhost:1099 --type jboss --command enumds
Found 1 datasource(s)
MySqlDS - MySQL 5.5.13-log
```

### Inspect a class with reflection ###

```bash
java -jar ASExplorer.jar --server localhost:1099 --type jboss --command inspect --class jmx/invoker
```

## TODO ##
* complete log4j integration
* add GlassFish support
