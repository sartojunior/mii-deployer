package org.asem.eclipse.mii.model.abs;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface IXMLElement {
    Element store (Document doc);
    void restore (Element element);
}
