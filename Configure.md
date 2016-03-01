## Configure Eclipse Project ##

Right click mouse on your project in Project Explorer view and choose Properties menu item. Select **SAP MII Configuration** page.

All fields are required
  * Jdbc Driver - Here you can select or type JDBC driver class name. The default is the Oracle driver. To use drivers you need to install appropriate driver into Eclipse IDE
  * URL - This is JDBC url to database with [SAP NetWeaver](http://en.wikipedia.org/wiki/SAP_NetWeaver) tables
  * User and Password - Credentials to access to the [SAP NetWeaver](http://en.wikipedia.org/wiki/SAP_NetWeaver) database
  * Test Query - Query will be used to determine connection status when plugin update or commit files. The default is a test query for the Oracle database
  * WEB Url - HTTP url to access to [SAP NetWeaver](http://en.wikipedia.org/wiki/SAP_NetWeaver)
  * NW user, NW Password - Credentials to access to the [SAP NetWeaver](http://en.wikipedia.org/wiki/SAP_NetWeaver)through the HTTP protocol. This user must have privileges to work with SAP MII
  * MII Project - name of xMII project as you see in in SAP MII Workbench

Further you should create a WEB folder in project's source

See also: [Using actions](actions.md), [ExtJS Editor](editor.md)

![http://mii-deployer.googlecode.com/svn/wiki/image/ProjectProperty.png](http://mii-deployer.googlecode.com/svn/wiki/image/ProjectProperty.png)