package org.asem.eclipse.mii.model.abs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.asem.eclipse.mii.model.editor.ShapesEditor;
import org.eclipse.gef.requests.CreationFactory;


public class ShapesFactory implements CreationFactory {
    private Class<? extends AbstractModelElement>                           clazz;
    private List<AbstractModelElement>                                      elements = new ArrayList<AbstractModelElement>();
    private static final Map<ShapesEditor, Map<Class<?>, ShapesFactory>>    factories = new HashMap<ShapesEditor, Map<Class<?>,ShapesFactory>>();
    private static ShapesEditor                                             currentEditor;

    public synchronized static void setCurrentEditor(ShapesEditor viewer) {
       currentEditor = viewer;
    }
    
    public static void removeEditror (ShapesEditor viewer) {
        synchronized(factories) {
            factories.remove(viewer);
        }
    }
    
    public static final ShapesFactory getInstance (Class<? extends AbstractModelElement> clazz) {
        Map<Class<?>, ShapesFactory> fmap = factories.get(currentEditor);
        if (fmap == null) {
            fmap = new HashMap<Class<?>, ShapesFactory>();
            factories.put(currentEditor, fmap);
        }

        ShapesFactory fact = fmap.get(clazz);
        if (fact == null) {
            fact = new ShapesFactory(clazz);
            fmap.put(clazz, fact);
        }

        return fact;
    }

    protected ShapesFactory (Class<? extends AbstractModelElement> clazz) {
        this.clazz = clazz;
    }

    public boolean containsName (String name) {
        for (AbstractModelElement el : elements) {
            String elName = (String) el.getPropertyValue(ShapeConstants.NAME);
            if (name.equals(elName))
                return true;
        }

        return false;
    }

    public void addShape (AbstractModelElement shape) {
        if (!shape.getClass().equals(clazz)) 
            return;

        String name =  (String) shape.getPropertyValue(ShapeConstants.NAME);
        if (containsName(name)) {
            System.out.println (name + ": name already exists");
            return;
        }

        elements.add(shape);
    }

    public void remove (AbstractModelElement shape) {
        assert (!shape.getClass().equals(clazz));
        elements.remove(shape);
    }

    @Override
    public Object getNewObject() {
        int number = elements.size();
        String name = clazz.getSimpleName() + number;

        while (containsName(name)) {
            number++;
            name = clazz.getSimpleName() + number;
        }

        try {
            AbstractModelElement elem = clazz.newInstance();

            if (elem instanceof IShape) {
                IShape sp = (IShape)elem;
                if (sp.hasWizard()) {
                    sp.invokeWizard();
                }
            }

            elem.setPropertyValue(ShapeConstants.NAME, name);
            elements.add(elem);
            return elem;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Object getObjectType() {
        return clazz.getSimpleName();
    }

    public List<AbstractModelElement> getElements() {
        return elements;
    }
}
