package org.asem.eclipse.mii.popup.actions;

import java.net.MalformedURLException;
import java.net.URL;

import org.asem.eclipse.mii.db.Config;
import org.asem.eclipse.mii.db.DBConsts;
import org.asem.eclipse.mii.db.DBFiles;
import org.asem.eclipse.mii.model.abs.ShapeConstants;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.ui.browser.IWebBrowser;


public class ViewMii extends AbstractAction {

    @Override
    protected void execute() {
        if (selectedResources.size() != 1)
            return;
        
        IResource res = selectedResources.get(0); 
        IProject prj = res.getProject();

        String webURL = Config.getValue(prj, DBConsts.NW_WEB_URL, "");
        String miiProject = Config.getValue(prj, DBConsts.MII_PROJECT, "");

        IWebBrowser browser = Config.getBrowser(ShapeConstants.BROWSER_ID);
        
        String xmii = "/XMII/CM/" + miiProject;

        String fileName = res.getFullPath().toString();
        fileName = fileName.substring(0, fileName.lastIndexOf('.'));
        String ext = DBFiles.convertExt(res.getFileExtension());
        fileName = fileName + "." + ext;

        String relatedPath = fileName.replace(prj.getFullPath().toString(), "");
        relatedPath = relatedPath.replace("/WEB", xmii);

        URL url;
        try {
            url = new URL(webURL + relatedPath);
            browser.openURL(url);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        
    }
}
