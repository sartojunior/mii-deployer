# SAP MII plugin Actions #

Action available for several type of resources
  * Folders (recirsive update and commit)
  * JSON and HTML Files
  * Java Script, images and other files

Action cannot be accessed using keystrokes. It can be performed only through the right click menu.

  * View action enabled only when only one resource has been selected. It opens browser window and tries to show selected file in it. The default browser is browser which configured in the Eclipse IDE as default
  * Update action enabled when one or more resources has been selected. It recursively load selected folder and files from SAP MII project and reloads its in the Eclipse workspace.
  * Commit action enabled when one or more resources has been selected. This action stores files and folders recirsively in the SAP MII project and after send special command to SAP MII server to refresh all updated resources.

Also, all of this actions can be performed from context menu in the editors.

![http://mii-deployer.googlecode.com/svn/wiki/image/actions.png](http://mii-deployer.googlecode.com/svn/wiki/image/actions.png)