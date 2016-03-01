# SAP MII plugin for Eclipse #

Project short presentation: http://mes-solutions.org/?page_id=83

[Installation](Installation.md)

[Configure SAP MII plugin](Configure.md)

Plugin allow to edit SAP MII WEB content using Eclipse IDE.

## Description ##
Plugin can be used with SAP MII (xMII) 12.1 and 12.2 version.
The plugin use JDBC to access xMII files stored in database. There are several actions implemented in the Eclipse IDE. To configure plugin, JDBC drivers used to access SAP MII database, should be configured  within Eclipse IDE.

  * Update - Action used to update files from xMII project's WEB folder. It supports recursive directory structure update. All local resources will be updated from xMII project

  * Commit - Action used to store files into xMII project's WEB folder. As well as the Update action it also supported recursion

  * View - Action used to view selected in Eclipse IDE file in browser.

Plugin also converts file extensition from IRPT to HTML or JSON depends on file content.

### Extension for ExtJS 4.x JavaScript library ###
Plugin provide functionality for generate ExtJS JavaScript code for models and stores. Parser for SAP MII XML format and utility class to call MII Xacute Queries from ExtJS also provided