Introduction
------------

ASExplorer is a tool to interact with an application server, such as Weblogic
or JBoss; especially useful if you're enforcing security.

Main features
-------------
* built-in support for **Weblogic** and **JBoss**
* automatic discover SQL *datasource*
* automatic loading of external libraries (*JARs*) to interact with AS

TODO
----
* refactor shared code of ModuleLocator and CommandLocator
* log4j integration
* each ModuleInterface implementation must validate protocol selection
