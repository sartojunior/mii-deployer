package org.asem.eclipse.mii.model.shapes;

import org.asem.eclipse.mii.model.abs.IXMLElement;
import org.asem.eclipse.mii.model.abs.ShapeConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



public class COLUMN implements IXMLElement {
    public String name;
    public String type;
    
    public COLUMN () {
        
    }
    
    public COLUMN(String name, String type) {
        this.name = name;
        this.type = type;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        COLUMN other = (COLUMN) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        }
        else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public Element store(Document doc) {
        Element element = doc.createElement(ShapeConstants.COLUMN_ELEMENT);
        element.setAttribute(ShapeConstants.ELEMENT_CLASS, getClass().getName());
        element.setAttribute(ShapeConstants.NAME, name);
        element.setAttribute(ShapeConstants.TYPE, type);
        return element;
    }

    @Override
    public void restore(Element element) {
        name = element.getAttribute(ShapeConstants.NAME);
        type = element.getAttribute(ShapeConstants.TYPE);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    
}
