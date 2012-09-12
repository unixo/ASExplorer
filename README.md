# Introduction #

ASExplorer is a tool useful to browse resources exposed by an application server, 
such as Weblogic or JBoss; especially useful if you're enforcing security.

## Main features ##
* built-in support for **Weblogic** and **JBoss**
* JNDI resources browsing
* automatic discover SQL *datasource*
* automatic loading of external libraries (*JARs*) to interact with AS
* interaction with SQL datasources

## Installation

To use ASExplorer you'll need:

* a JAVA compiler (javac)
* a Java based make tool (ant)
* specific application server libraries

First of all, download the package and build it:
```bash
$ cd <*path-to-asexplorer*>
$ ant
$ cd dist
$ mkdir -p lib/ext
```

Copy all *JAR*s you need to connect in this folder (lib/ext); in my testing environment, I use the following files:

* concurrent.jar
* cryptojFIPS.jar
* jboss-client.jar
* jboss-common-core.jar
* jboss-integration.jar
* jboss-jmx.jar
* jboss-logging-log4j.jar
* jboss-logging-spi.jar
* jboss-remoting.jar
* jboss-security-spi.jar
* jboss-serialization.jar
* jbossall-client.jar
* jbosscx-client.jar
* jbosssx-client.jar
* jnp-client.jar
* ojdbc14.jar
* webserviceclient+ssl.jar
* webserviceclient.jar
* wlcipher.jar
* wlfullclient.jar
* wls-api.jar
* wlthint3client.jar

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

### Interaction with SQL datasources

Assuming that the application server is exporting a datasource named "MySqlDS" and it contains a table named "user":

```bash
java -jar ASExplorer.jar -s 127.0.0.1:1099 -t jboss -c sql-select --sql "SELECT user,host FROM user" --datasource MySqlDS
User (1) - Host (1) - 
msandbox - % - root - localhost -
```

### Inspect a class with reflection ###

```bash
java -jar ASExplorer.jar --server localhost:1099 --type jboss --command inspect --class jmx/invoker
```

## TODO ##
* complete log4j integration
* add GlassFish support
