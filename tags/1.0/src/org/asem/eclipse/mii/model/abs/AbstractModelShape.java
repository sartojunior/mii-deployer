package org.asem.eclipse.mii.model.abs;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.asem.eclipse.mii.model.ShapesPlugin;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Abstract prototype of a shape. Has a size (width and height), a location (x and y position) and a list of incoming
 * and outgoing connections. Use subclasses to instantiate a specific shape.
 * 
 * @author ASementsov
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class AbstractModelShape extends AbstractModelElement implements IShape {
    private static final long serialVersionUID = 1;

    protected String getLocalizedString(String key) {
        try {
            return ResourceBundle.getBundle(AbstractModelShape.class.getName()).getString(key);
        }
        catch (MissingResourceException e) {
            return "!" + key + "!";
        }
    }

    /**
     * A list of properties for this shape
     */
    private Map<String, Object> properties = new HashMap<String, Object>();
    /**
     * A array of property descriptors. There is one IPropertyDescriptor entry per editable property.
     * 
     * @see #getPropertyDescriptors()
     * @see #getPropertyValue(Object)
     * @see #setPropertyValue(Object, Object)
     */
    private transient IPropertyDescriptor[] descriptors;
    /**
     * Location of this shape.
     */
    private Point location = new Point(0, 0);
    /**
     * Size of this shape.
     */
    private Dimension size = new Dimension(50, 50);
    /**
     * List of outgoing Connections.
     */
    private List<Connection> sourceConnections = new ArrayList();
    /**
     * List of incoming Connections.
     */
    private List<Connection> targetConnections = new ArrayList();

    /**
     * Function initializes the properties map and properties descriptor
     * 
     * @param propertiesToAdd
     *            - additional properties
     */
    protected void init(Map<String, Object> propertiesToAdd) {
        properties.put(ShapeConstants.HEIGHT_PROP, String.valueOf(size.height));
        properties.put(ShapeConstants.WIDTH_PROP, String.valueOf(size.width));
        properties.put(ShapeConstants.XPOS_PROP, String.valueOf(location.x));
        properties.put(ShapeConstants.YPOS_PROP, String.valueOf(location.y));
        /*
         * put additional properties
         */
        if (propertiesToAdd != null)
            properties.putAll(propertiesToAdd);

        descriptors = new IPropertyDescriptor[properties.size()];
        int i = 0;
        for (Entry<String, Object> entry : properties.entrySet()) {
            descriptors[i] = new TextPropertyDescriptor(entry.getKey(), getLocalizedString(entry.getKey()));
            if (entry.getValue() instanceof Number) {
                ((TextPropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
                    public String isValid(Object value) {
                        int intValue = -1;
                        if (value instanceof String) {
                            try {
                                intValue = Integer.parseInt((String) value);
                            }
                            catch (NumberFormatException exc) {
                                return "Not a number";
                            }
                        }
                        else if (value instanceof Number) {
                            intValue = ((Number) value).intValue();
                        }

                        return (intValue >= 0) ? null : "Value must be >=  0";
                    }
                });
            }

            i++;
        }
    }

    protected static Image createImage(String name) {
        InputStream stream = ShapesPlugin.class.getResourceAsStream(name);
        if (stream == null)
            return null;

        Image image = new Image(null, stream);
        try {
            stream.close();
        }
        catch (IOException ioe) {
        }
        return image;
    }

    /**
     * Add an incoming or outgoing connection to this shape.
     * 
     * @param conn
     *            a non-null connection instance
     * @throws IllegalArgumentException
     *             if the connection is null or has not distinct endpoints
     */
    void addConnection(Connection conn) {
        if (conn == null || conn.getSource() == conn.getTarget()) {
            throw new IllegalArgumentException();
        }

        if (conn.getSource() == this) {
            sourceConnections.add(conn);
            firePropertyChange(ShapeConstants.SOURCE_CONNECTIONS_PROP, null, conn);
        }
        else if (conn.getTarget() == this) {
            targetConnections.add(conn);
            firePropertyChange(ShapeConstants.TARGET_CONNECTIONS_PROP, null, conn);
        }
    }

    /**
     * Return a pictogram (small icon) describing this model element. Children should override this method and return an
     * appropriate Image.
     * 
     * @return a 16x16 Image or null
     */
    public abstract Image getIcon();

    /**
     * Return the Location of this shape.
     * 
     * @return a non-null location instance
     */
    public Point getLocation() {
        return location.getCopy();
    }

    /**
     * Returns an array of IPropertyDescriptors for this shape.
     * <p>
     * The returned array is used to fill the property view, when the edit-part corresponding to this model element is
     * selected.
     * </p>
     * 
     * @see #descriptors
     * @see #getPropertyValue(Object)
     * @see #setPropertyValue(Object, Object)
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return descriptors;
    }

    /**
     * Return the property value for the given propertyId, or null.
     * <p>
     * The property view uses the IDs from the IPropertyDescriptors array to obtain the value of the corresponding
     * properties.
     * </p>
     * 
     * @see #descriptors
     * @see #getPropertyDescriptors()
     */
    public Object getPropertyValue(Object propertyId) {
        return properties.get(propertyId);
    }

    /**
     * Return the Size of this shape.
     * 
     * @return a non-null Dimension instance
     */
    public Dimension getSize() {
        return size.getCopy();
    }

    /**
     * Return a List of outgoing Connections.
     */
    public List<Connection> getSourceConnections() {
        return new ArrayList(sourceConnections);
    }

    /**
     * Return a List of incoming Connections.
     */
    public List<Connection> getTargetConnections() {
        return new ArrayList(targetConnections);
    }

    /**
     * Remove an incoming or outgoing connection from this shape.
     * 
     * @param conn
     *            a non-null connection instance
     * @throws IllegalArgumentException
     *             if the parameter is null
     */
    void removeConnection(Connection conn) {
        if (conn == null) {
            throw new IllegalArgumentException();
        }

        if (conn.getSource() == this) {
            sourceConnections.remove(conn);
            firePropertyChange(ShapeConstants.SOURCE_CONNECTIONS_PROP, null, conn);
        }
        else if (conn.getTarget() == this) {
            targetConnections.remove(conn);
            firePropertyChange(ShapeConstants.TARGET_CONNECTIONS_PROP, null, conn);
        }
    }

    /**
     * Set the Location of this shape.
     * 
     * @param newLocation
     *            a non-null Point instance
     * @throws IllegalArgumentException
     *             if the parameter is null
     */
    public void setLocation(Point newLocation) {
        if (newLocation == null) {
            throw new IllegalArgumentException();
        }

        location.setLocation(newLocation);
        properties.put(ShapeConstants.XPOS_PROP, String.valueOf(location.x));
        properties.put(ShapeConstants.YPOS_PROP, String.valueOf(location.y));

        firePropertyChange(ShapeConstants.LOCATION_PROP, null, location);
    }

    /**
     * Set the property value for the given property id. If no matching id is found, the call is forwarded to the
     * superclass.
     * <p>
     * The property view uses the IDs from the IPropertyDescriptors array to set the values of the corresponding
     * properties.
     * </p>
     * 
     * @see #descriptors
     * @see #getPropertyDescriptors()
     */
    public void setPropertyValue(Object propertyId, Object value) {
        if (ShapeConstants.XPOS_PROP.equals(propertyId)) {
            int x = Integer.parseInt((String) value);
            setLocation(new Point(x, location.y));
        }
        else if (ShapeConstants.YPOS_PROP.equals(propertyId)) {
            int y = Integer.parseInt((String) value);
            setLocation(new Point(location.x, y));
        }
        else if (ShapeConstants.HEIGHT_PROP.equals(propertyId)) {
            int height = Integer.parseInt((String) value);
            setSize(new Dimension(size.width, height));
        }
        else if (ShapeConstants.WIDTH_PROP.equals(propertyId)) {
            int width = Integer.parseInt((String) value);
            setSize(new Dimension(width, size.height));
        }
        else {
            Object oldValue = properties.get(propertyId);
            properties.put((String) propertyId, value);
            firePropertyChange(ShapeConstants.LOCATION_PROP, oldValue, value);
        }
    }

    /**
     * Set the Size of this shape. Will not modify the size if newSize is null.
     * 
     * @param newSize a non-null Dimension instance or null
     */
    public void setSize(Dimension newSize) {
        if (newSize != null) {
            size.setSize(newSize);

            properties.put(ShapeConstants.HEIGHT_PROP, String.valueOf(size.height));
            properties.put(ShapeConstants.WIDTH_PROP, String.valueOf(size.width));

            firePropertyChange(ShapeConstants.SIZE_PROP, null, size);
        }
    }

    protected Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public boolean hasWizard() {
        return false;
    }

    @Override
    public void invokeWizard() {
    }
    
    @Override
    public Element store(Document doc) {
        /*
         * Create element for the diagram
         */
        Element element = doc.createElement(ShapeConstants.SHAPE_ELEMENT);
        /*
         * Store class name attribute
         */
        element.setAttribute(ShapeConstants.ELEMENT_CLASS, getClass().getName());
        for (Entry<String, Object> entry : getProperties().entrySet()) {
            element.setAttribute(entry.getKey(), String.valueOf(entry.getValue()));
        }

        for (Connection src : getSourceConnections()) {
            Element con = src.store(doc);
            con.setAttribute(ShapeConstants.RELATION, ShapeConstants.SOURCE);
            element.appendChild(con);
        }

        for (Connection trg : getTargetConnections()) {
            Element con = trg.store(doc);
            con.setAttribute(ShapeConstants.RELATION, ShapeConstants.TARGET);
            element.appendChild(con);
        }
        
        return element;
    }
    
    @Override
    public void restore(Element element) {
        super.restore(element);
        
        if (getClass().getName().equals(element.getAttribute(ShapeConstants.ELEMENT_CLASS))) {
            NamedNodeMap attrs = element.getAttributes();
            for (int i = 0; i < attrs.getLength(); i++) {
                String attrName = attrs.item(i).getLocalName();
                String value = attrs.item(i).getNodeValue();
                setPropertyValue(attrName, value);
            }

            /*
             * Connections or other children
             */
            NodeList nodeList = element.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node instanceof Element) {
                    Element el = (Element) node;
                    IXMLElement xmlEl = LoadFactory.loadObject(el);
                    if (xmlEl == null)
                        continue;

                    xmlEl.restore(el);
                    if (xmlEl instanceof Connection) {
                        /*
                         * Check relation of the connection
                         */
                        String relation = el.getAttribute(ShapeConstants.RELATION);
                        Connection con = (Connection) xmlEl;
    
                        if (ShapeConstants.SOURCE.equals(relation)) {
                            con.setSource(this);
                        }
                        else {
                            con.setTarget(this);
                        }
    
                        if (con.getTarget() != null && con.getSource() != null)
                            con.reconnect();
                    }
                    else {
                        addCustomObject(xmlEl);
                    }
                }
            }
        }
        else {
            throw (new RuntimeException("Invalid class: " + element.getAttribute(ShapeConstants.ELEMENT_CLASS)
                    + ", expecting " + getClass().getName()));
        }
    }

    protected void addCustomObject(IXMLElement obj) {}
}