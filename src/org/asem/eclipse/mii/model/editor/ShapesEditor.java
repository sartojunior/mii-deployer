/*******************************************************************************
 * Copyright (c) 2004, 2005 Elias Volanakis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Elias Volanakis - initial API and implementation
�*******************************************************************************/
package org.asem.eclipse.mii.model.editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.EventObject;

import javax.xml.parsers.DocumentBuilderFactory;

import org.asem.eclipse.mii.model.abs.LoadFactory;
import org.asem.eclipse.mii.model.abs.ShapeConstants;
import org.asem.eclipse.mii.model.abs.ShapesFactory;
import org.asem.eclipse.mii.model.parts.ShapesEditPartFactory;
import org.asem.eclipse.mii.model.parts.ShapesTreeEditPartFactory;
import org.asem.eclipse.mii.model.shapes.ShapesDiagram;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSSerializer;


/**
 * A graphical editor with flyout palette that can edit .shapes files. The binding between the .shapes file extension
 * and this editor is done in plugin.xml
 * 
 * @author Elias Volanakis
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ShapesEditor extends GraphicalEditorWithFlyoutPalette {

    /** This is the root of the editor's model. */
    private ShapesDiagram diagram;
    /** Palette component, holding the tools and shapes. */
    private static PaletteRoot PALETTE_MODEL;

    /** Create a new ShapesEditor instance. This is called by the Workspace. */
    public ShapesEditor() {
        setEditDomain(new DefaultEditDomain(this));
    }

    /**
     * Configure the graphical viewer before it receives contents.
     * <p>
     * This is the place to choose an appropriate RootEditPart and EditPartFactory for your editor. The RootEditPart
     * determines the behavior of the editor's "work-area". For example, GEF includes zoomable and scrollable root edit
     * parts. The EditPartFactory maps model elements to edit parts (controllers).
     * </p>
     * 
     * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
     */
    protected void configureGraphicalViewer() {
        super.configureGraphicalViewer();

        GraphicalViewer viewer = getGraphicalViewer();
        viewer.setEditPartFactory(new ShapesEditPartFactory());
        viewer.setRootEditPart(new ScalableFreeformRootEditPart());
        viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer));

        // configure the context menu provider
        ContextMenuProvider cmProvider = new ShapesEditorContextMenuProvider(viewer, getActionRegistry());
        viewer.setContextMenu(cmProvider);
        getSite().registerContextMenu(cmProvider, viewer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.ui.parts.GraphicalEditor#commandStackChanged(java.util .EventObject)
     */
    public void commandStackChanged(EventObject event) {
        firePropertyChange(IEditorPart.PROP_DIRTY);
        super.commandStackChanged(event);
    }

    private void createOutputStream(OutputStream os) throws IOException {

        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element el = diagram.store(doc);
            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            DOMImplementationLS impl = (DOMImplementationLS)registry.getDOMImplementation("LS");
            
            LSOutput out = impl.createLSOutput();
            out.setByteStream(os);
            out.setEncoding(ShapeConstants.ENCODING);
            
            LSSerializer writer = impl.createLSSerializer();
            writer.write(el, out);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette# createPaletteViewerProvider()
     */
    protected PaletteViewerProvider createPaletteViewerProvider() {
        return new PaletteViewerProvider(getEditDomain()) {
            protected void configurePaletteViewer(PaletteViewer viewer) {
                super.configurePaletteViewer(viewer);
                // create a drag source listener for this palette viewer
                // together with an appropriate transfer drop target listener,
                // this will enable
                // model element creation by dragging a
                // CombinatedTemplateCreationEntries
                // from the palette into the editor
                // @see ShapesEditor#createTransferDropTargetListener()
                viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
            }
        };
    }

    /**
     * Create a transfer drop target listener. When using a CombinedTemplateCreationEntry tool in the palette, this will
     * enable model element creation by dragging from the palette.
     * 
     * @see #createPaletteViewerProvider()
     */
    private TransferDropTargetListener createTransferDropTargetListener() {
        return new TemplateTransferDropTargetListener(getGraphicalViewer()) {
            protected CreationFactory getFactory(Object template) {
                return ShapesFactory.getInstance((Class)template);
            }
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor )
     */
    public void doSave(IProgressMonitor monitor) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            createOutputStream(out);
            IFile file = ((IFileEditorInput) getEditorInput()).getFile();
            file.setContents(
                    new ByteArrayInputStream(out.toByteArray()), 
                    true,
                    false,
                    monitor);
            getCommandStack().markSaveLocation();
        }
        catch (CoreException ce) {
            ce.printStackTrace();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.ISaveablePart#doSaveAs()
     */
    public void doSaveAs() {
        // Show a SaveAs dialog
        Shell shell = getSite().getWorkbenchWindow().getShell();
        SaveAsDialog dialog = new SaveAsDialog(shell);
        dialog.setOriginalFile(((IFileEditorInput) getEditorInput()).getFile());
        dialog.open();

        IPath path = dialog.getResult();
        if (path != null) {
            // try to save the editor's contents under a different file name
            final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
            try {
                new ProgressMonitorDialog(shell).run(false, // don't fork
                        false, // not cancelable
                        new WorkspaceModifyOperation() { // run this operation
                            public void execute(final IProgressMonitor monitor) {
                                try {
                                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                                    createOutputStream(out);
                                    file.create(new ByteArrayInputStream(out.toByteArray()),
                                            true, 
                                            monitor);
                                }
                                catch (CoreException ce) {
                                    ce.printStackTrace();
                                }
                                catch (IOException ioe) {
                                    ioe.printStackTrace();
                                }
                            }
                        });
                // set input to the new file
                setInput(new FileEditorInput(file));
                getCommandStack().markSaveLocation();
            }
            catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            catch (InvocationTargetException ite) {
                ite.printStackTrace();
            }
        }
    }

    public Object getAdapter(Class type) {
        if (type == IContentOutlinePage.class)
            return new ShapesOutlinePage(new TreeViewer());
        return super.getAdapter(type);
    }

    ShapesDiagram getModel() {
        return diagram;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#getPaletteRoot ()
     */
    protected PaletteRoot getPaletteRoot() {
        if (PALETTE_MODEL == null)
            PALETTE_MODEL = ShapesEditorPaletteFactory.createPalette();
        return PALETTE_MODEL;
    }

    private void handleLoadException(Exception e) {
        System.err.println("** Load failed. Using default model. **");
        e.printStackTrace();
        diagram = new ShapesDiagram();
    }

    /**
     * Set up the editor's inital content (after creation).
     * 
     * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#initializeGraphicalViewer()
     */
    protected void initializeGraphicalViewer() {
        super.initializeGraphicalViewer();
        GraphicalViewer viewer = getGraphicalViewer();
        viewer.setContents(getModel()); // set the contents of this editor

        // listen for dropped parts
        viewer.addDropTargetListener(createTransferDropTargetListener());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
     */
    public boolean isSaveAsAllowed() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
     */
    protected void setInput(IEditorInput input) {
        super.setInput(input);
        ShapesFactory.setCurrentEditor (this);
        
        try {
            IFile file = ((IFileEditorInput) input).getFile();
            InputStream in = file.getContents();
            
            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            DOMImplementationLS impl = (DOMImplementationLS)registry.getDOMImplementation("LS");
            
            LSInput inp = impl.createLSInput();
            inp.setByteStream(in);
            inp.setEncoding(ShapeConstants.ENCODING);

            LSParser lParser = impl.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, null);
            Document doc = lParser.parse(inp);

            diagram = new ShapesDiagram();
            diagram.restore((Element)doc.getElementsByTagName(ShapeConstants.DIAGRAM_ELEMENT).item(0));
            in.close();
            setPartName(file.getName());
        }
        catch (Exception e) {
            handleLoadException(e);
        }
        finally {
            LoadFactory.clear(); 
        }
    }

    /**
     * Creates an outline pagebook for this editor.
     */
    public class ShapesOutlinePage extends ContentOutlinePage {
        /**
         * Create a new outline page for the shapes editor.
         * 
         * @param viewer
         *            a viewer (TreeViewer instance) used for this outline page
         * @throws IllegalArgumentException
         *             if editor is null
         */
        public ShapesOutlinePage(EditPartViewer viewer) {
            super(viewer);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite )
         */
        public void createControl(Composite parent) {
            // create outline viewer page
            getViewer().createControl(parent);
            // configure outline viewer
            getViewer().setEditDomain(getEditDomain());
            getViewer().setEditPartFactory(new ShapesTreeEditPartFactory());
            // configure & add context menu to viewer
            ContextMenuProvider cmProvider = new ShapesEditorContextMenuProvider(getViewer(), getActionRegistry());
            getViewer().setContextMenu(cmProvider);
            getSite().registerContextMenu("org.eclipse.gef.examples.shapes.outline.contextmenu", cmProvider,
                    getSite().getSelectionProvider());
            // hook outline viewer
            getSelectionSynchronizer().addViewer(getViewer());
            // initialize outline viewer with model
            getViewer().setContents(getModel());
            // show outline viewer
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.part.IPage#dispose()
         */
        public void dispose() {
            ShapesFactory.removeEditror(ShapesEditor.this);
            // unhook outline viewer
            getSelectionSynchronizer().removeViewer(getViewer());
            // dispose
            super.dispose();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.part.IPage#getControl()
         */
        public Control getControl() {
            ShapesFactory.setCurrentEditor (ShapesEditor.this);
            return getViewer().getControl();
        }

        /**
         * @see org.eclipse.ui.part.IPageBookViewPage#init(org.eclipse.ui.part.IPageSite)
         */
        public void init(IPageSite pageSite) {
            super.init(pageSite);
            ActionRegistry registry = getActionRegistry();
            IActionBars bars = pageSite.getActionBars();
            String id = ActionFactory.UNDO.getId();
            bars.setGlobalActionHandler(id, registry.getAction(id));
            id = ActionFactory.REDO.getId();
            bars.setGlobalActionHandler(id, registry.getAction(id));
            id = ActionFactory.DELETE.getId();
            bars.setGlobalActionHandler(id, registry.getAction(id));
        }
    }

    public ShapesDiagram getDiagram() {
        return diagram;
    }
}