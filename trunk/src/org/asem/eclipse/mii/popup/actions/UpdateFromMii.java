package org.asem.eclipse.mii.popup.actions;

import org.asem.eclipse.mii.db.DBFiles;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;



public class UpdateFromMii extends AbstractAction {

    @Override
    protected void execute() {
        IProject prj = null;
        DBFiles files = null;
        for (IResource res : selectedResources) {
            IProject p = res.getProject();
            if (p != prj) {
                files = new DBFiles(p);
                prj = p;
            }
            
            if (!files.checkPreferences()) {
                System.err.println("Check Preferences for project: " + prj.getName() + " failed!!! File: " + res.getName() + " skipped.");
                continue;
            }

            files.getFile(res);
            
            try {
                res.refreshLocal(IResource.DEPTH_INFINITE, null);
            }
            catch (CoreException e) {
            }
        }
    }
}
