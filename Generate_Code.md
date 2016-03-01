# Generate ExtJS Code #

When **mej** file is open in an editor, you can call **generate code** action.
![http://mii-deployer.googlecode.com/svn/wiki/image/gen_code.png](http://mii-deployer.googlecode.com/svn/wiki/image/gen_code.png)

After this press F5 key (synchronize filesystem) and you will see generated files.

Folder structure:

![http://mii-deployer.googlecode.com/svn/wiki/image/DirTree.png](http://mii-deployer.googlecode.com/svn/wiki/image/DirTree.png)
  * appName - name of your application
  * model - folder contains all model and store classes
  * asem - utility classes

Model and Store classes for one XacuteQuery stored in one file

Sample:
```JavaScript

/**
* Test Model
*/
Ext.define('test.model.TestModel', {
extend: 'Ext.data.Model',
fields: [
// Columns definitions
{name: 'DESCRIPTION'},
...
]
});

/**
* Class define TestStore store object
*/
Ext.define('test.model.TestStore', {
extend: 'Ext.data.Store',
model: 'test.TestModel',
autoLoad: true,

requires: [
'asem.data.MiiReader'
],

proxy: {
type: 'ajax',
extraParams: {
'QueryTemplate': 'Test/Queries/xa_getResourceList'
'Content-Type': 'text/xml'
},
actionMethods: {
read   : 'POST'
},
api: {
read: '/XMII/Illuminator'
},
reader: {
type: 'mii',
record: 'Row'
}
}
});
```

This a reader class **asem.data.MiiReader** used to parse MII XML retrieved by XacuteQuery.

Also when use MiiReader column can be dynamically loaded from MII XML, but to force MiiReader to do this modelType field in model should be set to 'dynamic'.

Some additional functions defined in the Common.js file