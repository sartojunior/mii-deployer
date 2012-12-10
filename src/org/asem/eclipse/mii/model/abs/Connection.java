package org.asem.eclipse.mii.model.abs;

import org.eclipse.draw2d.Graphics;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A connection between two distinct shapes.
 * 
 * @author Elias Volanakis
 */
public class Connection extends AbstractModelElement {
    private static final IPropertyDescriptor[] descriptors = new IPropertyDescriptor[1];
    private static final long serialVersionUID = 1;

    /** 
     * True, if the connection is attached to its endpoints. 
     */
    private boolean isConnected;
    /** 
     * Line drawing style for this connection. 
     */
    private int lineStyle = Graphics.LINE_SOLID;
    /** 
     * Connection's source endpoint. 
     */
    private AbstractModelShape source;
    /** 
     * Connection's target endpoint. 
     */
    private AbstractModelShape target;

    static {
        descriptors[0] = new ComboBoxPropertyDescriptor(
                ShapeConstants.LINESTYLE_PROP, 
                ShapeConstants.LINESTYLE_PROP, 
                new String[] { ShapeConstants.SOLID_STR, ShapeConstants.DASHED_STR });
    }

    public Connection () {
    }
    
    /**
     * Create a (solid) connection between two distinct shapes.
     * 
     * @param source
     *            a source endpoint for this connection (non null)
     * @param target
     *            a target endpoint for this connection (non null)
     * @throws IllegalArgumentException
     *             if any of the parameters are null or source == target
     * @see #setLineStyle(int)
     */
    public Connection(AbstractModelShape source, AbstractModelShape target) {
        reconnect(source, target);
    }

    /**
     * Disconnect this connection from the shapes it is attached to.
     */
    public void disconnect() {
        if (isConnected) {
            source.removeConnection(this);
            target.removeConnection(this);
            isConnected = false;
        }
    }

    /**
     * Returns the line drawing style of this connection.
     * 
     * @return an int value (Graphics.LINE_DASH or Graphics.LINE_SOLID)
     */
    public int getLineStyle() {
        return lineStyle;
    }

    /**
     * Returns the descriptor for the lineStyle property
     * 
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return descriptors;
    }

    /**
     * Returns the lineStyle as String for the Property Sheet
     * 
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
     */
    public Object getPropertyValue(Object id) {
        if (id.equals(ShapeConstants.LINESTYLE_PROP)) {
            if (getLineStyle() == Graphics.LINE_DASH)
                // Dashed is the second value in the combo dropdown
                return new Integer(1);
            // Solid is the first value in the combo dropdown
            return new Integer(0);
        }
        return super.getPropertyValue(id);
    }

    /**
     * Returns the source endpoint of this connection.
     * 
     * @return a non-null Shape instance
     */
    public AbstractModelShape getSource() {
        return source;
    }

    /**
     * Returns the target endpoint of this connection.
     * 
     * @return a non-null Shape instance
     */
    public AbstractModelShape getTarget() {
        return target;
    }

    /**
     * Reconnect this connection. The connection will reconnect with the shapes it was previously attached to.
     */
    public void reconnect() {
        if (!isConnected) {
            source.addConnection(this);
            target.addConnection(this);
            isConnected = true;
        }
    }

    /**
     * Reconnect to a different source and/or target shape. The connection will disconnect from its current attachments
     * and reconnect to the new source and target.
     * 
     * @param newSource
     *            a new source endpoint for this connection (non null)
     * @param newTarget
     *            a new target endpoint for this connection (non null)
     * @throws IllegalArgumentException
     *             if any of the paramers are null or newSource == newTarget
     */
    public void reconnect(AbstractModelShape newSource, AbstractModelShape newTarget) {
        if (newSource == null || newTarget == null || newSource == newTarget) {
            throw new IllegalArgumentException();
        }
        disconnect();
        this.source = newSource;
        this.target = newTarget;
        reconnect();
    }

    /**
     * Set the line drawing style of this connection.
     * 
     * @param lineStyle
     *            one of following values: Graphics.LINE_DASH or Graphics.LINE_SOLID
     * @see Graphics#LINE_DASH
     * @see Graphics#LINE_SOLID
     * @throws IllegalArgumentException
     *             if lineStyle does not have one of the above values
     */
    public void setLineStyle(int lineStyle) {
        if (lineStyle != Graphics.LINE_DASH && lineStyle != Graphics.LINE_SOLID) {
            throw new IllegalArgumentException();
        }
        this.lineStyle = lineStyle;
        firePropertyChange(ShapeConstants.LINESTYLE_PROP, null, new Integer(this.lineStyle));
    }

    /**
     * Sets the lineStyle based on the String provided by the PropertySheet
     * 
     * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
     */
    public void setPropertyValue(Object id, Object value) {
        if (id.equals(ShapeConstants.LINESTYLE_PROP))
            setLineStyle(new Integer(1).equals(value) ? Graphics.LINE_DASH : Graphics.LINE_SOLID);
        else
            super.setPropertyValue(id, value);
    }

    @Override
    public Element store(Document doc) {
        /*
         * Create element for the diagram
         */
        Element element = doc.createElement(ShapeConstants.CONNECTION_ELEMENT);
        /*
         * Store class name attribute
         */
        element.setAttribute(ShapeConstants.ELEMENT_CLASS, getClass().getName());
        element.setAttribute(ShapeConstants.LINESTYLE_PROP, String.valueOf(lineStyle));

        element.setAttribute(ShapeConstants.SOURCE, (String)source.getPropertyValue(ShapeConstants.NAME));
        element.setAttribute(ShapeConstants.TARGET, (String)target.getPropertyValue(ShapeConstants.NAME));
        element.setAttribute(ShapeConstants.NAME, source.getPropertyValue(ShapeConstants.NAME) + "To" + target.getPropertyValue(ShapeConstants.NAME));

        return element;
    }

    @Override
    public void restore(Element element) {
        if (getClass().getName().equals(element.getAttribute(ShapeConstants.ELEMENT_CLASS))) {
            String ls = element.getAttribute(ShapeConstants.LINESTYLE_PROP);
            if (ls != null)
                lineStyle = Integer.valueOf(ls);
        }
        else {
            throw (new RuntimeException("Invalid class: " + element.getAttribute(ShapeConstants.ELEMENT_CLASS) + ", expecting " + getClass().getName()));
        }
    }

    public void setSource(AbstractModelShape source) {
        this.source = source;
    }

    public void setTarget(AbstractModelShape target) {
        this.target = target;
    }
}
