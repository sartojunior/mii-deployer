package org.asem.eclipse.mii.model.shapes;

import java.util.ArrayList;
import java.util.List;

import org.asem.eclipse.mii.model.abs.AbstractModelElement;
import org.asem.eclipse.mii.model.abs.AbstractModelShape;
import org.asem.eclipse.mii.model.abs.IXMLElement;
import org.asem.eclipse.mii.model.abs.LoadFactory;
import org.asem.eclipse.mii.model.abs.ShapeConstants;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * A container for multiple shapes. This is the "root" of the model data
 * structure.
 * 
 * @author ASementsov
 */
@SuppressWarnings({"rawtypes"})
public class ShapesDiagram extends AbstractModelElement {
	private static final long serialVersionUID = 1;
	private List<AbstractModelShape> shapes = new ArrayList<AbstractModelShape>();
	
	public ShapesDiagram () {
	    
	}

	/**
	 * Add a shape to this diagram.
	 * 
	 * @param s
	 *            a non-null shape instance
	 * @return true, if the shape was added, false otherwise
	 */
	public boolean addChild(AbstractModelShape s) {
		if (s != null && shapes.add(s)) {
			firePropertyChange(ShapeConstants.CHILD_ADDED_PROP, null, s);
			return true;
		}
		return false;
	}

	/**
	 * Return a List of Shapes in this diagram. The returned List should not be
	 * modified.
	 */
	public List getChildren() {
		return shapes;
	}

	/**
	 * Remove a shape from this diagram.
	 * 
	 * @param shape - a non-null shape instance;
	 * @return true, if the shape was removed, false otherwise
	 */
	public boolean removeChild(AbstractModelShape shape) {
		if (shape != null && shapes.remove(shape)) {
			firePropertyChange(ShapeConstants.CHILD_REMOVED_PROP, null, shape);
			return true;
		}
		return false;
	}

    @Override
    public Element store(Document doc) {
        /*
         * Create element for the diagram
         */
        Element element = doc.createElement(ShapeConstants.DIAGRAM_ELEMENT);
        /*
         * Store class name attribute
         */
        element.setAttribute(ShapeConstants.ELEMENT_CLASS, getClass().getName());
        for (IPropertyDescriptor pd : getPropertyDescriptors()) {
            String attrName = (String) pd.getId();
            String value = (String) getPropertyValue(attrName);
            element.setAttribute(attrName, value);
        }
        
        /*
         * Store nested shapes
         */
        for (AbstractModelShape shape : shapes) {
            Element shapeElem = shape.store(doc);
            element.appendChild(shapeElem);
        }

        return element;
    }

    @Override
    public void restore(Element element) {
        if (getClass().getName().equals(element.getAttribute(ShapeConstants.ELEMENT_CLASS))) {
            NamedNodeMap attrs = element.getAttributes();
            for (int i = 0; i < attrs.getLength(); i++) {
                String attrName = attrs.item(i).getLocalName();
                String value = attrs.item(i).getNodeValue();
                setPropertyValue(attrName, value);
            }
            
            NodeList nodeList = element.getChildNodes();
            for (int i=0;i<nodeList.getLength();i++) {
                Node node = nodeList.item(i);
                if (node instanceof Element) {
                    Element el = (Element)node;
                    IXMLElement xmlEl = LoadFactory.loadObject(el);
                    if (xmlEl == null)
                        continue;
            
                    xmlEl.restore(el);
                    shapes.add((AbstractModelShape)xmlEl);
                }
            }
        }
        else {
            throw (new RuntimeException("Invalid class: " + element.getAttribute(ShapeConstants.ELEMENT_CLASS) + ", expecting " + getClass().getName()));
        }
    }

    private static IPropertyDescriptor[] pd = new IPropertyDescriptor[] {
        new TextPropertyDescriptor(ShapeConstants.APPNAME, ShapeConstants.APPNAME)
    };
    
    private String appName = "";
    
    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return pd;
    }

    @Override
    public Object getPropertyValue(Object id) {
        if (ShapeConstants.APPNAME.equals(id)) {
            return appName;
        }
        return null;
    }

    @Override
    public void setPropertyValue(Object id, Object value) {
        if (ShapeConstants.APPNAME.equals(id)) {
            appName = (String)value;
        }
    }
}