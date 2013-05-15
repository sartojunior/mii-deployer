package org.asem.eclipse.mii.popup.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.asem.eclipse.mii.db.Config;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;


public abstract class AbstractAction implements IObjectActionDelegate {
    protected  Shell                     shell;
    protected final List<IResource>      selectedResources = new ArrayList<IResource>();
    protected final ExecutorService      execService = Executors.newFixedThreadPool(1);
    
    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        selectedResources.clear();

        Iterator<?> theSet = ((IStructuredSelection)selection).iterator ();
        while (theSet.hasNext ()) {
            Object obj = theSet.next ();
            if (obj instanceof IResource) {
                selectedResources.add ((IResource)obj);
            }
            else if (obj instanceof ICompilationUnit) {
                IResource fp = ((ICompilationUnit)obj).getResource();
                selectedResources.add (fp);
            }
            else if (obj instanceof IJavaElement) {
                IResource fp = ((IJavaElement)obj).getResource();
                selectedResources.add (fp);
            }
            else if (obj instanceof IFileEditorInput) {
                IResource fp = ((IFileEditorInput)obj).getFile();
                selectedResources.add (fp);
            }
        }
    }

    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        shell = targetPart.getSite().getShell();
    }

    public void run(IAction action) {
        execService.execute(new Runnable() {
            @Override
            public void run() {
                Config.activateConsole("MII");
                execute();
            }
        });
    }

    protected abstract void execute();
}
