# ExtJS Editor #

[ExtJS](http://en.wikipedia.org/wiki/Ext_JS) is a JavaScript application framework

SAP MII plugin for Eclipse implements some functions to manipulate data using AJAX calls within ExtJS.
To achieve this used special files - `*.mej`.
There is graphical editor for this type of files in SAP MII plugin for Eclipse.
While only one type of objects in a palette can be used. Its Model. Model can be created manually and automatically from xMII url.

![http://mii-deployer.googlecode.com/svn/wiki/image/mej_editor.png](http://mii-deployer.googlecode.com/svn/wiki/image/mej_editor.png)

## Create The Model ##
To create the Model choose **Model** element in the palette. The Wizard window will appears.

Select one of the followed options
  * Load model from MII
  * Enter manually

![http://mii-deployer.googlecode.com/svn/wiki/image/model_1.png](http://mii-deployer.googlecode.com/svn/wiki/image/model_1.png)

If choose **Load from MII** options, window with additional parameters will appears.

![http://mii-deployer.googlecode.com/svn/wiki/image/model_2.png](http://mii-deployer.googlecode.com/svn/wiki/image/model_2.png)

Follow parameter should be filled:
  * URL - HTTP url to perform SAP MII Xacute Query through an Illuminator servlet
  * Username and password - Credentials to access this url
When you press **next** button, you will see last wizard window. In this window you can view and edit columns definition

![http://mii-deployer.googlecode.com/svn/wiki/image/model_3.png](http://mii-deployer.googlecode.com/svn/wiki/image/model_3.png)

Further you can edit attributes in the Eclipse **properties view**.

Next: [Generate ExtJS Code](Generate_Code.md)