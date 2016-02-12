# Introduction #

ASExplorer is a tool useful to browse resources exposed by an application server, 
such as Weblogic or JBoss; especially useful if you're enforcing security.

## Main features ##
* built-in support for **Weblogic**, **Tomcat** and **JBoss**
* JNDI resources browsing
* automatic discovering of SQL *datasource*
* automatic loading of external libraries (*JARs*) to interact with AS
* r/w interaction with SQL datasources
* easy to extend

## Installation

To use ASExplorer you'll need:

* a JAVA compiler (javac)
* a Java based make tool (ant)
* specific application server libraries

First of all, download the package and build it:
```bash
$ cd <path-to-asexplorer>
$ ant
$ cd dist
$ mkdir -p lib/ext
```

Copy all *JAR*s you need to connect in this folder (lib/ext); in my testing environment, I use the following files:

* com.ibm.ws.ejb.thinclient_7.0.0.jar
* com.ibm.ws.orb_7.0.0.jar
* concurrent.jar
* cryptojFIPS.jar
* geronimo-connector-3.1.1.jar
* geronimo-transaction-3.1.1.jar
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
* openejb-client-4.6.0.1.jar
* openejb-core-3.0.1.jar
* openejb-cxf-rs-4.6.0.1.jar
* openejb-jee-4.6.0.1.jar
* openejb-jee-accessors-4.6.0.1.jar
* openejb-loader-4.6.0.1.jar
* openjpa-2.3.0-nonfinal-1540826.jar
* slf4j-api-1.7.5.jar
* slf4j-jdk14-1.7.5.jar
* swizzle-stream-1.6.2.jar
* weblogic.jar
* webserviceclient+ssl.jar
* webserviceclient.jar
* wlcipher.jar
* wlfullclient.jar
* wls-api.jar
* wlthint3client.jar
* wsdl4j-1.6.3.jar
* xbean-finder-shaded-3.15.jar
* xbean-naming-3.15.jar
* xbean-reflect-3.15.jar

## Examples ##

### Usage

Each execution of ASExplorer needs at least three parameters:

* --server *socket*: application server remote socket
* --type *type*: specify AS type, such as *jboss*, *weblogic*
* --command *name*: command name to use

### Commands list and help

```bash
java -jar ASExplorer.jar --commlist
+-------+----------------------------------------+
|Command|Description                             |
+-------+----------------------------------------+
|browse |Enumerate configured JNDI resources     |
|ddl    |DDL instruction for SQL datasource      |
|dql    |Execute a DQL command to a datasource   |
|enumds |Look for datasources                    |
|inspect|Inspect specified resource by reflection|
|invoke |Invoke a method of a remote EJB         |
+-------+----------------------------------------+
```

For each command you can also get the parameters list:

```bash
java -jar ASExplorer.jar --commhelp browse
Usage: java -jar ASExplorer [...] --command browse [--root str] [--depth num]
```

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

If you need to know the class type of each resource, specify the parameter "-v".


### Automatic datasources enumeration ###

```bash 
java -jar ASExplorer.jar -s 10.6.125.92:10001 -t weblogic --command enumds
[.foo.jdbc.MyDataSource]: UserName: TEST_CC
[.foo.jdbc.MySecondDataSource]: UserName: TEST_CC
Found 2 datasource(s)
.foo.jdbc.MyDataSource - Oracle Oracle9i Enterprise Edition Release 9.2.0.8.0 - 64bit
.foo.jdbc.MySecondDataSource - Oracle Oracle9i Enterprise Edition Release 9.2.0.8.0 - 64bit
```

### Interaction with SQL datasources

##DQL Operations
DQL (Data Query Language) refers to the SELECT, SHOW and HELP statements (queries): SELECT is the main DQL instruction.
Assuming that your application server is exporting an Oracle-based datasource named "foo.jdbc.MyDataSource":

```bash
java -jar ASExplorer.jar -s 127.0.0.1:10001 -t weblogic --dql-datasource foo.jdbc.MyDataSource --command dql --dql 'SELECT * FROM session_privs'
+--------------------+
|PRIVILEGE           |
+--------------------+
|CREATE SESSION      |
|ALTER SESSION       |
|CREATE TABLE        |
|CREATE CLUSTER      |
|CREATE SYNONYM      |
|CREATE VIEW         |
|CREATE SEQUENCE     |
|CREATE DATABASE LINK|
|CREATE PROCEDURE    |
|CREATE TRIGGER      |
|CREATE TYPE         |
|CREATE OPERATOR     |
|CREATE INDEXTYPE    |
+--------------------+
```

Parameters available:

* --ddl-datasource name: datasource name to interact (**required**)
* --ddl string: SELECT command to issue (**required** or use --ddl-file)
* --ddl-file filename: filename containing the DDL (use --ddl or --ddl-file)
* --limit num: limit the result set to the first 'num' records (*optional*)
* --csv string: format the output as CSV, using 'string' as field separator (*optional*)
* --colsize num: limit all columns size to 'num' (*optional*)
* --callable: mark the command as callable, e.g. stored procedure calling (*optional*)

```bash
echo 'SELECT * FROM session_privs' > dql.sql
java -jar ASExplorer.jar -s 127.0.0.1:10001 -t weblogic --ddl-datasource foo.jdbc.MyDataSource --command ddl --ddl-file dql.sql --limit 3
+--------------+
|PRIVILEGE     |
+--------------+
|CREATE SESSION|
|ALTER SESSION |
|CREATE TABLE  |
+--------------+
```

Example of stored procedure calling:

```bash
java -jar ASExplorer.jar -t weblogic -s 127.0.0.1:7001 --command dql --dql-datasource foo.jdbc.MyDataSource --dql "{ call SYS.UTLREADFILE('MY_DIR', 'passwd') }" --callable
root:x:0:0:root:/root:/bin/bash
bin:x:1:1:bin:/bin:/bin/bash
daemon:x:2:2:Daemon:/sbin:/bin/bash
lp:x:4:7:Printing daemon:/var/spool/lpd:/bin/bash
mail:x:8:12:Mailer daemon:/var/spool/clientmqueue:/bin/false
...
```

##DDL Operations
DDL (Data Definition Language) refers to the CREATE, ALTER and DROP statements: DDL allows to add / modify / delete the logical structures which contain the data or which allow users to access / mantain the data (databases, tables, keys, views...). DDL is about "metadata".
Assuming that your application server is exporting an Oracle-based datasource named "foo.jdbc.MyDataSource":

```bash
java -jar ASExplorer.jar -s 127.0.0.1:10001 -t weblogic --ddl-datasource foo.jdbc.MyDataSource --command ddl --ddl "update customer set key=1 where value=2"
Row affected: 1
```

As well as DQL, you can pass a file containing the instructions to be executed, such as a stored procedure creation.
```bash
echo 'DROP PROCEDURE foo' > ddl.sql
java -jar ASExplorer.jar -s 127.0.0.1:10001 -t weblogic --ddl-datasource foo.jdbc.MyDataSource --command ddl --ddl-file ddl.sql
Row affected: 1
```

### Inspect a class with reflection ###

```bash
java -jar ASExplorer.jar --server localhost:1099 --type jboss --command inspect --class jmx/invoker
```

If the remote object is an EJB, you can specify the parameter "--remote" to inspect the class connected to the it.

## TODO ##
* add GlassFish support
