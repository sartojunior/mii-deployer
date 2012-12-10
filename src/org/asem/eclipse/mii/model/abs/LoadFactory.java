package org.asem.eclipse.mii.model.abs;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

@SuppressWarnings("unchecked")
public final class LoadFactory {
    public static Map<String, IXMLElement>   loadedObjects = new HashMap<String, IXMLElement>();

    private LoadFactory() {
    }

    public static IXMLElement getLoadedObject (String clazz, String name) {
        return loadedObjects.get(clazz + ":" + name);
    }

    public synchronized static IXMLElement loadObject (Element el) {
        String className = el.getAttribute(ShapeConstants.ELEMENT_CLASS);
        String name = el.getAttribute(ShapeConstants.NAME);

        /*
         * First of all try to find in loaded files
         */
        IXMLElement found = getLoadedObject (className, name);
        if (found != null)
            return found;

        try {
            Class<IXMLElement> cl = (Class<IXMLElement>)Class.forName(className);
            IXMLElement xmlEl = cl.newInstance();
            loadedObjects.put(className + ":" + name, xmlEl);
            return xmlEl;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public synchronized static void clear () {
        loadedObjects.clear();
    }
}
