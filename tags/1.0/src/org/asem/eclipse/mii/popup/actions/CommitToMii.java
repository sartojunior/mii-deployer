package org.asem.eclipse.mii.popup.actions;

import java.util.HashSet;
import java.util.Set;

import org.asem.eclipse.mii.db.Config;
import org.asem.eclipse.mii.db.DBConsts;
import org.asem.eclipse.mii.db.DBFiles;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;


public class CommitToMii extends AbstractAction {

    @Override
    protected void execute() {
        IProject prj = null;
        DBFiles files = null;
        Set<String> projects = new HashSet<String> ();
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

            files.saveFile(res);
            projects.add(Config.getValue(p, DBConsts.MII_PROJECT, ""));
        }

        /*
         * Update all root catalog of MII project
         */
        for (String miiPrj : projects) {
            files.updateFolder(miiPrj);
        }

        files.close();
    }
}
