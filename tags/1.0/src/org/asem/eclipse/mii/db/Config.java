package org.asem.eclipse.mii.db;

import java.io.PrintStream;

import org.asem.eclipse.mii.Activator;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.internal.browser.DefaultBrowserSupport;

public final class Config {
    public static String getValue (IResource res, String key, String defaultValue) {
        try {
            return res.getPersistentProperty(new QualifiedName("", key));
        }
        catch (CoreException e) {
            return defaultValue;
        }
    }
    
    public static void setValue (IResource res, String key, String value) {
        try {
            res.setPersistentProperty(new QualifiedName("", key), value);
        }
        catch (CoreException e) {
            e.printStackTrace();
        }
    }

	public static String getString (String key, String defaultValue) {
		String ret = Activator.getDefault().getPreferenceStore().getString(key);
		if (ret == null)
			return defaultValue;
		else
			return ret;
	}

	public static Boolean getBoolean (String key, Boolean defaultValue) {
		Boolean ret = Activator.getDefault().getPreferenceStore().getBoolean(key);
		if (ret == null)
			return defaultValue;
		else
			return ret;
	}

	public static int getInt (String key) {
		return Activator.getDefault().getPreferenceStore().getInt(key);
	}

	public static MessageConsole getConsole (String consoleName) {
		ConsolePlugin plugin = ConsolePlugin.getDefault ();
		IConsoleManager conMan = plugin.getConsoleManager ();
		
		IConsole[] consoles = conMan.getConsoles();
		for (IConsole console : consoles) {
			if (consoleName.equals(console.getName()))
				return (MessageConsole)console;
		}

		MessageConsole newConsole = new MessageConsole (consoleName, null);
		conMan.addConsoles (new IConsole[] { newConsole });
		return newConsole;
	}
	
	public static void activateConsole (String consoleName) {
	    MessageConsole console = getConsole (consoleName); 
	    console.activate ();
	    MessageConsoleStream out = console.newMessageStream ();
        System.setErr (new PrintStream(out));
        System.setOut (new PrintStream(out));
	}
	
	public static Shell getShell () {
	    IWorkbench wbench = Activator.getDefault().getWorkbench();
	    if (wbench != null) {
	        IWorkbenchWindow window = wbench.getActiveWorkbenchWindow();
	        if (window != null)
	            return window.getShell();
	        else {
	            IShellProvider prov = wbench.getModalDialogShellProvider ();
	            if (prov != null)
	                return prov.getShell();

	            return new Shell(wbench.getDisplay());
	        }
	    }

	    return new Shell();
	}

    public static IWebBrowser getBrowser (String browserId) {
	    try {
            return new DefaultBrowserSupport().createBrowser(browserId);
        }
        catch (PartInitException e) {
            e.printStackTrace();
        }
	    
	    return null;
	}
}
