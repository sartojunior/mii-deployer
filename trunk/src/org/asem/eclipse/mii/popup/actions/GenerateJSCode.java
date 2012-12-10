package org.asem.eclipse.mii.popup.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.PropertyConfigurator;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.asem.eclipse.mii.model.abs.ShapeConstants;
import org.asem.eclipse.mii.model.abs.ShapesFactory;
import org.asem.eclipse.mii.model.editor.ShapesEditor;
import org.asem.eclipse.mii.model.shapes.ModelShape;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import resources.Resources4Generate;
import resources.Resources4Generate.RESOURCE_WITH_PATH;

public class GenerateJSCode implements IEditorActionDelegate {
    private IEditorPart editor;
    protected final ExecutorService execService = Executors.newFixedThreadPool(1);
    
    private VelocityEngine velocityEngine;
    private VelocityContext velocityContext;
    
    @Override
    public void run(IAction action) {
        execService.execute(new Runnable() {
            @Override
            public void run() {
                execute();
            }
        });
    }

    protected void execute() {
        if (editor instanceof ShapesEditor) {
            initContext ();
            PropertyConfigurator.configure(getClass().getResource ("log4j.properties"));
            prepareStructure();
            generateModels();
        }
    }

    private String getPath () {
        IFileEditorInput inp = (IFileEditorInput) editor.getEditorInput();
        String projectPath = inp.getFile().getProject().getLocation().toString();
        return projectPath + ShapeConstants.SCRIPT_PATH + "/"  + velocityContext.get("appName") + "/app";
    }

    private void streamCopy (InputStream in, OutputStream out) {
        int b;
        try {
            while ((b = in.read()) != -1) {
                out.write(b);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Adds common files provided by IBS
     */
    private void prepareStructure () {
        String path = getPath();
        String filePath = path + "/../";
        File fp = new File(filePath);
        if (!fp.exists())
            fp.mkdirs();

        for (RESOURCE_WITH_PATH res : Resources4Generate.JSResources) {
            try {
                InputStream in = Resources4Generate.class.getResourceAsStream(res.res + ".js-tmpl");
                if (in == null)
                    continue;

                String dir = filePath + res.path;
                File fpDir = new File(dir);
                if (!fpDir.exists())
                    fpDir.mkdirs();

                String file = filePath + res.getPath() + ".js";
                FileOutputStream out = new FileOutputStream(file);
                streamCopy (in, out);
                out.close();
                in.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void generateModels() {
        ShapesFactory fact = ShapesFactory.getInstance(ModelShape.class);
        String path = getPath();

        for (Object omodel : fact.getElements()) {
            ModelShape sm = (ModelShape)omodel;
            String filePath = path + "/model/";

            File fp = new File(filePath);
            if (!fp.exists())
                fp.mkdirs();

            velocityContext.put("model", sm);
            generateCode("velocity/Model.vm", filePath + sm.getPropertyValue(ShapeConstants.NAME) + "Store.js");
        }

        velocityContext.remove("model");
    }

    /**
     * function initialize velocity
     */
    protected void initVelocity ()
    {
        if (velocityEngine != null)
            return;

        try {
            velocityEngine = new VelocityEngine();
            velocityEngine.setProperty("resource.loader", "class");
            velocityEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            velocityEngine.setProperty("velocimacro.permissions.allow.inline.local.scope", true);
            velocityEngine.setProperty("class.resource.loader.path", "/ru/ibs/eclipse/mii/popup/actions");
            velocityEngine.init();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * function initialize context for current XML file and version
     * @param curVersion    - current version
     */
    public void initContext ()
    {
        initVelocity ();

        velocityContext = new VelocityContext();
        /*
        * Current datetime
         */
        velocityContext.put("date", (new Date()).toString());
        /*
        * Current username
         */
        velocityContext.put("author", System.getProperty("user.name"));
        /*
        * Application name
         */
        String appName = (String) ((ShapesEditor)editor).getDiagram().getPropertyValue(ShapeConstants.APPNAME);
        if (appName == null || appName.isEmpty())
            appName = "noname";

        velocityContext.put("appName", appName);
    }

    /**
     * function generateCode
     * @param templateName  - name of velocity template file, fle searchs in current package store
     * @param outFileName   - file name for output file, must be full path to a file
     * @return flag is operation successfully
     */
    public boolean generateCode (String templateName, String outFileName)
    {
        System.out.println(String.format("generateCode: template %s, outFile %s", templateName, outFileName));
        StringBuffer bf = new StringBuffer ("/");
        bf.append (getClass().getPackage ().getName ()).append (".");
        String templateUrl = bf.toString().replace (".", "/") + templateName;

        if (!velocityEngine.resourceExists (templateUrl))
            return false;

        try {
            Template tpl = velocityEngine.getTemplate (templateUrl);
            BufferedWriter writer = new BufferedWriter (new FileWriter(outFileName));
            tpl.merge (velocityContext, writer);
            writer.flush ();
            writer.close ();
        }
        catch (Exception e) {
            System.err.println(String.format("%s (%s)", "generateCode", templateUrl) + ": " +  e);
            return false;
        }

        return true;
    }
    
    @Override
    public void selectionChanged(IAction action, ISelection selection) {
    }

    @Override
    public void setActiveEditor(IAction action, IEditorPart editorPart) {
        editor = editorPart;
    }
}
